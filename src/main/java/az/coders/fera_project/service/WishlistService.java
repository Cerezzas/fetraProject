package az.coders.fera_project.service;

import az.coders.fera_project.dto.cart.WishlistItemDto;

import java.util.List;

public interface WishlistService {
    List<WishlistItemDto> getWishlistItems(Long userId, String sessionKey);
    void addProductToWishlist(Long userId, String sessionKey, Integer productId);
    void removeProductFromWishlist(Long userId, String sessionKey, Integer wishlistItemId);
}
