package az.coders.fera_project.repository;

import az.coders.fera_project.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByUserId(Integer userId);  // Найти заказы пользователя

    Optional<Order> findByIdAndUserId(Long id, Long user_id);
}
