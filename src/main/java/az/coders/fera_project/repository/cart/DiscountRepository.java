package az.coders.fera_project.repository.cart;

import az.coders.fera_project.entity.cart.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {

    Optional<Discount> findByCode(String code);  // Метод для поиска по коду
}

