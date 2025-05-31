package book.store.model;

import book.store.dto.audit.OrderDtoForAudit;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logs")
@Data
@Builder
public class OrderAuditInfo {
    @Id
    private String id;
    private Long userId;
    private OrderDtoForAudit order;
    private LocalDateTime timestamp;
}
