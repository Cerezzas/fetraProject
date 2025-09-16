package az.coders.fera_project.dto.homepage;

import az.coders.fera_project.entity.Media;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
//    private Integer id;
    private String name;
    private Media image;
//    private List<Integer> productIds; // ID продуктов в этой категории
}
