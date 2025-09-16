package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.PaymentDto;
import az.coders.fera_project.dto.ReviewOrderDto;
import az.coders.fera_project.dto.ShippingAddressDto;
import az.coders.fera_project.service.ReviewOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewOrderController {

    private final ReviewOrderService reviewOrderService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ReviewOrderDto> getReviewOrder(@PathVariable Long orderId,
                                                         @RequestParam Long userId) {
        ReviewOrderDto reviewOrderDto = reviewOrderService.getReviewOrder(orderId, userId);
        return ResponseEntity.ok(reviewOrderDto);
    }



    /**
     * Обновить адрес доставки для заказа.
     *
     * @param orderId ID заказа.
     * @param shippingAddressDto DTO с новыми данными адреса.
     * @return Сообщение об успешном обновлении.
     */
    @PutMapping("/{orderId}/update-shipping-address")
    public ResponseEntity<String> updateShippingAddress(@PathVariable Long orderId,
                                                        @RequestBody ShippingAddressDto shippingAddressDto) {
        reviewOrderService.updateShippingAddress(orderId, shippingAddressDto);
        return ResponseEntity.ok("Shipping address updated");
    }

    @PutMapping("/payment/{orderId}")
    public ResponseEntity<String> updatePaymentMethod(@PathVariable Long orderId,
                                                      @RequestBody @Valid PaymentDto paymentDto) {
        reviewOrderService.updatePaymentMethod(orderId, paymentDto);
        return ResponseEntity.ok("Payment information updated");
    }


    /**
     * Завершить заказ (Place Order).
     *
     * @param orderId ID заказа.
     * @param userId ID пользователя.
     * @return Сообщение об успешном завершении заказа.
     */
    @PostMapping("/place-order/{orderId}")
    public ResponseEntity<Map<String, Object>> placeOrder(@PathVariable Long orderId,
                                                          @RequestParam Long userId) {
        reviewOrderService.placeOrder(orderId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order placed successfully");
        response.put("orderId", orderId);
        return ResponseEntity.ok(response);
    }

}

