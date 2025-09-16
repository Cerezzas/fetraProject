package az.coders.fera_project.repository.cart;

import az.coders.fera_project.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository <CartItem, Long> {

     Optional<CartItem> findByCartIdAndProductId(Long cartId, Integer productId);
 //   CartItem deleteAllByCartId(Integer userId);
    List<CartItem> findAllByCartId(Long cartId);
//    void deleteAllByCartId(Long cartId);
@Modifying
@Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
void deleteAllByCartId(@Param("cartId") Long cartId);
    Optional<CartItem> findByIdAndCartId(Integer id, Long cartId);

}
