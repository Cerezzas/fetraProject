package az.coders.fera_project.dto.homepage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMainPageDto {
    private String userName;// Имя пользователя, который оставил отзыв
    private String comment; // Текст отзыва
    private Double rating; // Средний рейтинг
    private LocalDateTime createdAt;// Дата создания отзыва
}
