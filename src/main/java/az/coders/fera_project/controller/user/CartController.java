package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.cart.CartDto;
import az.coders.fera_project.service.CartService;
import az.coders.fera_project.service.DiscountService;
import az.coders.fera_project.util.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import jakarta.servlet.http.Cookie;


@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private static final String SESSION_COOKIE_NAME = "SESSION_KEY";

    private final CartService cartService;

    // ---------- вспомогалки ----------
    private String ensureSessionKey(HttpServletRequest request, HttpServletResponse response, Long userId) {
        if (userId != null) return null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (SESSION_COOKIE_NAME.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    return c.getValue();
                }
            }
        }
        String header = request.getHeader("X-Session-Key");
        if (header != null && !header.isBlank()) return header;

        String newKey = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, newKey);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
        return newKey;
    }

    private Long getCurrentUserIdOrNull() {
        return AuthUtils.extractUserId(); // возвращает null для гостя
    }

    // ---------- endpoints ----------
    @GetMapping("/me")
    public ResponseEntity<CartDto> getCart(HttpServletRequest request, HttpServletResponse response) {
        Long userId = getCurrentUserIdOrNull();
        String sessionKey = ensureSessionKey(request, response, userId);
        return ResponseEntity.ok(cartService.getCart(userId, sessionKey));
    }

    @PostMapping("/me/apply-discount")
    public ResponseEntity<CartDto> applyDiscount(@RequestParam String code,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        Long userId = getCurrentUserIdOrNull();
        String sessionKey = ensureSessionKey(request, response, userId);
        CartDto updated = cartService.applyDiscount(userId, sessionKey, code);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/me/grandtotal")
    public ResponseEntity<BigDecimal> getGrandTotal(HttpServletRequest request, HttpServletResponse response) {
        Long userId = getCurrentUserIdOrNull();
        String sessionKey = ensureSessionKey(request, response, userId);
        BigDecimal total = cartService.getGrandTotal(userId, sessionKey);
        return ResponseEntity.ok(total);
    }

    // Checkout — только для залогиненных
    @PostMapping("/me/checkout")
    public ResponseEntity<Void> checkout() {
        Long userId = getCurrentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        cartService.checkout(userId, null);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Добавить товар — поддерживаем гостя (создаём sessionKey, если нужно)
    @PostMapping("/me/add")
    public ResponseEntity<Void> addProductToCart(@RequestParam Integer productId,
                                                 @RequestParam Integer quantity,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        Long userId = getCurrentUserIdOrNull();
        String sessionKey = ensureSessionKey(request, response, userId);
        cartService.addProductToCart(userId, sessionKey, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/me/{cartItemId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Integer cartItemId,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        Long userId = getCurrentUserIdOrNull();
        String sessionKey = ensureSessionKey(request, response, userId);
        cartService.removeProductFromCart(userId, sessionKey, cartItemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/me/{cartItemId}/move-to-wishlist")
    public ResponseEntity<Void> moveToWishlist(@PathVariable Integer cartItemId,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        Long userId = getCurrentUserIdOrNull();
        String sessionKey = ensureSessionKey(request, response, userId);
        cartService.moveToWishlist(userId, sessionKey, cartItemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me/move-multiple-to-wishlist")
    public ResponseEntity<Void> moveMultipleToWishlist(@RequestBody List<Long> cartItemIds,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        Long userId = getCurrentUserIdOrNull();
        String sessionKey = ensureSessionKey(request, response, userId);
        cartService.moveMultipleToWishlist(userId, sessionKey, cartItemIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/remove-multiple")
    public ResponseEntity<Void> removeMultipleFromCart(@RequestBody List<Long> cartItemIds,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        Long userId = getCurrentUserIdOrNull();
        String sessionKey = ensureSessionKey(request, response, userId);
        cartService.removeMultipleFromCart(userId, sessionKey, cartItemIds);
        return ResponseEntity.noContent().build();
    }
}



//    // Применить скидку по промокоду
//    @PostMapping("/me/apply-discount")
//    public ResponseEntity<String> applyDiscount(@RequestParam String code) {
//        Long userId = getCurrentUserId();
//
//        try {
//            cartService.applyDiscount(userId, code);
//            return ResponseEntity.ok("Discount applied successfully");
//        } catch (NotFoundException e) {
//            return ResponseEntity.badRequest().body("Invalid or inactive discount code");
//        }
//    }


//    // Получить итоги корзины (сумма товаров, налог, доставка)
//    @GetMapping("/me/subtotal")
//    public ResponseEntity<TotalsDto> getCartTotals() {
//        Long userId = getCurrentUserId();
//        TotalsDto totals = cartService.getCartTotals(userId);
//        return ResponseEntity.ok(totals);
//    }

//    // Оформить заказ (чекаут)
//    @PostMapping("/me/checkout")
//    public ResponseEntity<Void> checkout() {
//        Long userId = getCurrentUserId();
//        cartService.checkout(userId);  // Передаем только userId
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

//    @GetMapping("/me/debug")
//    public ResponseEntity<?> debugPrincipal() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println("Principal class: " + principal.getClass().getName());
//        System.out.println("Principal value: " + principal);
//        return ResponseEntity.ok("Check your console for output.");
//    }

