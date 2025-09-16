package az.coders.fera_project.controller.user;


import az.coders.fera_project.dto.homepage.ProductMainPageDto;
import az.coders.fera_project.dto.product.user.ProductDetailsDto;
import az.coders.fera_project.dto.product.user.ProductFilterRequestDto;
import az.coders.fera_project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userProductController")
@RequestMapping("/products") // базовый путь
@RequiredArgsConstructor // создаст конструктор с нужными зависимостями
public class ProductController {

    private final ProductService productService; // сервис для бизнес-логики

    // Получить список всех продуктов
    @GetMapping
    public ResponseEntity<List<ProductDetailsDto>> getProducts() {
        List<ProductDetailsDto> products = productService.getProducts();
        return ResponseEntity.ok(products);
    }

    // Получить продукт по ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> getProduct(@PathVariable Integer id) {
        ProductDetailsDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<ProductMainPageDto>> filterProducts(
            @RequestBody ProductFilterRequestDto filterRequest) {
        List<ProductMainPageDto> filteredProducts = productService.filterProducts(filterRequest);
        return ResponseEntity.ok(filteredProducts);
    }


}
