package az.coders.fera_project.service.impl;

import az.coders.fera_project.dto.PaymentDto;
import az.coders.fera_project.entity.Order;
import az.coders.fera_project.entity.Payment;
import az.coders.fera_project.enums.PaymentStatus;

import org.springframework.transaction.annotation.Transactional;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.OrderRepository;
import az.coders.fera_project.repository.PaymentRepository;
import az.coders.fera_project.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void processPayment(Long orderId, PaymentDto paymentDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setIsPaid(true);
        payment.setStatus(PaymentStatus.PAID); // или APPROVED / COMPLETED — по логике

        payment.setPaidAt(LocalDateTime.now()); // текущая дата и время

        // Сохраняем маскированный номер
        if (paymentDto.getCardNumber() != null) {
            payment.setCardNumber(maskCardNumber(paymentDto.getCardNumber()));
        }

        if (paymentDto.getCardHolderName() != null) {
            payment.setCardHolderName(paymentDto.getCardHolderName());
        }

        paymentRepository.save(payment);
        order.setPayment(payment);
        orderRepository.save(order);
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        if (cardNumber.contains("*")) return cardNumber;

        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }


//    @Override
//    public PaymentDto getPaymentForm(Long orderId) {
//        orderRepository.findById(orderId)
//                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));
//
//        // Возвращаем форму оплаты с дефолтным методом
//        PaymentDto dto = new PaymentDto();
//        dto.setPaymentMethod(PaymentMethod.CREDIT_CARD);
//        return dto;
//    }
}
