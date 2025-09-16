package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.cart.CartItemDto;
import az.coders.fera_project.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/cart/items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    // Получить конкретный товар из корзины по ID
    @GetMapping("/{id}")
    public ResponseEntity<CartItemDto> getCartItem(@PathVariable Long id) {
        CartItemDto item = cartItemService.getCartItemById(id);
        return ResponseEntity.ok(item);
    }

    // Добавить товар в корзину
    @PostMapping
    public ResponseEntity<Void> addItemToCart(@RequestParam Integer productId,
                                              @RequestParam Integer quantity) {
        // Извлекаем principal из контекста безопасности
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("PRINCIPAL: " + principal.getClass() + " -> " + principal);

        // Проверяем, если principal это LinkedHashMap
        if (principal instanceof LinkedHashMap) {
            LinkedHashMap<?, ?> principalMap = (LinkedHashMap<?, ?>) principal;

            // Извлекаем ID пользователя как Integer и конвертируем в Long
            Integer userId = (Integer) principalMap.get("id");
            Long userLongId = Long.valueOf(userId);  // Преобразуем в Long

            // Теперь userLongId можно использовать для дальнейшей логики
            cartItemService.addProductToCart(userLongId.intValue(), productId, quantity);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            // Если principal не является LinkedHashMap, возвращаем ошибку авторизации
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    // Удалить товар из корзины по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItem(@PathVariable Integer id) {
        cartItemService.removeItem(id);
        return ResponseEntity.noContent().build();
    }

    // Удалить несколько товаров из корзины
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> removeItems(@RequestBody List<Long> ids) {
        cartItemService.removeItems(ids);
        return ResponseEntity.noContent().build();
    }

    // Обновить количество товара в корзине
    @PutMapping("/{id}/quantity")
    public ResponseEntity<Void> updateQuantity(@PathVariable Integer id,
                                               @RequestParam int quantity) {
        cartItemService.updateQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }

    // Переместить товар из корзины в вишлист
    @PostMapping("/{id}/move-to-wishlist")
    public ResponseEntity<Void> moveToWishlist(@PathVariable Integer id) {
        cartItemService.moveToWishlist(id);
        return ResponseEntity.ok().build();
    }
}
