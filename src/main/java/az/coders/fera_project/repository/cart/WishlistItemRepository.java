package az.coders.fera_project.repository.cart;

import az.coders.fera_project.entity.cart.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
}
