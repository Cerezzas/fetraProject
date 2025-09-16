package az.coders.fera_project.service;

import az.coders.fera_project.dto.cart.CartDto;
import az.coders.fera_project.dto.cart.QuickCartDto;

import java.math.BigDecimal;
import java.util.List;


public interface CartService {

    // Получить текущую корзину пользователя
    CartDto getCart(Long userId);

    // Получить только итоговую сумму корзины
    BigDecimal getGrandTotal(Long userId);

    // Применить скидку к корзине
    CartDto applyDiscount(Long userId, String discountCode);

    // Оформить заказ (чекаут)
    void checkout(Long userId);

    // Добавить товар в корзину
    void addProductToCart(Long userId, Integer productId, Integer quantity);

    // Удалить товар из корзины
    void removeProductFromCart(Long userId, Integer cartItemId);

    // Переместить товар в список желаемого
    void moveToWishlist(Long userId, Integer cartItemId);

    // Переместить несколько товаров в список желаемого
    void moveMultipleToWishlist(Long userId, List<Long> cartItemIds);

    // Удалить несколько товаров из корзины
    void removeMultipleFromCart(Long userId, List<Long> cartItemIds);

    // Получить "быструю" корзину (для отображения в UI)
    QuickCartDto getQuickCart(Long userId);
}

