package az.coders.fera_project.controller.admin;


import az.coders.fera_project.entity.cart.Discount;
import az.coders.fera_project.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    // Получить все скидки
    @GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts() {
        List<Discount> discounts = discountService.getAllDiscounts();
        return ResponseEntity.ok(discounts);
    }

    // Получить скидку по коду
    @GetMapping("/{code}")
    public ResponseEntity<Discount> getDiscountByCode(@PathVariable String code) {
        Discount discount = discountService.getDiscountByCode(code);
        return ResponseEntity.ok(discount);
    }

    // Создать новую скидку
    @PostMapping
    public ResponseEntity<Discount> createDiscount(@RequestBody Discount discount) {
        Discount createdDiscount = discountService.createDiscount(discount);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDiscount);
    }

    // Обновить существующую скидку
    @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(@PathVariable Integer id, @RequestBody Discount discountDetails) {
        Discount updatedDiscount = discountService.updateDiscount(id, discountDetails);
        return ResponseEntity.ok(updatedDiscount);
    }

    // Деактивировать скидку
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateDiscount(@PathVariable Integer id) {
        discountService.deactivateDiscount(id);
        return ResponseEntity.noContent().build();
    }

    // Удалить скидку
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Integer id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
}
