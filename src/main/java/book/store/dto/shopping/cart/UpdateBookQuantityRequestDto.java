package book.store.dto.shopping.cart;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateBookQuantityRequestDto {
    @Positive
    private int quantity;
}
