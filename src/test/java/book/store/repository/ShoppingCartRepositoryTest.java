package book.store.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.shopping.cart.ShoppingCartRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/user/add-users-to-users-tables.sql",
        "classpath:database/book/add-books-to-books-table.sql",
        "classpath:database/shopping/cart/add-shopping-carts-to-shopping-carts-table.sql",
        "classpath:database/shopping/cart/item/add-shopping-carts-items-to-cart-items-table.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database/shopping/cart/item/"
        + "delete-shopping-carts-items-from-cart-items-table.sql",
        "classpath:database/shopping/cart/delete-shopping-carts-from-shopping-carts-table.sql",
        "classpath:database/book/delete-books-from-books-table.sql",
        "classpath:database/user/delete-users-from-users-table.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Verify findShoppingCartByUserId() method works with valid user id")
    public void findShoppingCartByUserId_ValidUserId_ReturnsOptionalUser() {
        //given
        Long userId = 3L;
        User user = createUser(userId);
        ShoppingCart expectedShoppingCart = createShoppingCart(user);
        CartItem cartItem = createCartItem(expectedShoppingCart);
        Set<CartItem> cartItemSet = new HashSet<>();
        cartItemSet.add(cartItem);
        expectedShoppingCart.setCartItems(cartItemSet);
        Optional<ShoppingCart> expectedOptionalOfShoppingCart = Optional.of(expectedShoppingCart);

        //when
        Optional<ShoppingCart> actualOptionalShoppingCart = shoppingCartRepository
                .findShoppingCartByUserId(userId);

        //then
        assertEquals(expectedOptionalOfShoppingCart.orElseThrow(),
                actualOptionalShoppingCart.orElseThrow());
        assertEquals(expectedOptionalOfShoppingCart.orElseThrow().getCartItems(),
                actualOptionalShoppingCart.orElseThrow().getCartItems());
    }

    @Test
    @DisplayName("Verify findShoppingCartByUserId() method works with invalid user id")
    public void findShoppingCartByUserId_InvalidUserId_ReturnsEmptyOptional() {
        //given
        Long userId = 1000L;

        //when
        Optional<ShoppingCart> actualOptionalShoppingCart = shoppingCartRepository
                .findShoppingCartByUserId(userId);

        //then
        assertTrue(actualOptionalShoppingCart.isEmpty());
    }

    private User createUser(Long userId) {
        return new User()
                .setId(userId);
    }

    private ShoppingCart createShoppingCart(@NotNull User user) {
        return new ShoppingCart()
                .setId(user.getId())
                .setUser(user)
                .setDeleted(false);
    }

    private CartItem createCartItem(ShoppingCart shoppingCart) {
        return new CartItem()
                .setId(3L)
                .setQuantity(30)
                .setShoppingCart(shoppingCart)
                .setDeleted(false);
    }
}
