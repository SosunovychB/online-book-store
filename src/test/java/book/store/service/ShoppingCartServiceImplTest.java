package book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import book.store.dto.shopping.cart.AddBookToShoppingCartRequestDto;
import book.store.dto.shopping.cart.ShoppingCartDto;
import book.store.dto.shopping.cart.UpdateBookQuantityRequestDto;
import book.store.dto.shopping.cart.item.CartItemDto;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.Book;
import book.store.model.CartItem;
import book.store.model.Role;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.book.BookRepository;
import book.store.repository.shopping.cart.ShoppingCartRepository;
import book.store.repository.shopping.cart.item.CartItemRepository;
import book.store.repository.user.UserRepository;
import book.store.service.impl.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartServiceImpl;

    @Test
    @DisplayName("Verify getShoppingCartByUser() method works")
    public void getShoppingCartByUser_ValidUserEmail_ReturnsValidShoppingCartDto() {
        //given
        User user2 = createUserWithId2();
        ShoppingCart expectedShoppingCart = createModelShoppingCart2(user2);
        ShoppingCartDto expectedShoppingCartDto = createModelShoppingCart2Dto(expectedShoppingCart);

        Mockito.when(userRepository.findByEmail(user2.getEmail()))
                .thenReturn(Optional.of(user2));
        Mockito.when(shoppingCartRepository.findShoppingCartByUserId(user2.getId()))
                .thenReturn(Optional.of(expectedShoppingCart));
        Mockito.when(shoppingCartMapper.toDto(expectedShoppingCart))
                .thenReturn(expectedShoppingCartDto);

        //when
        ShoppingCartDto actualShoppingCartDto = shoppingCartServiceImpl
                .getShoppingCartByUser(user2.getEmail());

        //then
        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);

        verify(userRepository, Mockito.times(1))
                .findByEmail(user2.getEmail());
        verify(shoppingCartRepository, Mockito.times(1))
                .findShoppingCartByUserId(user2.getId());
        verify(shoppingCartMapper, Mockito.times(1))
                .toDto(expectedShoppingCart);
    }

    @Test
    @DisplayName("Verify addBookToShoppingCart() method works")
    public void addBookToShoppingCart_ValidUserEmailAndRequest_ReturnsValidShoppingCartDto() {
        //given
        AddBookToShoppingCartRequestDto requestDto = new AddBookToShoppingCartRequestDto()
                .setBookId(3L)
                .setQuantity(200);

        User user2 = createUserWithId2();
        ShoppingCart initialShoppingCart = createModelShoppingCart2(user2);
        ShoppingCart newShoppingCart = createModelShoppingCart2(user2);
        Book newBook = createBookBasedOnRequestDto(requestDto);
        CartItem newCartItem = createNewCartItem(newShoppingCart, requestDto);
        newShoppingCart.getCartItems().add(newCartItem);
        ShoppingCartDto expectedShoppingCartDto = createModelShoppingCart2Dto(newShoppingCart);

        Mockito.when(userRepository.findByEmail(user2.getEmail()))
                .thenReturn(Optional.of(user2));
        Mockito.when(shoppingCartRepository.findShoppingCartByUserId(user2.getId()))
                .thenReturn(Optional.of(initialShoppingCart));
        Mockito.when(bookRepository.findBookById(newBook.getId()))
                .thenReturn(Optional.of(newBook));
        Mockito.when(cartItemRepository.save(any(CartItem.class)))
                .thenReturn(newCartItem);
        Mockito.when(shoppingCartRepository.save(newShoppingCart))
                .thenReturn(newShoppingCart);
        Mockito.when(shoppingCartMapper.toDto(newShoppingCart))
                .thenReturn(expectedShoppingCartDto);

        //when
        ShoppingCartDto actualShoppingCartDto = shoppingCartServiceImpl
                .addBookToShoppingCart(user2.getEmail(), requestDto);

        //then
        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);

        verify(userRepository, Mockito.times(1))
                .findByEmail(user2.getEmail());
        verify(shoppingCartRepository, Mockito.times(1))
                .findShoppingCartByUserId(user2.getId());
        verify(bookRepository, Mockito.times(1))
                .findBookById(newBook.getId());
        verify(cartItemRepository, Mockito.times(1))
                .save(any(CartItem.class));
        verify(shoppingCartRepository, Mockito.times(1))
                .save(newShoppingCart);
        verify(shoppingCartMapper, Mockito.times(1))
                .toDto(newShoppingCart);
    }

    @Test
    @DisplayName("Verify updateBookQuantityInCartItem() method works")
    public void updateBookQuantityInCartItem_ValidItemIdAndRequest_ReturnsValidShoppingCartDto() {
        //given
        Long cartItemId = 2L;
        UpdateBookQuantityRequestDto updateRequestDto = new UpdateBookQuantityRequestDto()
                .setQuantity(100);

        User user2 = createUserWithId2();
        ShoppingCart initialShoppingCart = createModelShoppingCart2(user2);
        Set<CartItem> initialCartItemSet = initialShoppingCart.getCartItems();
        CartItem initialCartItem = initialCartItemSet.stream().findFirst().orElseThrow();
        CartItem newCartItem = initialCartItem.setQuantity(updateRequestDto.getQuantity());
        Set<CartItem> newCartItemSet = new HashSet<>();
        newCartItemSet.add(newCartItem);
        ShoppingCart newShoppingCart = initialShoppingCart.setCartItems(newCartItemSet);
        ShoppingCartDto expectedShoppingCartDto = createModelShoppingCart2Dto(newShoppingCart);

        Mockito.when(cartItemRepository.findById(cartItemId))
                .thenReturn(Optional.of(initialCartItem));
        Mockito.when(cartItemRepository.save(newCartItem))
                .thenReturn(newCartItem);
        Mockito.when(shoppingCartRepository.findShoppingCartByUserId(user2.getId()))
                .thenReturn(Optional.of(newShoppingCart));
        Mockito.when(shoppingCartMapper.toDto(newShoppingCart))
                .thenReturn(expectedShoppingCartDto);

        //when
        ShoppingCartDto actualShoppingCartDto = shoppingCartServiceImpl
                .updateBookQuantityInCartItem(cartItemId, updateRequestDto);

        //then
        assertEquals(expectedShoppingCartDto, actualShoppingCartDto);

        verify(cartItemRepository, Mockito.times(1))
                .findById(cartItemId);
        verify(cartItemRepository, Mockito.times(1))
                .save(newCartItem);
        verify(shoppingCartRepository, Mockito.times(1))
                .findShoppingCartByUserId(user2.getId());
        verify(shoppingCartMapper, Mockito.times(1))
                .toDto(newShoppingCart);
    }

    private User createUserWithId2() {
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role().setRoleName(Role.RoleName.ROLE_USER));
        return new User()
                .setId(2L)
                .setEmail("user2@example.com")
                .setRoles(roleSet);
    }

    private ShoppingCart createShoppingCartWithId2(User user) {
        return new ShoppingCart()
                .setId(2L)
                .setUser(user)
                .setDeleted(false);
    }

    private Book createBook2() {
        return new Book()
                .setId(2L)
                .setTitle("Book 2")
                .setAuthor("Author 2")
                .setIsbn("ISBN-2")
                .setPrice(new BigDecimal("15.75"))
                .setDescription("Description for Book 2")
                .setCoverImage("cover2.jpg")
                .setDeleted(false);
    }

    private CartItem createDefaultCartItemWithId2(ShoppingCart shoppingCart) {
        Book book = createBook2();
        return new CartItem()
                .setId(2L)
                .setShoppingCart(shoppingCart)
                .setBook(book)
                .setQuantity(20)
                .setDeleted(false);
    }

    private @NotNull ShoppingCart createModelShoppingCart2(User user2) {
        ShoppingCart shoppingCart = createShoppingCartWithId2(user2);
        CartItem cartItem = createDefaultCartItemWithId2(shoppingCart);
        Set<CartItem> cartItemSet = new HashSet<>();
        cartItemSet.add(cartItem);
        shoppingCart.setCartItems(cartItemSet);
        return shoppingCart;
    }

    private ShoppingCartDto createModelShoppingCart2Dto(@NotNull ShoppingCart shoppingCart) {
        return new ShoppingCartDto()
                .setId(shoppingCart.getId())
                .setUserId(shoppingCart.getUser().getId())
                .setCartItems(createCartItemDtoFromCartItem(shoppingCart.getCartItems()));
    }

    private Set<CartItemDto> createCartItemDtoFromCartItem(@NotNull Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> new CartItemDto()
                        .setId(cartItem.getId())
                        .setBookId(cartItem.getBook().getId())
                        .setBookTitle(cartItem.getBook().getTitle())
                        .setQuantity(cartItem.getQuantity()))
                .collect(Collectors.toSet());
    }

    private Book createBookBasedOnRequestDto(
            @NotNull AddBookToShoppingCartRequestDto addBookToShoppingCartRequestDto) {
        return new Book()
                .setId(addBookToShoppingCartRequestDto.getBookId());
    }

    private CartItem createNewCartItem(
            ShoppingCart shoppingCart,
            @NotNull AddBookToShoppingCartRequestDto addBookToShoppingCartRequestDto) {
        return new CartItem()
                .setId(4L)
                .setShoppingCart(shoppingCart)
                .setBook(createBookBasedOnRequestDto(addBookToShoppingCartRequestDto))
                .setQuantity(addBookToShoppingCartRequestDto.getQuantity())
                .setDeleted(false);
    }
}
