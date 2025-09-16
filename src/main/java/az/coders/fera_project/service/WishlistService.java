package az.coders.fera_project.service;

import az.coders.fera_project.dto.cart.WishlistItemDto;

import java.util.List;

public interface WishlistService {
    List<WishlistItemDto> getWishlistItems(Integer userId);
    void addProductToWishlist(Integer userId, Integer productId);
    void removeProductFromWishlist(Integer userId, Integer wishlistItemId);

}
