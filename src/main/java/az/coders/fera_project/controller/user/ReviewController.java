package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.ReviewDto;
import az.coders.fera_project.dto.product.user.ReviewRequestDto;
import az.coders.fera_project.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Получение всех отзывов для указанного продукта.
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByProduct(@PathVariable Integer productId) {
        List<ReviewDto> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Добавление нового отзыва к продукту.
     */
    @PostMapping("/add/{productId}")
    public ResponseEntity<ReviewDto> addReview(
            @PathVariable Integer productId,
            @Valid @RequestBody ReviewRequestDto dto
    ) {
        ReviewDto created = reviewService.addReview(dto, productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Удаление отзыва по ID.
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
