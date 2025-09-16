package az.coders.fera_project.service;

import az.coders.fera_project.entity.cart.Discount;

import java.util.List;

public interface DiscountService {
    List<Discount> getAllDiscounts();

    Discount getDiscountByCode(String code);

    Discount createDiscount(Discount discount);

    Discount updateDiscount(Integer id, Discount discountDetails);

    void deactivateDiscount(Integer id);

    void deleteDiscount(Integer id);


}
