package az.coders.fera_project.dto;

import az.coders.fera_project.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // Ниже — фейковые поля (используются только валидации на фронте/бэке, не сохраняются в БД)

    @Size(min = 16, max = 16, message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry date must be in MM/YY format")
    private String expiryDate;

    @Size(min = 3, max = 4, message = "CVC must be 3 or 4 digits")
    private String cvc;
}


