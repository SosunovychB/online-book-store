package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.shopping.cart.item.CartItemDto;
import book.store.model.CartItem;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toCartItemDto(CartItem cartItem);

    @Named(value = "toCartItemDtoSet")
    default Set<CartItemDto> toCartItemDtoSet(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toCartItemDto)
                .collect(Collectors.toSet());
    }
}
