package az.coders.fera_project.entity;

import az.coders.fera_project.enums.PaymentMethod;
import az.coders.fera_project.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Boolean isPaid;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    // Новые поля
    private String cardHolderName;
    private String cardNumber;  // здесь храним уже маскированный номер, либо хранить полный (не рекомендуется)

    // Не сохраняем:
    // private String expiryDate;
    // private String cvc;
}
