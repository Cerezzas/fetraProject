package az.coders.fera_project.service;

import az.coders.fera_project.dto.ReviewDto;
import az.coders.fera_project.dto.product.user.ReviewRequestDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addReview(ReviewRequestDto reviewRequestDto, Integer productId);
    List<ReviewDto> getReviewsByProduct(Integer productId);
//    Double getAverageRating(Integer productId);
    void deleteReview(Long reviewId);  // Новый метод для удаления отзыва
}

