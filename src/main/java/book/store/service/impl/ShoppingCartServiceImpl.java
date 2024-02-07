package book.store.service.impl;

import book.store.dto.shopping.cart.AddBookToShoppingCartRequestDto;
import book.store.dto.shopping.cart.ShoppingCartDto;
import book.store.dto.shopping.cart.UpdateBookQuantityRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.exception.ItemIsAlreadyInCartException;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.repository.book.BookRepository;
import book.store.repository.cart.item.CartItemRepository;
import book.store.repository.shopping.cart.ShoppingCartRepository;
import book.store.repository.user.UserRepository;
import book.store.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getShoppingCartByUser(String userEmail) {
        ShoppingCart shoppingCart = findShoppingCartByUserEmail(userEmail);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addBookToShoppingCart(String userEmail,
                                                 AddBookToShoppingCartRequestDto requestDto) {
        ShoppingCart shoppingCart = findShoppingCartByUserEmail(userEmail);
        checkIfBookIsInTheCart(shoppingCart, requestDto);

        CartItem newCartItem = setUpNewCartItem(shoppingCart, requestDto);
        CartItem savedNewCartItem = cartItemRepository.save(newCartItem);

        shoppingCart.getCartItems().add(savedNewCartItem);
        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(savedShoppingCart);
    }

    @Override
    public ShoppingCartDto updateBookQuantityInCartItem(Long cartItemId,
                                                    UpdateBookQuantityRequestDto updateRequestDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find cart item with id " + cartItemId)
        );
        cartItem.setQuantity(updateRequestDto.getQuantity());
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        ShoppingCart updatedShoppingCart = shoppingCartRepository
                .findShoppingCartByUserId(savedCartItem.getShoppingCart().getUser().getId());
        return shoppingCartMapper.toDto(updatedShoppingCart);
    }

    private ShoppingCart findShoppingCartByUserEmail(String userEmail) {
        Long userId = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with email " + userEmail)
        ).getId();
        return shoppingCartRepository.findShoppingCartByUserId(userId);
    }

    private void checkIfBookIsInTheCart(ShoppingCart shoppingCart,
                                         AddBookToShoppingCartRequestDto requestDto) {
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        List<Long> bookIds = cartItems.stream()
                .map(cartItem -> cartItem.getBook().getId())
                .toList();
        if (bookIds.contains(requestDto.getBookId())) {
            throw new ItemIsAlreadyInCartException("Book with id " + requestDto.getBookId()
                    + " is already in your shopping cart!");
        }
    }

    private CartItem setUpNewCartItem(ShoppingCart shoppingCart,
                                      AddBookToShoppingCartRequestDto requestDto) {
        CartItem newCartItem = new CartItem();
        newCartItem.setShoppingCart(shoppingCart);
        newCartItem.setBook(bookRepository.findBookById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find book with id "
                        + requestDto.getBookId())));
        newCartItem.setQuantity(requestDto.getQuantity());
        return newCartItem;
    }
}
