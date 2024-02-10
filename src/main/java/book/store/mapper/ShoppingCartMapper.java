package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.shopping.cart.ShoppingCartDto;
import book.store.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems", qualifiedByName = "toCartItemDtoSet")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
