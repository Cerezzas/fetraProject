package az.coders.fera_project.service.impl;

import az.coders.fera_project.dto.product.user.ReviewRequestDto;
import az.coders.fera_project.entity.Product;
import az.coders.fera_project.dto.ReviewDto;
import az.coders.fera_project.entity.Review;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.ProductRepository;
import az.coders.fera_project.repository.ReviewRepository;
import az.coders.fera_project.repository.register.UserRepository;
import az.coders.fera_project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewDto addReview(ReviewRequestDto dto, Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        User user = getCurrentUser();

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);

        // Логирование для отладки
        System.out.println("Received userName from request: " + dto.getUserName());

        // Имя пользователя: если передано, используем его, иначе используем имя текущего пользователя.
        String userNameToSet = dto.getUserName();
        if (userNameToSet != null && !userNameToSet.isBlank()) {
            review.setUserName(userNameToSet);
        } else {
            review.setUserName(user.getUsername());
        }

        // Email: если передан, сохраняем, иначе используем email текущего пользователя.
        String emailToSet = dto.getEmail();
        if (emailToSet != null && !emailToSet.isBlank()) {
            review.setEmail(emailToSet);
        } else {
            review.setEmail(user.getEmail());
        }

        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);
        updateProductReviewStats(product);

        return mapToReviewDto(review);
    }




    @Override
    public List<ReviewDto> getReviewsByProduct(Integer productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(this::mapToReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        Product product = review.getProduct();
        reviewRepository.delete(review);

        updateProductReviewStats(product);
    }

    private void updateProductReviewStats(Product product) {
        List<Review> reviews = reviewRepository.findByProductId(product.getId());
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        int reviewCount = reviews.size();

        product.setAverageRating(averageRating);
        product.setReviewCount(reviewCount);

        productRepository.save(product);
    }

    private ReviewDto mapToReviewDto(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getUserName(),
                review.getComment(),
                review.getRating(),
                review.getCreatedAt()
        );
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotFoundException("User not authenticated");
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new NotFoundException("User not found by username"));
        } else if (principal instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id != null) {
                return userRepository.findById(Long.valueOf(id.toString()))
                        .orElseThrow(() -> new NotFoundException("User not found by id"));
            }
        }

        throw new NotFoundException("Cannot extract user from context");
    }

}
