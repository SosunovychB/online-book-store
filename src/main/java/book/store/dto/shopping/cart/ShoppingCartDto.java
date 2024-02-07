package book.store.dto.shopping.cart;

import book.store.dto.cart.item.CartItemDto;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems = new LinkedHashSet<>();
}
