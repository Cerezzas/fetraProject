package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.cart.QuickCartDto;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/quick-cart")
@RequiredArgsConstructor
public class QuickCartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<QuickCartDto> getQuickCart() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId = null;
        if (principal instanceof User user) {
            userId = Long.valueOf(user.getId());
        } else if (principal instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id != null) {
                userId = Long.valueOf(id.toString());
            }
        }

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(cartService.getQuickCart(userId));
    }


}


