package az.coders.fera_project.controller.user;

import az.coders.fera_project.service.WishlistService;
import az.coders.fera_project.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/wishlist")
//@RequiredArgsConstructor
//public class WishlistController {
//
//    private final WishlistService wishlistService;
//
//    @GetMapping
//    public ResponseEntity<List<WishlistItemDto>> getWishlist(
//            @CookieValue(name = "SESSION_KEY", required = false) String sessionKey
//    ) {
//        Long userId = AuthUtils.extractUserId();
//        return ResponseEntity.ok(wishlistService.getWishlistItems(userId, sessionKey));
//    }
//
//    @PostMapping
//    public ResponseEntity<Void> addToWishlist(
//            @RequestParam Integer productId,
//            @CookieValue(name = "SESSION_KEY", required = false) String sessionKey
//    ) {
//        Long userId = AuthUtils.extractUserId();
//        wishlistService.addProductToWishlist(userId, sessionKey, productId);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> removeFromWishlist(
//            @PathVariable Integer id,
//            @CookieValue(name = "SESSION_KEY", required = false) String sessionKey
//    ) {
//        Long userId = AuthUtils.extractUserId();
//        wishlistService.removeProductFromWishlist(userId, sessionKey, id);
//        return ResponseEntity.noContent().build();
//    }
//}
//
