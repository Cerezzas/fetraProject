package az.coders.fera_project.repository;

import az.coders.fera_project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name); // Метод для поиска категории по имени
    // Метод для получения первых 3 категорий по ID в порядке возрастания
    List<Category> findTop3ByOrderByIdAsc();
    List<Category> findAllByNameIn(List<String> names);

}

