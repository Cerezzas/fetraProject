package az.coders.fera_project.service;

import az.coders.fera_project.dto.homepage.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories();
    CategoryDto getCategoryById(Integer id);
    CategoryDto createCategory(CategoryDto categoryDto);
    void addImageToCategory(Integer categoryId, Integer mediaId);
    void deleteImageFromCategory(Integer categoryId);
    void deleteCategory(Integer id);
    CategoryDto updateCategory(Integer id, CategoryDto categoryDto);
}
