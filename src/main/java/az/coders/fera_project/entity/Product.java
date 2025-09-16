package az.coders.fera_project.entity;

import az.coders.fera_project.entity.cart.WishlistItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;// название товара


    private String description; // описание товара (короткое/основное)


    private Double price; // цена

    private Double oldPrice; // зачеркнутая цена

    @OneToOne
    @JoinColumn(name = "media_id")
    private Media image;
 //   private String imageUrl; // основное фото товара

//    // Рейтинг и количество отзывов (кеш)
//    private Double averageRating;     // средний рейтинг (например, 4.5)
//    private Integer reviewCount;      // количество отзывов (например, 2800)


    // Наличие на складе
    private Integer stockQuantity;    // количество в наличии (например, 4)

    // Цвета (перечисление цветов, доступных для товара)
   @ElementCollection
    private List<String> availableColors;

    private String material;  // материал, например "leather"

    private String colorName; // конкретный цвет, например "brown"

    private Double weightKg; // вес в кг

    // Размеры
    private Double widthCm;
    private Double heightCm;
    private Double depthCm;

    // Тексты для раскрывающихся секций (Description, Additional Info, Shipping & Delivery)
    @Column(columnDefinition = "TEXT")
    private String detailedDescription; // длинный текст описания

    @Column(columnDefinition = "TEXT")
    private String additionalInformation;

    @Column(columnDefinition = "TEXT")
    private String shippingAndDelivery;

    //for cart
    private int returnDays;
    private LocalDate deliveryDate;


    // Многие к одному - много продуктов принадлежат одной категории
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;


    // Связь с отзывами
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    // Вот кешируемые поля для рейтинга и количества отзывов:
    private Double averageRating; // средний рейтинг товара (например 4.5)

    private Integer reviewCount = 0;
    // количество отзывов (например 2800)


    @ManyToMany
    @JoinTable(
            name = "product_similar_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "similar_product_id")
    )
    @JsonIgnore
    private List<Product> similarProducts; // Список похожих товаров

    //поле wishlistItems, которое будет хранить все элементы вишлиста, связанные с конкретным продуктом.
   // Связь с вишлистом
    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<WishlistItem> wishlistItems = new ArrayList<>();

//    // Флаг "all items" для фильтра
//    private Boolean allItems = false;

}
