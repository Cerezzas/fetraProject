package az.coders.fera_project.entity.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import az.coders.fera_project.entity.register.User;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_key")
    private String sessionKey; // уникальный идентификатор гостя

    @OneToOne
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    private BigDecimal subtotal;

    @ManyToOne  // Связь с Discount (многие корзины могут иметь один дисконт)
    @JoinColumn(name = "discount_id")  // Указание на внешний ключ
    private Discount discount;

    private BigDecimal tax;

    private BigDecimal deliveryFee;

    private BigDecimal total;
}
