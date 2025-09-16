package az.coders.fera_project.service;

import az.coders.fera_project.dto.PaymentDto;
public interface PaymentService {
    void processPayment(Long orderId, PaymentDto paymentDto);

}
