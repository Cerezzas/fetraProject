package az.coders.fera_project.dto.homepage;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomePageDto {

    private List<CategoryDto> categories; // Список категорий для отображения на главной странице
    private List<ProductMainPageDto> popularProducts; // Список популярных товаров
    private List<ReviewMainPageDto> reviews; // Список отзывов, которые отображаются на главной странице
    private String instagramLink; // Ссылка на Instagram
 //   private List<SocialLinkDto> socialLinks;     // Если решу добавить соцсети (можно пока пропустить)
}


