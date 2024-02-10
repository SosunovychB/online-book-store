package book.store.controller;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateOrderStatusRequestDto;
import book.store.dto.order.item.OrderItemDto;
import book.store.model.User;
import book.store.service.OrderItemService;
import book.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders management", description = "Endpoints for orders management")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderItemService orderItemService;
    private final OrderService orderService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Retrieve user's order history",
            description = "Retrieve user's order history")
    public List<OrderDto> getAllOrders(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrders(pageable, user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(summary = "Place an order",
            description = "Place an order")
    public OrderDto placeOrder(Authentication authentication,
                               @RequestBody @Valid PlaceOrderRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user, requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Retrieve all OrderItems for a specific order",
            description = "Retrieve all OrderItems for a specific order")
    public List<OrderItemDto> getAllOrderItems(Pageable pageable, @PathVariable Long orderId) {
        return orderItemService.getAllOrderItems(pageable, orderId);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Retrieve a specific OrderItem within an order",
            description = "Retrieve a specific OrderItem within an order")
    public OrderItemDto getOrderItemById(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderItemService.getOrderItemByOrderIdAndId(orderId, itemId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}")
    @Operation(summary = "Update order status",
            description = "Update order status")
    public OrderDto updateOrderStatus(@PathVariable Long orderId,
            @RequestBody @Valid UpdateOrderStatusRequestDto requestDto) {
        return orderService.updateOrderStatus(orderId, requestDto);
    }
}
