package book.store.dto.audit;

import book.store.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderDtoForAudit {
    private Long orderId;
    private Long userId;
    private Order.Status status;
    private BigDecimal total;
    private LocalDateTime orderDate;
    private String shippingAddress;
}
