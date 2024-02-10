package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.order.item.OrderItemDto;
import book.store.model.OrderItem;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);

    @Named(value = "toOrderItemDtoSet")
    default Set<OrderItemDto> toOrderItemDtoSet(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
}
