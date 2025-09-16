package az.coders.fera_project.controller.admin;


import az.coders.fera_project.dto.product.user.ProductDetailsDto;
import az.coders.fera_project.dto.product.admin.ProductRequestDto;
import az.coders.fera_project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("adminProductController")
@RequestMapping("/admin/products") // базовый путь
@RequiredArgsConstructor // создаст конструктор с нужными зависимостями
public class ProductController {

    private final ProductService productService; // сервис для бизнес-логики

//    // Получить список всех продуктов
//    @GetMapping
//    public ResponseEntity<List<ProductDetailsDto>> getProducts() {
//        List<ProductDetailsDto> products = productService.getProducts();
//        return ResponseEntity.ok(products);
//    }
//
//    // Получить продукт по ID
//    @GetMapping("/{id}")
//    public ResponseEntity<ProductDetailsDto> getProduct(@PathVariable Integer id) {
//        ProductDetailsDto product = productService.getProductById(id);
//        return ResponseEntity.ok(product);
//    }

    // Создать новый продукт
    @PostMapping
    public ResponseEntity<ProductDetailsDto> createProduct(@RequestBody ProductRequestDto dto) {
        ProductDetailsDto createdProduct = productService.createProduct(dto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // Обновить продукт по ID
    @PutMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> updateProduct(@PathVariable Integer id, @RequestBody ProductRequestDto dto) {
        ProductDetailsDto updatedProduct = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }

    // Удалить продукт по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Добавление изображения к продукту
    @PostMapping("/{productId}/image")
    public ResponseEntity<Void> addImageToProduct(
            @PathVariable Integer productId,
            @RequestParam Integer mediaId) {
        productService.addImageToProduct(productId, mediaId);
        return ResponseEntity.ok().build();
    }

    // Удаление изображения у продукта
    @DeleteMapping("/{productId}/image")
    public ResponseEntity<Void> deleteImageFromProduct(@PathVariable Integer productId) {
        productService.deleteImageFromProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
