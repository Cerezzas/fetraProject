package az.coders.fera_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;  // Привязка к заказу


    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // Привязка к продукту

    private Integer quantity;  // Количество товара

    private BigDecimal price;  // Цена товара

    // Конструкторы, getters и setters
}

