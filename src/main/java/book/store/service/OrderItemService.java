package book.store.service;

import book.store.dto.order.item.OrderItemDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderItemService {
    List<OrderItemDto> getAllOrderItems(Pageable pageable, Long orderId);

    OrderItemDto getOrderItemByOrderIdAndId(Long orderId, Long itemId);
}
