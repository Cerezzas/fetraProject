package az.coders.fera_project.repository.cart;

import az.coders.fera_project.entity.cart.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserId(Long userId);
    Optional<Wishlist> findBySessionKey(String sessionKey);

}
