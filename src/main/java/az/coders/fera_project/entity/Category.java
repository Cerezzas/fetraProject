package az.coders.fera_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Entity
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Название категории, например "Living Room", "Kitchen"
    private String name;

    @OneToOne
    @JoinColumn(name = "media_id")
    private Media image;

//    private String imageUrl; // Изображение категории

    // Один ко многим - в категории много продуктов
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products;

//    // Продукты в этой категории
//    @ManyToMany
//    @JoinTable(
//            name = "category_product",
//            joinColumns = @JoinColumn(name = "category_id"),
//            inverseJoinColumns = @JoinColumn(name = "product_id")
//    )
//    private List<Product> products;

//
//    @ManyToMany(mappedBy = "categories")
//    private List<Product> products;



//    // quantity of products
//    private Long quantity;

}
