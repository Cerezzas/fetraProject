package az.coders.fera_project.service.impl.cart;

import az.coders.fera_project.dto.cart.CartItemDto;
import az.coders.fera_project.entity.Product;
import az.coders.fera_project.entity.cart.Cart;
import az.coders.fera_project.entity.cart.CartItem;
import az.coders.fera_project.entity.cart.Wishlist;
import az.coders.fera_project.entity.cart.WishlistItem;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.exception.BadRequestException;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.ProductRepository;
import az.coders.fera_project.repository.cart.CartItemRepository;
import az.coders.fera_project.repository.cart.CartRepository;
import az.coders.fera_project.repository.cart.WishlistItemRepository;
import az.coders.fera_project.repository.cart.WishlistRepository;
import az.coders.fera_project.repository.register.UserRepository;
import az.coders.fera_project.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;

    // ======= Добавление товара =======
    @Override
    public void addProductToCart(Long userId, String sessionKey, Integer productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId, sessionKey);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId).orElse(null);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int currentQuantity = cartItem != null ? cartItem.getQuantity() : 0;
        int totalRequested = currentQuantity + quantity;

        if (totalRequested > stock) {
            throw new BadRequestException("Only " + stock + " items in stock for product: " + product.getName());
        }

        if (cartItem != null) {
            cartItem.setQuantity(totalRequested);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        cartItemRepository.save(cartItem);
    }

    // ======= Удаление товара =======
    @Override
    public void removeProductFromCart(Long userId, String sessionKey, Integer cartItemId) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        CartItem item = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        cartItemRepository.delete(item);
    }

    @Override
    public void removeItem(Long userId, String sessionKey, Integer cartItemId) {
        removeProductFromCart(userId, sessionKey, cartItemId);
    }

    @Override
    public void removeItems(Long userId, String sessionKey, List<Long> cartItemIds) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        List<CartItem> items = cartItemRepository.findAllById(cartItemIds);
        items.forEach(item -> {
            if (item.getCart().getId().equals(cart.getId())) {
                cartItemRepository.delete(item);
            }
        });
    }

    // ======= Обновление количества =======
    @Override
    public void updateQuantity(Long userId, String sessionKey, Integer cartItemId, int quantity) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        CartItem item = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        Product product = item.getProduct();
        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        if (quantity > stock) {
            throw new BadRequestException("Only " + stock + " items in stock for product: " + product.getName());
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    // ======= Получение товара =======
    @Override
    public CartItemDto getCartItemById(Long userId, String sessionKey, Long cartItemId) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        CartItem item = cartItemRepository.findByIdAndCartId(cartItemId.intValue(), cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        return mapToDto(item);
    }

    // ======= Перемещение в wishlist =======
    @Override
    public void moveToWishlist(Long userId, String sessionKey, Integer cartItemId) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        Wishlist wishlist;
        if (userId != null) {
            wishlist = wishlistRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Wishlist w = new Wishlist();
                        w.setUser(cart.getUser());
                        return wishlistRepository.save(w);
                    });
        } else {
            wishlist = wishlistRepository.findBySessionKey(sessionKey)
                    .orElseGet(() -> {
                        Wishlist w = new Wishlist();
                        w.setSessionKey(sessionKey);
                        return wishlistRepository.save(w);
                    });
        }

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setProduct(cartItem.getProduct());
        wishlistItem.setWishlist(wishlist);

        wishlistItemRepository.save(wishlistItem);
        cartItemRepository.delete(cartItem);
    }

    // ======= Приватные методы =======
    private Cart getOrCreateCart(Long userId, String sessionKey) {
        if (userId != null) {
            return cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("User not found"));
                        Cart cart = new Cart();
                        cart.setUser(user);
                        cart.setSubtotal(BigDecimal.ZERO);
                        cart.setTax(BigDecimal.ZERO);
                        cart.setTotal(BigDecimal.ZERO);
                        return cartRepository.save(cart);
                    });
        } else if (sessionKey != null) {
            return cartRepository.findBySessionKey(sessionKey)
                    .orElseGet(() -> {
                        Cart cart = new Cart();
                        cart.setSessionKey(sessionKey);
                        cart.setSubtotal(BigDecimal.ZERO);
                        cart.setTax(BigDecimal.ZERO);
                        cart.setTotal(BigDecimal.ZERO);
                        return cartRepository.save(cart);
                    });
        } else {
            throw new BadRequestException("Either userId or sessionKey must be provided");
        }
    }

    private CartItemDto mapToDto(CartItem item) {
        BigDecimal price = BigDecimal.valueOf(item.getProduct().getPrice());
        int quantity = item.getQuantity();
        return new CartItemDto(
                item.getId(),
                item.getProduct().getName(),
                item.getProduct().getImage(),
                price,
                quantity,
                price.multiply(BigDecimal.valueOf(quantity))
        );
    }
}
