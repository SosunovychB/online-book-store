package book.store.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @BeforeEach
    public void beforeEach(@Autowired @NotNull DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/categories/add-categories-to-categories-table.sql"));
        } catch (SQLException e) {
            throw new RuntimeException("Error creating up database", e);
        }
    }

    @AfterEach
    public void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get all categories in pages")
    public void getAll_ValidPageable_ReturnsCategoryDtoList() throws Exception {
        //given
        List<CategoryDto> expectedCategoryDtoList = new ArrayList<>();
        expectedCategoryDtoList.add(new CategoryDto().setId(1L).setName("Category 1")
                .setDescription("Description for Category 1"));
        expectedCategoryDtoList.add(new CategoryDto().setId(2L).setName("Category 2")
                .setDescription("Description for Category 2"));
        expectedCategoryDtoList.add(new CategoryDto().setId(3L).setName("Category 3")
                .setDescription("Description for Category 3"));

        //when
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<CategoryDto> actualCategoryDtoList = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), new TypeReference<List<CategoryDto>>() {});

        assertEquals(expectedCategoryDtoList.size(), actualCategoryDtoList.size());
        assertEquals(expectedCategoryDtoList, actualCategoryDtoList);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get a category by id")
    public void getBookById_ValidRequestDto_ReturnsValidCategoryDto() throws Exception {
        //given
        Long categoryId = 2L;
        CategoryDto expectedCategoryDto = new CategoryDto().setId(2L).setName("Category 2")
                .setDescription("Description for Category 2");

        //when
        MvcResult result = mockMvc
                .perform(get("/api/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andReturn();

        //then
        CategoryDto actualCategoryDto = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto.class);

        assertEquals(expectedCategoryDto, actualCategoryDto);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get all books in a certain category")
    public void getBooksByCategoryId_ValidCategoryId_ReturnsValidCategoryDto(
            @Autowired @NotNull DataSource dataSource) throws Exception {
        //given
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-books-to-books-table.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error creating up database", e);
        }

        Long categoryId = 2L;
        BookDtoWithoutCategoryIds expectedBookDto = new BookDtoWithoutCategoryIds()
                .setId(2L)
                .setTitle("Book 2")
                .setAuthor("Author 2")
                .setIsbn("ISBN-2")
                .setPrice(new BigDecimal("15.75"))
                .setDescription("Description for Book 2")
                .setCoverImage("cover2.jpg");
        List<BookDtoWithoutCategoryIds> expectedBookDtoList = List.of(expectedBookDto);

        //when + delete books from db
        MvcResult result = mockMvc
                .perform(get("/api/categories/{categoryId}/books", categoryId))
                .andExpect(status().isOk())
                .andReturn();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/delete-books-from-books-table.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error creating up database", e);
        }

        //then
        List<BookDtoWithoutCategoryIds> actualBookDtoList = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<List<BookDtoWithoutCategoryIds>>() {});

        assertEquals(expectedBookDtoList, actualBookDtoList);
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Create a new category")
    public void createCategory_ValidRequest_ReturnsValidCategoryDto() throws Exception {
        //given
        CreateCategoryRequestDto createCategoryRequestDto = createCategoryRequestDto();
        CategoryDto expectedCategoryDto = createCategoryDto(createCategoryRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(createCategoryRequestDto);

        //when
        MvcResult result = mockMvc.perform(post("/api/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        CategoryDto actualCategoryDto = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto.class);

        assertNotNull(actualCategoryDto);
        assertNotNull(actualCategoryDto.getId());
        EqualsBuilder.reflectionEquals(expectedCategoryDto, actualCategoryDto, "id");
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Update a category by id")
    public void updateCategory_ValidRequest_ReturnsValidCategoryDto() throws Exception {
        //given
        Long categoryId = 2L;
        CreateCategoryRequestDto createCategoryRequestDto = createCategoryRequestDto();
        CategoryDto expectedCategoryDto = createCategoryDto(createCategoryRequestDto);
        expectedCategoryDto.setId(categoryId);

        String jsonRequest = objectMapper.writeValueAsString(createCategoryRequestDto);

        //when
        MvcResult result = mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        CategoryDto actualCategoryDto = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto.class);

        assertEquals(expectedCategoryDto, actualCategoryDto);
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Delete a category by id")
    public void deleteCategory_ValidCategory_Success() throws Exception {
        //given
        Long categoryId = 3L;

        //when + then
        MvcResult result = mockMvc
                .perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private static void teardown(@NotNull DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/categories/delete-categories-from-categories-table.sql"));
        } catch (SQLException e) {
            throw new RuntimeException("Error cleaning up database", e);
        }
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName("Category")
                .setDescription("Description");
    }

    private CategoryDto createCategoryDto(
            @NotNull CreateCategoryRequestDto createCategoryRequestDto) {
        return new CategoryDto()
                .setName(createCategoryRequestDto.getName())
                .setDescription(createCategoryRequestDto.getDescription());
    }
}
