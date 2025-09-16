package az.coders.fera_project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaDto {
    private Integer id;
    private String path;
    private MediaTypeDto mediaTypeDto;
}
