package az.coders.fera_project.dto;

import az.coders.fera_project.entity.Media;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    @JsonIgnore
    private Integer productId;
    private String productName;
    private Media productImage;
    private Integer quantity;
    private BigDecimal price;
}
