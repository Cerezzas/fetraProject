package az.coders.fera_project.dto.product.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    private String name;
    private String description;
    private Double price;
    private Double oldPrice;
//    private String mainImageUrl;
    private Integer stockQuantity;
    private List<String> availableColors;
    private String material;
    private String colorName;
    private Double weightKg;
    private Double widthCm;
    private Double heightCm;
    private Double depthCm;
    private String detailedDescription;
    private String additionalInformation;
    private String shippingAndDelivery;
    private Integer returnDays;
    private LocalDate deliveryDate;

    private Integer categoryId; // чтобы связать с категорией
    private List<Integer> similarProductIds; // чтобы связать с другими продуктами - ID похожих товаров
}

