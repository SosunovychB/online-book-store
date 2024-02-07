package book.store.dto.shopping.cart;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddBookToShoppingCartRequestDto {
    @Positive
    private Long bookId;
    @Positive
    private int quantity;
}
