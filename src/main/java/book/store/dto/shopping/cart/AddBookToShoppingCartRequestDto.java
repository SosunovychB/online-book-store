package book.store.dto.shopping.cart;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddBookToShoppingCartRequestDto {
    @Positive
    private Long bookId;
    @Positive
    private int quantity;
}
