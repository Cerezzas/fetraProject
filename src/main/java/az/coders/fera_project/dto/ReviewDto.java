package az.coders.fera_project.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//что отдаем клиенту:
public class ReviewDto {
//    private Double averageRating; // Рейтинг товара
//    private Integer reviewCount;  // Количество отзывов
//    private String comment;       // Текст отзыва
//    private Integer userId;       // ID пользователя, оставившего отзыв
//    private Integer productId;    // ID товара, к которому относится отзыв

    private Long id;
    private String userName;
    private String comment;
    private Double rating;
    private LocalDateTime createdAt;


}

