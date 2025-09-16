package az.coders.fera_project.entity.cart;

import az.coders.fera_project.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;  // Связь с корзиной

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // Связь с продуктом

    private Integer quantity;  // Количество товара
}
