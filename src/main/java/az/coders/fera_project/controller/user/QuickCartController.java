package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.cart.QuickCartDto;
import az.coders.fera_project.service.CartService;
import az.coders.fera_project.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
    public ResponseEntity<QuickCartDto> getQuickCart(
            @CookieValue(name = "SESSION_KEY", required = false) String sessionKey
    ) {
        Long userId = AuthUtils.extractUserId();
        return ResponseEntity.ok(cartService.getQuickCart(userId, sessionKey));
    }

}
