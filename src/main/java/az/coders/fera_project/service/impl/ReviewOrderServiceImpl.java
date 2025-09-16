package az.coders.fera_project.service.impl;

import az.coders.fera_project.config.EnhancedObjectMapper;
import az.coders.fera_project.dto.*;
import az.coders.fera_project.entity.*;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.enums.OrderStatus;
import az.coders.fera_project.exception.BadRequestException;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.AddressRepository;
import az.coders.fera_project.repository.OrderItemRepository;
import az.coders.fera_project.repository.OrderRepository;
import az.coders.fera_project.repository.PaymentRepository;
import az.coders.fera_project.service.ReviewOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ReviewOrderServiceImpl implements ReviewOrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final EnhancedObjectMapper objectMapper;

    @Override
    public ReviewOrderDto getReviewOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        List<OrderItemDto> orderItemDtos = orderItemRepository.findAllByOrderId(orderId).stream()
                .map(this::mapToDto)  // используем маппинг
                .collect(Collectors.toList());

        ShippingAddressDto shippingAddressDto = Optional.ofNullable(order.getShippingAddress())
                .map(addr -> convertToDto(addr))
                .orElseThrow(() -> new NotFoundException("Shipping address not found"));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Payment information not found"));

        String maskedCardNumber = maskCardNumber(payment.getCardNumber());
        PaymentReviewDto paymentReviewDto = new PaymentReviewDto(
                payment.getCardHolderName(),
                maskedCardNumber,
                payment.getPaymentMethod()
        );

        ReviewOrderDto reviewOrderDto = new ReviewOrderDto();
        reviewOrderDto.setOrderItems(orderItemDtos);
        reviewOrderDto.setShippingAddress(shippingAddressDto);
        reviewOrderDto.setPaymentReview(paymentReviewDto);

        return reviewOrderDto;
    }

    private OrderItemDto mapToDto(OrderItem orderItem) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(orderItem.getProduct().getId());
        dto.setProductName(orderItem.getProduct().getName());
        dto.setProductImage(orderItem.getProduct().getImage());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }

    // Пример конвертации Address в ShippingAddressDto
    private ShippingAddressDto convertToDto(Address address) {
        ShippingAddressDto dto = new ShippingAddressDto();
        dto.setName(address.getName());
        dto.setStreet(address.getStreet());
        return dto;
    }

    @Override
    @Transactional
    public void updateShippingAddress(Long orderId, ShippingAddressDto shippingAddressDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        User user = order.getUser();

        Address address = order.getShippingAddress();

        if (address == null) {
            // Если адреса нет, создаём новый
            address = new Address();
            address.setUser(user);
        } else {
            // Проверяем, что адрес принадлежит пользователю
            if (!address.getUser().getId().equals(user.getId())) {
                throw new BadRequestException("Address does not belong to the user");
            }
        }

        // Обновляем поля адреса из DTO, только если они не null
        if (shippingAddressDto.getName() != null) {
            address.setName(shippingAddressDto.getName());
        }

        if (shippingAddressDto.getStreet() != null) {
            address.setStreet(shippingAddressDto.getStreet());
        }


        // Снимаем дефолтный флаг с текущего дефолтного адреса пользователя (если есть)
        addressRepository.findByUserIdAndIsDefaultTrue(Long.valueOf(user.getId()))
                .ifPresent(existing -> {
                    existing.setDefault(false);
                    addressRepository.save(existing);
                });

        // Устанавливаем текущий адрес дефолтным
        address.setDefault(true);
        address = addressRepository.save(address);

        // Назначаем адрес заказу
        order.setShippingAddress(address);
        orderRepository.save(order);
    }


    @Override
    @Transactional
    public void updatePaymentMethod(Long orderId, PaymentDto paymentDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Payment information not found"));

        // Обновляем имя и номер
        if (paymentDto.getCardHolderName() != null) {
            payment.setCardHolderName(paymentDto.getCardHolderName());
        }
        if (paymentDto.getCardNumber() != null) {
            // Сохраняем уже маскированный
            payment.setCardNumber(maskCardNumber(paymentDto.getCardNumber()));
        }

        // Обновляем способ оплаты, если он указан
        if (paymentDto.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentDto.getPaymentMethod());
        }


        paymentRepository.save(payment);

        order.setPayment(payment);
        orderRepository.save(order);
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        if (cardNumber.contains("*")) return cardNumber; // уже маскировано

        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }





    @Override
    @Transactional
    public void placeOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // 1. Получаем все товары в заказе
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            int orderedQty = item.getQuantity();

            Integer currentStock = product.getStockQuantity();
            if (currentStock == null) currentStock = 0;

            // 2. Проверка на наличие
            if (currentStock < orderedQty) {
                throw new BadRequestException("Not enough stock for product: " + product.getName());
            }

            // 3. Уменьшаем количество
            product.setStockQuantity(currentStock - orderedQty);
        }

        // 4. Обновляем статус заказа
        order.setStatus(OrderStatus.PLACED);

        orderRepository.save(order);
    }

}
