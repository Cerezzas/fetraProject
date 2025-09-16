package az.coders.fera_project.dto.homepage;

import az.coders.fera_project.entity.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMainPageDto {
    private String name; // Название товара
    private Double price; // Цена
    private Double oldPrice; // Старая цена (если есть)
    private Media image; // Ссылка на главное изображение товара
    private Double averageRating; // Средний рейтинг товара
}

