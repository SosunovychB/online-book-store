package book.store.service;

import book.store.dto.cart.item.CartItemDto;
import book.store.dto.shopping.cart.UpdateBookQuantityRequestDto;

public interface CartItemService {
    CartItemDto updateBookQuantityInCartItem(Long cartItemId,
                                             UpdateBookQuantityRequestDto requestDto);

    void deleteCartItemById(Long cartItemId);
}
