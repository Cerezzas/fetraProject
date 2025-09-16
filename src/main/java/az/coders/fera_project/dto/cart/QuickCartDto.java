package az.coders.fera_project.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuickCartDto {
    private int totalItems;
    private List<CartItemDto> items;
    private BigDecimal subtotal;

    // Удобный конструктор для использования в сервисе
    public QuickCartDto(BigDecimal subtotal, List<CartItemDto> items) {
        this.subtotal = subtotal;
        this.items = items;
        this.totalItems = items.size();
    }
}
