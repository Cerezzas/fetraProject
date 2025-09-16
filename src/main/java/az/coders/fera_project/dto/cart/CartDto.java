package az.coders.fera_project.dto.cart;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private List<CartItemDto> products;
    private BigDecimal subtotal;
    private BigDecimal taxes;
    private BigDecimal discount;     // Сумма скидки
    private BigDecimal deliveryFee;
    private BigDecimal grandTotal;
}