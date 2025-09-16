package az.coders.fera_project.dto.cart;

import az.coders.fera_project.entity.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemDto {
    private Integer id;              // ID WishlistItem
    private Integer productId;       // ID продукта
    private String productName;      // Название продукта
    private Media image;         // URL изображения
    private double price;            // Цена продукта
}
