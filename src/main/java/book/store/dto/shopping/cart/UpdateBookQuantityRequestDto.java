package book.store.dto.shopping.cart;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateBookQuantityRequestDto {
    @Positive
    private int quantity;
}
