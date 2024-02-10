package book.store.service.impl;

import book.store.dto.order.item.OrderItemDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.OrderItemMapper;
import book.store.model.OrderItem;
import book.store.repository.order.item.OrderItemRepository;
import book.store.service.OrderItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemDto> getAllOrderItems(Pageable pageable, Long orderId) {
        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(pageable, orderId);
        return orderItemList.stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getOrderItemByOrderIdAndId(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findOrderItemByOrderIdAndId(orderId, itemId)
                        .orElseThrow(() -> new EntityNotFoundException("Can't find item with id "
                                + itemId + " from order with id " + orderId));
        return orderItemMapper.toDto(orderItem);
    }
}
