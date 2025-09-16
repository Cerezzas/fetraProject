package az.coders.fera_project.entity;

import az.coders.fera_project.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import az.coders.fera_project.entity.register.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "order_table") // ✅ ИЗМЕНЕНО: было "order", стало "orders"
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;  // Привязка к пользователю

    @ManyToOne
    private Address shippingAddress;  // Адрес доставки

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();// Список товаров в заказе

    @OneToOne(cascade = CascadeType.ALL)  // рекомендую поставить cascade ALL или как минимум PERSIST
    @JoinColumn(name = "payment_id")
    private Payment payment; // Платеж

    private BigDecimal totalAmount;  // Итоговая сумма заказа



    private LocalDateTime orderDate;  // Дата заказа

    private OrderStatus status;  // Статус заказа (например, "Ожидает", "Отправлен", "Доставлен")

}

