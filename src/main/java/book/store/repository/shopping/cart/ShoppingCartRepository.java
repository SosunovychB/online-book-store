package book.store.repository.shopping.cart;

import book.store.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("FROM ShoppingCart s LEFT JOIN FETCH s.cartItems c WHERE s.user.id = :userId")
    Optional<ShoppingCart> findShoppingCartByUserId(Long userId);
}
