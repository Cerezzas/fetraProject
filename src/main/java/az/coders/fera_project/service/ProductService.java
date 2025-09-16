package az.coders.fera_project.service;

import az.coders.fera_project.dto.homepage.ProductMainPageDto;
import az.coders.fera_project.dto.product.user.ProductDetailsDto;
import az.coders.fera_project.dto.product.user.ProductFilterRequestDto;
import az.coders.fera_project.dto.product.admin.ProductRequestDto;

import java.util.List;

public interface ProductService {

    List<ProductDetailsDto> getProducts();
    ProductDetailsDto getProductById(Integer id);
    ProductDetailsDto createProduct(ProductRequestDto dto);
    void addImageToProduct(Integer productId, Integer mediaId);
    void deleteImageFromProduct(Integer productId);
    ProductDetailsDto updateProduct(Integer id, ProductRequestDto dto);
    void deleteProduct(Integer id);
    List<ProductMainPageDto> filterProducts(ProductFilterRequestDto request);



}
