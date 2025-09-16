package az.coders.fera_project.dto.cart;

import az.coders.fera_project.entity.Media;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Integer id;
    private String productName;
    private Media image;                // объект с изображением товара
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;

    // Дополнительный конструктор без image (для getCart())
    public CartItemDto(Integer id, String productName, BigDecimal productPrice, Integer quantity, BigDecimal totalPrice) {
        this.id = id;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }
}



