package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.cart.CartDto;
import az.coders.fera_project.service.CartService;
import az.coders.fera_project.service.DiscountService;
import az.coders.fera_project.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final DiscountService discountService;

    private Long getCurrentUserId() {
        Long userId = AuthUtils.extractUserId();
        if (userId == null) {
            throw new IllegalStateException("User ID not found in security context");
        }
        return userId;
    }

    @GetMapping("/me")
    public ResponseEntity<CartDto> getCart() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/me/apply-discount")
    public ResponseEntity<CartDto> applyDiscount(@RequestParam String code) {
        Long userId = getCurrentUserId();
        CartDto updatedCart = cartService.applyDiscount(userId, code);
        return ResponseEntity.ok(updatedCart);
    }

    @GetMapping("/me/grandtotal")
    public ResponseEntity<BigDecimal> getGrandTotal() {
        Long userId = getCurrentUserId();
        BigDecimal grandTotal = cartService.getGrandTotal(userId);
        return ResponseEntity.ok(grandTotal);
    }

    @PostMapping("/me/checkout")
    public ResponseEntity<Void> checkout() {
        Long userId = getCurrentUserId();
        cartService.checkout(userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Добавить товар в корзину
    @PostMapping("/me/add")
    public ResponseEntity<Void> addProductToCart(@RequestParam Integer productId, @RequestParam Integer quantity) {
        Long userId = getCurrentUserId();
        cartService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/me/{cartItemId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Integer cartItemId) {
        Long userId = getCurrentUserId();
        cartService.removeProductFromCart(userId, cartItemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/me/{cartItemId}/move-to-wishlist")
    public ResponseEntity<Void> moveToWishlist(@PathVariable Integer cartItemId) {
        Long userId = getCurrentUserId();
        cartService.moveToWishlist(userId, cartItemId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Переместить несколько товаров в список желаемого
    @PostMapping("/me/move-multiple-to-wishlist")
    public ResponseEntity<Void> moveMultipleToWishlist(@RequestBody List<Long> cartItemIds) {
        Long userId = getCurrentUserId();
        cartService.moveMultipleToWishlist(userId, cartItemIds);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Удалить несколько товаров из корзины
    @DeleteMapping("/me/remove-multiple")
    public ResponseEntity<Void> removeMultipleFromCart(@RequestBody List<Long> cartItemIds) {
        Long userId = getCurrentUserId();
        cartService.removeMultipleFromCart(userId, cartItemIds);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
}
