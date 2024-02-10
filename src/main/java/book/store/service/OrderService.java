package book.store.service;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateOrderStatusRequestDto;
import book.store.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    List<OrderDto> getAllOrders(Pageable pageable, Long userId);

    OrderDto placeOrder(User user, PlaceOrderRequestDto requestDto);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);
}
