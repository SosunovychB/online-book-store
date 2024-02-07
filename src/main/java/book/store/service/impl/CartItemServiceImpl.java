package book.store.service.impl;

import book.store.dto.cart.item.CartItemDto;
import book.store.dto.shopping.cart.UpdateBookQuantityRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.model.CartItem;
import book.store.repository.cart.item.CartItemRepository;
import book.store.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItemDto updateBookQuantityInCartItem(Long cartItemId,
                                                    UpdateBookQuantityRequestDto updateRequestDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find cart item with id " + cartItemId)
        );
        cartItem.setQuantity(updateRequestDto.getQuantity());
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toCartItemDto(savedCartItem);
    }

    @Override
    public void deleteCartItemById(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
