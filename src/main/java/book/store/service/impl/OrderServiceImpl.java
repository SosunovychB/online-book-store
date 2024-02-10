package book.store.service.impl;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateOrderStatusRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.OrderMapper;
import book.store.model.CartItem;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.order.OrderRepository;
import book.store.repository.shopping.cart.ShoppingCartRepository;
import book.store.repository.shopping.cart.item.CartItemRepository;
import book.store.service.OrderService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> getAllOrders(Pageable pageable, Long userId) {
        List<Order> orderList = orderRepository.findAllByUserId(pageable, userId);
        return orderList.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto placeOrder(User user, PlaceOrderRequestDto requestDto) {
        Set<OrderItem> orderItems = createOrderItems(user);
        BigDecimal totalPrice = calculateTotalPrice(orderItems);
        String shippingAddress = requestDto.getShippingAddress();
        Order savedOrder = createOrder(user, orderItems, totalPrice, shippingAddress);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id " + orderId));
        Order.Status newStatus = Order.Status.valueOf(requestDto.getStatus().toUpperCase());
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }

    private Set<OrderItem> createOrderItems(User user) {
        Long userId = user.getId();
        ShoppingCart userShoppingCart = shoppingCartRepository.findShoppingCartByUserId(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find shopping cart for user with id " + userId));
        Set<CartItem> cartItems = cartItemRepository
                .findAllByShoppingCartId(userShoppingCart.getId());
        checkPresenceOfCartItems(cartItems);
        cartItemRepository.deleteAll(userShoppingCart.getCartItems());
        return cartItems.stream()
                .map(this::convertCartItemIntoOrderItem)
                .collect(Collectors.toSet());
    }

    private boolean checkPresenceOfCartItems(Set<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new EntityNotFoundException("Can not create order, "
                    + "because your shopping cart is empty!");
        }
        return true;
    }

    private OrderItem convertCartItemIntoOrderItem(CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice());
        return orderItem;
    }

    private BigDecimal calculateTotalPrice(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> orderItem.getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order createOrder(User user,
                              Set<OrderItem> orderItems,
                              BigDecimal totalPrice,
                              String shippingAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.ACCEPTED);
        order.setTotal(totalPrice);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);

        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        order.setOrderItems(orderItems);

        return orderRepository.save(order);
    }
}
