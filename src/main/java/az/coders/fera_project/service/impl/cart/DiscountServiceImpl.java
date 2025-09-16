package az.coders.fera_project.service.impl.cart;

import az.coders.fera_project.entity.cart.Discount;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.cart.DiscountRepository;
import az.coders.fera_project.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    // Получить все скидки
    @Override
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    // Получить скидку по коду
    @Override
    public Discount getDiscountByCode(String code) {
        return discountRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Discount not found"));
    }

    // Создать новую скидку
    @Override
    public Discount createDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    // Обновить существующую скидку
    @Override
    public Discount updateDiscount(Integer id, Discount discountDetails) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount not found"));

        // Обновляем код скидки, если он передан
        if (discountDetails.getCode() != null) {
            discount.setCode(discountDetails.getCode());
        }

        // Обновляем процент скидки, если он передан
        if (discountDetails.getDiscountPercentage() != null) {
            discount.setDiscountPercentage(discountDetails.getDiscountPercentage());
        }

        // Обновляем активность скидки, если она передана
        if (discountDetails.getActive() != null) {
            discount.setActive(discountDetails.getActive());
        }

        // Сохраняем изменения в базе данных
        return discountRepository.save(discount);
    }


    // Деактивировать скидку
    @Override
    public void deactivateDiscount(Integer id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount not found"));
        discount.setActive(false);
        discountRepository.save(discount);
    }

    // Удалить скидку
    @Override
    public void deleteDiscount(Integer id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount not found"));
        discountRepository.delete(discount);
    }
}
