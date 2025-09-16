package az.coders.fera_project.dto.product.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequestDto {
    private List<String> categoryNames;
    private List<String> colors;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy; // "popularity", "priceAsc", "priceDesc"
}

