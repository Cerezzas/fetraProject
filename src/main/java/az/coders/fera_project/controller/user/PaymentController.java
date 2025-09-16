package az.coders.fera_project.controller.user;

import az.coders.fera_project.dto.PaymentDto;
import az.coders.fera_project.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

//    // ✅ Получить форму оплаты (по ID заказа из ссылки)
//    @GetMapping("/form")
//    public ResponseEntity<PaymentDto> getPaymentForm(@RequestParam Long orderId) {
//        return ResponseEntity.ok(paymentService.getPaymentForm(orderId));
//    }

    // ✅ Обработка платежа (с валидацией)
    @PostMapping("/process")
    public ResponseEntity<Void> processPayment(
            @RequestParam Long orderId,
            @RequestBody @Valid PaymentDto paymentDto
    ) {
        paymentService.processPayment(orderId, paymentDto);
        return ResponseEntity.ok().build();
    }
}
