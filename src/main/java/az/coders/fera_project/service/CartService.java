package az.coders.fera_project.service;

import az.coders.fera_project.dto.cart.CartDto;
import az.coders.fera_project.dto.cart.QuickCartDto;

import java.math.BigDecimal;
import java.util.List;


public interface CartService {

    // Получить текущую корзину пользователя
    CartDto getCart(Long userId, String sessionKey);

    // Получить только итоговую сумму корзины
    BigDecimal getGrandTotal(Long userId, String sessionKey);

    // Применить скидку к корзине
    CartDto applyDiscount(Long userId, String sessionKey, String discountCode);

    // Оформить заказ (чекаут)
    void checkout(Long userId, String sessionKey);

    // Добавить товар в корзину
    void addProductToCart(Long userId, String sessionKey, Integer productId, Integer quantity);

    // Удалить товар из корзины
    void removeProductFromCart(Long userId, String sessionKey, Integer cartItemId);

    // Переместить товар в список желаемого
    void moveToWishlist(Long userId, String sessionKey, Integer cartItemId);

    // Переместить несколько товаров в список желаемого
    void moveMultipleToWishlist(Long userId, String sessionKey, List<Long> cartItemIds) ;

    void removeMultipleFromCart(Long userId, String sessionKey, List<Long> cartItemIds);

    void mergeCart(Long userId, String sessionKey);
    // Получить "быструю" корзину (для отображения в UI)
    QuickCartDto getQuickCart(Long userId, String sessionKey);
}

