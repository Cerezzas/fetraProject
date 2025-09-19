package az.coders.fera_project.service;

import az.coders.fera_project.dto.cart.CartItemDto;

import java.util.List;
public interface CartItemService {

    void addProductToCart(Long userId, String sessionKey, Integer productId, Integer quantity);

    void removeProductFromCart(Long userId, String sessionKey, Integer cartItemId);

    CartItemDto getCartItemById(Long userId, String sessionKey, Long cartItemId);

    void removeItem(Long userId, String sessionKey, Integer cartItemId);

    void removeItems(Long userId, String sessionKey, List<Long> cartItemIds);

    void updateQuantity(Long userId, String sessionKey, Integer cartItemId, int quantity);

    void moveToWishlist(Long userId, String sessionKey, Integer cartItemId);
}
