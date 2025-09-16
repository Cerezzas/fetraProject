package az.coders.fera_project.entity.cart;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Discount code can't be empty")  // Проверка на null
    private String code;  // Промокод

    @Column(nullable = false)
    @NotNull(message = "Discount percentage can't be empty")  // Проверка на null
    @Positive(message = "Discount percentage have to be positive")  // Процент скидки должен быть положительным
    @Min(value = 0, message = "Discount percentage can't be null or negative")  // Процент скидки не может быть меньше 0
    private BigDecimal discountPercentage;  // Процент скидки

    @Column(nullable = false)
    private Boolean active;  // Активен ли промокод

    public boolean isActive() {
        return Boolean.TRUE.equals(active); // это обработает null значения корректно
    }
}


