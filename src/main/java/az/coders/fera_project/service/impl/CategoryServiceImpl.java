package az.coders.fera_project.service.impl;

import az.coders.fera_project.config.EnhancedObjectMapper;
import az.coders.fera_project.dto.homepage.CategoryDto;
import az.coders.fera_project.entity.Category;
import az.coders.fera_project.entity.Media;
import az.coders.fera_project.entity.Product;
import az.coders.fera_project.enums.ErrorCode;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.CategoryRepository;
import az.coders.fera_project.repository.MediaRepository;
import az.coders.fera_project.repository.ProductRepository;
import az.coders.fera_project.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final MediaRepository mediaRepository;
    private final EnhancedObjectMapper mapper;
    @Override
    public List<CategoryDto> getCategories() {
        return Arrays.asList(mapper.convertValue(categoryRepository.findAll(), CategoryDto[].class));

    }

    @Override
    public CategoryDto getCategoryById(Integer id) {
        return mapper.convertValue(findById(id), CategoryDto.class);
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        // Создаем новую категорию
        Category category = new Category();
        category.setName(categoryDto.getName());
        //category.setImageUrl(categoryDto.getImageUrl());

        // Сохраняем категорию в базу данных
        Category savedCategory = categoryRepository.save(category);

        // Если есть продукты, связываем их с категорией
        List<Product> products = productRepository.findAll(); // Тут можно получить все продукты, или определенные
        for (Product product : products) {
            // Здесь можно добавить логику для связывания продуктов с категорией
            if (product.getCategory() == null) {
                product.setCategory(savedCategory);
            }
        }
        productRepository.saveAll(products);

        // Возвращаем сохраненную категорию в DTO
        return mapper.convertValue(savedCategory, CategoryDto.class);
    }
//    @Override
//    public CategoryDto createCategory(CategoryDto categoryDto) {
//        return mapper.convertValue(categoryRepository.save(mapper.convertValue(categoryDto, Category.class)), CategoryDto.class);
//    }

    @Override
    public void addImageToCategory(Integer categoryId, Integer mediaId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new NotFoundException("Media not found"));

        category.setImage(media);
        categoryRepository.save(category);
    }

    @Override
    public void deleteImageFromCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        category.setImage(null);
        categoryRepository.save(category);
    }


    @Override
    public void deleteCategory(Integer id) {
        findById(id);
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto) {
        Category existingCategory = findById(id);

        // Обновляем только переданные поля
        if (categoryDto.getName() != null) {
            existingCategory.setName(categoryDto.getName());
        }

//        if (categoryDto.getImageUrl() != null) {
//            existingCategory.setImageUrl(categoryDto.getImageUrl());
//        }

        // Сохраняем изменения
        Category updated = categoryRepository.save(existingCategory);
        return mapper.convertValue(updated, CategoryDto.class);
    }


    private Category findById(Integer id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
    }
}
