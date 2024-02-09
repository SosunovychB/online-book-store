package book.store.repository.order.item;

import book.store.model.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Pageable pageable, Long orderId);

    Optional<OrderItem> findOrderItemByOrderIdAndId(Long orderId, Long itemId);
}
