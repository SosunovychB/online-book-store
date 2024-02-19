package book.store.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class BookControllerTest {
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/book/add-books-to-books-table.sql"));
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
    @DisplayName("Find all books")
    public void getAll_ValidPageable_ReturnsBookDtoList() throws Exception {
        //given
        Set<Long> categoryIdsForBook1 = new HashSet<>(List.of(1L));
        Set<Long> categoryIdsForBook2 = new HashSet<>(List.of(2L));
        Set<Long> categoryIdsForBook3 = new HashSet<>(List.of(3L));

        List<BookDto> expectedBookDtoList = new ArrayList<>();
        expectedBookDtoList.add(new BookDto().setId(1L).setTitle("Book 1")
                .setAuthor("Author 1").setIsbn("ISBN-1")
                .setPrice(new BigDecimal("10.50")).setCategoryIds(categoryIdsForBook1)
                .setDescription("Description for Book 1").setCoverImage("cover1.jpg"));
        expectedBookDtoList.add(new BookDto().setId(2L).setTitle("Book 2")
                .setAuthor("Author 2").setIsbn("ISBN-2")
                .setPrice(new BigDecimal("15.75")).setCategoryIds(categoryIdsForBook2)
                .setDescription("Description for Book 2").setCoverImage("cover2.jpg"));
        expectedBookDtoList.add(new BookDto().setId(3L).setTitle("Book 3")
                .setAuthor("Author 3").setIsbn("ISBN-3")
                .setPrice(new BigDecimal("20.00")).setCategoryIds(categoryIdsForBook3)
                .setDescription("Description for Book 3").setCoverImage("cover3.jpg"));

        //when
        MvcResult result = mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<BookDto> actualBookDtoList = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<List<BookDto>>() {});

        assertEquals(expectedBookDtoList.size(), actualBookDtoList.size());
        assertEquals(expectedBookDtoList, actualBookDtoList);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Search books by params")
    public void searchBooks_ValidParams_ReturnsBookDtoWithoutCategoryIdsList() throws Exception {
        //given
        BookDtoWithoutCategoryIds expectedBookDto = new BookDtoWithoutCategoryIds()
                .setId(2L)
                .setTitle("Book 2")
                .setAuthor("Author 2")
                .setIsbn("ISBN-2")
                .setPrice(new BigDecimal("15.75"))
                .setDescription("Description for Book 2")
                .setCoverImage("cover2.jpg");
        List<BookDtoWithoutCategoryIds> expectedBookDtoList = List.of(expectedBookDto);

        //when
        MvcResult result1 = mockMvc.perform(get("/api/books/search")
                        .param("titles","Book 2")
                )
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result2 = mockMvc.perform(get("/api/books/search")
                        .param("authors","Author 2")
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<BookDtoWithoutCategoryIds> actualBookDto1 = objectMapper.readValue(
                result1.getResponse().getContentAsByteArray(),
                new TypeReference<List<BookDtoWithoutCategoryIds>>() {});
        List<BookDtoWithoutCategoryIds> actualBookDto2 = objectMapper.readValue(
                result2.getResponse().getContentAsByteArray(),
                new TypeReference<List<BookDtoWithoutCategoryIds>>() {});

        assertEquals(expectedBookDtoList, actualBookDto1);
        assertEquals(expectedBookDtoList, actualBookDto2);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get a book by id")
    public void getBookById_ValidRequestDto_ReturnsValidBookDto() throws Exception {
        //given
        Long bookId = 2L;
        BookDto expectedBookDto = new BookDto()
                .setId(bookId)
                .setTitle("Book 2")
                .setAuthor("Author 2")
                .setIsbn("ISBN-2")
                .setPrice(new BigDecimal("15.75"))
                .setCategoryIds(Collections.singleton(2L))
                .setDescription("Description for Book 2")
                .setCoverImage("cover2.jpg");

        //when
        MvcResult result = mockMvc.perform(get("/api/books/{id}", bookId)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto actualBookDto = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto.class);

        assertEquals(expectedBookDto, actualBookDto);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    public void createBook_ValidRequestDto_ReturnsValidBookDto() throws Exception {
        //given
        CreateBookRequestDto createBookRequestDto = createBookRequestDto();
        BookDto expectedBookDto = createBookDtoFromCreateBookRequestDto(createBookRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);

        //when
        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto actualBookDto = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto.class);

        assertNotNull(actualBookDto);
        assertNotNull(actualBookDto.getId());
        EqualsBuilder.reflectionEquals(expectedBookDto, actualBookDto, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a book by id")
    public void updateBookDetails_ValidRequestDto_ReturnsValidBookDto() throws Exception {
        //given
        Long bookId = 2L;
        CreateBookRequestDto createBookRequestDto = createBookRequestDto();
        BookDto expectedBookDto = createBookDtoFromCreateBookRequestDto(createBookRequestDto);
        expectedBookDto.setId(bookId);

        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);

        //when
        MvcResult result = mockMvc.perform(put("/api/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto actualBookDto = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto.class);

        assertEquals(expectedBookDto, actualBookDto);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a book by id")
    public void delete_ValidBookId_Success() throws Exception {
        //given
        Long bookId = 3L;

        //when + then
        MvcResult result = mockMvc.perform(delete("/api/books/{id}", bookId)
                )
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/book/delete-books-from-books-table.sql"));
        } catch (SQLException e) {
            throw new RuntimeException("Error cleaning up database", e);
        }
    }

    private CreateBookRequestDto createBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("title")
                .setAuthor("author")
                .setIsbn("isbn")
                .setPrice(new BigDecimal("10.10"))
                .setCategoryIds(Collections.singleton(1L));
    }

    private BookDto createBookDtoFromCreateBookRequestDto(
            @NotNull CreateBookRequestDto createBookRequestDto) {
        return new BookDto()
                .setTitle(createBookRequestDto.getTitle())
                .setAuthor(createBookRequestDto.getAuthor())
                .setIsbn(createBookRequestDto.getIsbn())
                .setPrice(createBookRequestDto.getPrice())
                .setCategoryIds(createBookRequestDto.getCategoryIds());
    }
}
