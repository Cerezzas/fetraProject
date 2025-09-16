package az.coders.fera_project.repository;

import az.coders.fera_project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Метод для получения продуктов по списку их ID
//    List<Product> findAllById(Integer id);

    // Метод для получения 4 популярных продуктов (по рейтингу, например)
//    // Метод для получения 4 товаров с наивысшим рейтингом
    List<Product> findTop4ByOrderByAverageRatingDesc();
    List<Product> findByNameContainingIgnoreCase(String name);



}
