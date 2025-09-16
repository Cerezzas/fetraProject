package az.coders.fera_project.service.impl;

import az.coders.fera_project.config.EnhancedObjectMapper;
import az.coders.fera_project.dto.homepage.CategoryDto;
import az.coders.fera_project.dto.homepage.HomePageDto;
import az.coders.fera_project.dto.homepage.ProductMainPageDto;
import az.coders.fera_project.dto.homepage.ReviewMainPageDto;
import az.coders.fera_project.repository.CategoryRepository;
import az.coders.fera_project.repository.ProductRepository;
import az.coders.fera_project.repository.ReviewRepository;
import az.coders.fera_project.service.HomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomePageServiceImpl implements HomePageService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EnhancedObjectMapper mapper;


    @Value("${homepage.instagram.link}")
    private String instagramLink;

    @Override
    public String getInstagramLink() {
        return instagramLink;
    }

    @Override
    public HomePageDto getHomePageData() {
        List<CategoryDto> categories = mapper.convertList(
                categoryRepository.findTop3ByOrderByIdAsc(), CategoryDto.class);

        List<ProductMainPageDto> popularProducts = mapper.convertList(
                productRepository.findTop4ByOrderByAverageRatingDesc(), ProductMainPageDto.class);

        List<ReviewMainPageDto> reviews = mapper.convertList(
                reviewRepository.findTop3ByOrderByCreatedAtDesc(), ReviewMainPageDto.class);

        return new HomePageDto(categories, popularProducts, reviews, instagramLink);
    }
//
//    @Override
//    public HomePageDto getHomePageData() {
//        // Получаем список категорий (например, первые 3)
//        List<CategoryDto> categories = categoryRepository.findTop3ByOrderByIdAsc()
//                .stream()
//                .map(category -> new CategoryDto(category.getName(), category.getImageUrl()))
//                .collect(Collectors.toList());
//
//        // Получаем список популярных товаров (например, первые 4)
//        List<ProductMainPageDto> popularProducts = productRepository.findTop4ByOrderByAverageRatingDesc()
//                .stream()
//                .map(product -> new ProductMainPageDto(
//                        product.getName(),
//                        product.getPrice(),
//                        product.getOldPrice(),
//                        product.getMainImageUrl(),
//                        product.getAverageRating()))
//                .collect(Collectors.toList());
//
//        // Получаем последние 3 отзыва
//        List<ReviewMainPageDto> reviews = reviewRepository.findTop3ByOrderByCreatedAtDesc()
//                .stream()
//                .map(review -> new ReviewMainPageDto(
//                        review.getAverageRating(),
//                        review.getComment(),
//                        review.getUser().getUsername(),
//                        review.getCreatedAt()))
//                .collect(Collectors.toList());
//
//        // Возвращаем DTO с уже подставленным полем instagramLink (из @Value)
//        return new HomePageDto(categories, popularProducts, reviews, instagramLink);
//    }

}

