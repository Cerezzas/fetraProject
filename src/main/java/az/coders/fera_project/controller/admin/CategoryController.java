package az.coders.fera_project.controller.admin;


import az.coders.fera_project.dto.homepage.CategoryDto;
import az.coders.fera_project.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController("adminCategoryController")
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;
//
//    @GetMapping //getAll
//    public ResponseEntity<List<CategoryDto>> getCategories() {
//        return ResponseEntity.ok(categoryService.getCategories());
//    }
//
//    @GetMapping("/{id}") //getById
//    public ResponseEntity<CategoryDto> getCategory(@PathVariable Integer id) {
//        return ResponseEntity.ok(categoryService.getCategoryById(id));
//    }

    @PostMapping //save Category
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.createCategory(categoryDto) , HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Integer id, @RequestBody CategoryDto Dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, Dto));
    }

    // Добавление изображения к категории
    @PostMapping("/{categoryId}/image")
    public ResponseEntity<Void> addImageToCategory(
            @PathVariable Integer categoryId,
            @RequestParam Integer mediaId) {
        categoryService.addImageToCategory(categoryId, mediaId);
        return ResponseEntity.ok().build();
    }

    // Удаление изображения у категории
    @DeleteMapping("/{categoryId}/image")
    public ResponseEntity<Void> deleteImageFromCategory(@PathVariable Integer categoryId) {
        categoryService.deleteImageFromCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
