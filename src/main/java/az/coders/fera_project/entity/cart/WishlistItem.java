package az.coders.fera_project.entity.cart;

import az.coders.fera_project.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class WishlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    @JsonIgnore // предотвращает цикл Wishlist → WishlistItem → Wishlist
    private Wishlist wishlist;  // связь с вишлистом

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // связь с продуктом


}
