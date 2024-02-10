package book.store.repository.shopping.cart.item;

import book.store.model.CartItem;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Set<CartItem> findAllByShoppingCartId(Long shoppingCartId);
}
