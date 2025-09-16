package az.coders.fera_project.service;

import az.coders.fera_project.dto.PaymentDto;
import az.coders.fera_project.dto.ReviewOrderDto;
import az.coders.fera_project.dto.ShippingAddressDto;

public interface ReviewOrderService {
    // Получить информацию о заказе для страницы Review
    ReviewOrderDto getReviewOrder(Long orderId, Long userId);


    // Обновить адрес доставки для заказа
    void updateShippingAddress(Long orderId, ShippingAddressDto shippingAddressDto);

    // Обновить платёжную информацию для заказа
    void updatePaymentMethod(Long orderId, PaymentDto paymentDto);

    void placeOrder(Long orderId, Long userId);
}
