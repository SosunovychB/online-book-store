package book.store.service;

import book.store.dto.shopping.cart.AddBookToShoppingCartRequestDto;
import book.store.dto.shopping.cart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartByUser(String userEmail);

    ShoppingCartDto addBookToShoppingCart(String userEmail,
                                          AddBookToShoppingCartRequestDto requestDto);
}
