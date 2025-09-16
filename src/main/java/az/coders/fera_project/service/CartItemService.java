package az.coders.fera_project.service;

import az.coders.fera_project.dto.cart.CartItemDto;

import java.util.List;

public interface CartItemService {

    // Добавление товара в корзину
    void addProductToCart(Integer userId, Integer productId, Integer quantity);

    // Удаление товара из корзины
    void removeProductFromCart(Integer cartItemId);

    // Получение товара из корзины по ID
    CartItemDto getCartItemById(Long cartItemId);

    void removeItem(Integer cartItemId);
    void removeItems(List<Long> cartItemIds);
    void updateQuantity(Integer cartItemId, int quantity);
    void moveToWishlist(Integer cartItemId); // временно можно просто удалить
}
