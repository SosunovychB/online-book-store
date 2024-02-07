package book.store.service;

import book.store.dto.shopping.cart.AddBookToShoppingCartRequestDto;
import book.store.dto.shopping.cart.ShoppingCartDto;
import book.store.dto.shopping.cart.UpdateBookQuantityRequestDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartByUser(String userEmail);

    ShoppingCartDto addBookToShoppingCart(String userEmail,
                                          AddBookToShoppingCartRequestDto requestDto);

    ShoppingCartDto updateBookQuantityInCartItem(Long cartItemId,
                                                 UpdateBookQuantityRequestDto requestDto);
}
