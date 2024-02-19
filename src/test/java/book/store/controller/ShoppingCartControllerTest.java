package book.store.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.shopping.cart.AddBookToShoppingCartRequestDto;
import book.store.dto.shopping.cart.ShoppingCartDto;
import book.store.dto.shopping.cart.UpdateBookQuantityRequestDto;
import book.store.dto.shopping.cart.item.CartItemDto;
import book.store.model.Role;
import book.store.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
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
                    new ClassPathResource("database/user/add-users-to-users-tables.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/add-books-to-books-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping/cart/"
                            + "add-shopping-carts-to-shopping-carts-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping/cart/item/"
                            + "add-shopping-carts-items-to-cart-items-table.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error creating up database", e);
        }
    }

    @AfterEach
    public void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    @DisplayName("Retrieve user's shopping cart")
    public void retrieveShoppingCart_MockUserWithUserRole_ReturnsValidShoppingCartDto()
            throws Exception {
        //given
        User user = createUserWithId2();
        mockAuthentication(user);

        ShoppingCartDto expectedShoppingCartDto = createModelShoppingCartWithId2();
        CartItemDto defaultModelCartItemDto = createDefaultModelCartItemDtoWithId2();
        Set<CartItemDto> cartItemDtoSet = new HashSet<>();
        cartItemDtoSet.add(defaultModelCartItemDto);
        expectedShoppingCartDto.setCartItems(cartItemDtoSet);

        //when+then
        MvcResult result = mockMvc
                .perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ShoppingCartDto actualShoppingCartDto = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), ShoppingCartDto.class);

        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);
    }

    @Test
    @DisplayName("Add book to the shopping cart")
    public void addItemToShoppingCart_ValidAddBookRequestDto_ValidShoppingCartDto()
            throws Exception {
        //given
        User user = createUserWithId2();
        mockAuthentication(user);

        ShoppingCartDto expectedShoppingCartDto = createModelShoppingCartWithId2();
        CartItemDto defaultModelCartItemDto = createDefaultModelCartItemDtoWithId2();
        Set<CartItemDto> cartItemDtoSet = new HashSet<>();
        cartItemDtoSet.add(defaultModelCartItemDto);
        cartItemDtoSet.add(new CartItemDto()
                .setId(4L)
                .setBookId(3L)
                .setBookTitle("Book 3")
                .setQuantity(10));
        expectedShoppingCartDto.setCartItems(cartItemDtoSet);

        AddBookToShoppingCartRequestDto addBookToShoppingCartRequestDto =
                new AddBookToShoppingCartRequestDto()
                        .setBookId(3L)
                        .setQuantity(10);
        String jsonRequest = objectMapper.writeValueAsString(addBookToShoppingCartRequestDto);

        //when
        MvcResult result = mockMvc
                .perform(post("/api/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        ShoppingCartDto actualShoppingCartDto = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), ShoppingCartDto.class);

        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);

    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Update quantity of a book in the shopping cart")
    public void updateItemQuantity_ValidQuantityAndCartItemId_ReturnsValidShoppingCartDto()
            throws Exception {
        //given
        Long cartItemId = 2L;
        int newQuantity = 100;
        UpdateBookQuantityRequestDto updateBookQuantityRequestDto =
                new UpdateBookQuantityRequestDto()
                .setQuantity(newQuantity);
        String jsonRequest = objectMapper.writeValueAsString(updateBookQuantityRequestDto);

        ShoppingCartDto expectedShoppingCartDto = createModelShoppingCartWithId2();
        CartItemDto expectedCartItemDto = createModelCartItemDtoWithId2AndCertainQuantity(
                cartItemId, newQuantity);
        Set<CartItemDto> cartItemDtoSet = new HashSet<>();
        cartItemDtoSet.add(expectedCartItemDto);
        expectedShoppingCartDto.setCartItems(cartItemDtoSet);

        //when
        MvcResult result = mockMvc
                .perform(put("/api/cart/cart-items/{cartItemId}", cartItemId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        ShoppingCartDto actualShoppingCartDto = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), ShoppingCartDto.class);

        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Remove a book from the shopping cart")
    public void deleteItemFromShoppingCart_ValidShoppingCartId_Success() throws Exception {
        //given
        Long cartItemId = 2L;

        //when+then
        MvcResult result = mockMvc
                .perform(delete("/api/cart/cart-items/{cartItemId}", cartItemId))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private static void teardown(@NotNull DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping/cart/item/"
                            + "delete-shopping-carts-items-from-cart-items-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping/cart/"
                            + "delete-shopping-carts-from-shopping-carts-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/delete-books-from-books-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/delete-users-from-users-table.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error cleaning up database", e);
        }
    }

    private User createUserWithId2() {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role().setRoleName(Role.RoleName.ROLE_USER));
        return new User()
                .setId(2L)
                .setEmail("user2@example.com")
                .setRoles(roleSet);
    }

    private void mockAuthentication(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    private ShoppingCartDto createModelShoppingCartWithId2() {
        return new ShoppingCartDto()
                .setId(2L)
                .setUserId(2L);
    }

    private CartItemDto createDefaultModelCartItemDtoWithId2() {
        return new CartItemDto()
                .setId(2L)
                .setBookId(2L)
                .setBookTitle("Book 2")
                .setQuantity(20);
    }

    private CartItemDto createModelCartItemDtoWithId2AndCertainQuantity(
            Long cartItemId, int newQuantity) {
        return new CartItemDto()
                .setId(cartItemId)
                .setBookId(2L)
                .setBookTitle("Book 2")
                .setQuantity(newQuantity);
    }
}
