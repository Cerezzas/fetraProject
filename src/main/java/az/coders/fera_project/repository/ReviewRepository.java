package az.coders.fera_project.repository;

import az.coders.fera_project.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Метод для получения отзывов по ID продукта
    List<Review> findByProductId(Integer productId);

    // Метод для получения последних 3 отзывов
    List<Review> findTop3ByOrderByCreatedAtDesc();
}
