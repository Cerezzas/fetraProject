package az.coders.fera_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewOrderDto {
    private List<OrderItemDto> orderItems;
    private ShippingAddressDto shippingAddress;
    private PaymentReviewDto paymentReview; // Новый класс для платёжной информации
}
