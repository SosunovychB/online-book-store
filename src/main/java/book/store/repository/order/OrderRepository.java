package book.store.repository.order;

import book.store.model.Order;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("FROM Order o LEFT JOIN FETCH o.orderItems i WHERE o.user.id = :userId")
    List<Order> findAllByUserId(Pageable pageable, Long userId);
}
