package az.coders.fera_project.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalsDto {
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal taxes;
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;

    // Конструктор с полями
    public TotalsDto(double subtotal, double taxes, double deliveryFee, double grandTotal) {
        this.subtotal = BigDecimal.valueOf(subtotal);
        this.taxes = BigDecimal.valueOf(taxes);
        this.deliveryFee = BigDecimal.valueOf(deliveryFee);
        this.grandTotal = BigDecimal.valueOf(grandTotal);
    }

}
