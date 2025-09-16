package az.coders.fera_project.dto;


import az.coders.fera_project.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReviewDto {
    private String cardHolderName;
    private String maskedCardNumber;
    private PaymentMethod paymentMethod;
}
