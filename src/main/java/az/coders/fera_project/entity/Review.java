package az.coders.fera_project.entity;

import az.coders.fera_project.entity.register.User;
import jakarta.persistence.*;
import lombok.Data;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String userName;  // Имя, сохранённое в отзыве

    @Column(nullable = false)
    private String email;     // Email, сохранённый в отзыве

    // Рейтинг и количество отзывов (кеш)
    private Double rating;     // средний рейтинг (например, 4.5)

//    private Integer reviewCount;      // количество отзывов (например, 2800)

    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;  // Дата создания отзыва


    @ManyToOne
    @JoinColumn(name = "product_id") // колонка в БД, которая будет FK
    @JsonIgnore
    private Product product;


    // Используем сохранённое поле userName, если оно есть
    public String getUserName() {
        return userName != null ? userName : (user != null ? user.getUsername() : null);
    }
}

