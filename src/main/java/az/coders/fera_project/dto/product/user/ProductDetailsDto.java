package az.coders.fera_project.dto.product.user;

import az.coders.fera_project.dto.homepage.ProductMainPageDto;
import az.coders.fera_project.dto.homepage.ReviewMainPageDto;
import az.coders.fera_project.entity.Media;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsDto {

    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Double oldPrice;
    private Media image;

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

    private int returnDays;
    private LocalDate deliveryDate;

    private Double averageRating;
    private Integer reviewCount;

    private List<ReviewMainPageDto> topReviews;


    // Список похожих товаров
    private List<ProductMainPageDto> similarProducts;// ✅ список похожих товаров в облегчённом виде

    }
