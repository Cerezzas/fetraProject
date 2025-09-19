package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.cart.CartItemDto;
import az.coders.fera_project.service.CartItemService;
import az.coders.fera_project.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/cart/items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @GetMapping("/{id}")
    public ResponseEntity<CartItemDto> getCartItem(@PathVariable Long id,
                                                   @CookieValue(value = "session-key", required = false) String sessionKey) {
        Long userId = AuthUtils.extractUserId();
        return ResponseEntity.ok(cartItemService.getCartItemById(userId, sessionKey, id));
    }

    @PostMapping
    public ResponseEntity<Void> addItemToCart(@RequestParam Integer productId,
                                              @RequestParam Integer quantity,
                                              @CookieValue(value = "session-key", required = false) String sessionKey) {
        Long userId = AuthUtils.extractUserId();
        cartItemService.addProductToCart(userId, sessionKey, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItem(@PathVariable Integer id,
                                           @CookieValue(value = "session-key", required = false) String sessionKey) {
        Long userId = AuthUtils.extractUserId();
        cartItemService.removeItem(userId, sessionKey, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> removeItems(@RequestBody List<Long> ids,
                                            @CookieValue(value = "session-key", required = false) String sessionKey) {
        Long userId = AuthUtils.extractUserId();
        cartItemService.removeItems(userId, sessionKey, ids);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<Void> updateQuantity(@PathVariable Integer id,
                                               @RequestParam int quantity,
                                               @CookieValue(value = "session-key", required = false) String sessionKey) {
        Long userId = AuthUtils.extractUserId();
        cartItemService.updateQuantity(userId, sessionKey, id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/move-to-wishlist")
    public ResponseEntity<Void> moveToWishlist(@PathVariable Integer id,
                                               @CookieValue(value = "session-key", required = false) String sessionKey) {
        Long userId = AuthUtils.extractUserId();
        cartItemService.moveToWishlist(userId, sessionKey, id);
        return ResponseEntity.ok().build();
    }
}
