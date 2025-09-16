package az.coders.fera_project.service.impl.cart;

import az.coders.fera_project.dto.cart.CartDto;
import az.coders.fera_project.exception.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

import az.coders.fera_project.dto.cart.CartItemDto;
import az.coders.fera_project.dto.cart.QuickCartDto;
import az.coders.fera_project.dto.cart.TotalsDto;
import az.coders.fera_project.entity.Order;
import az.coders.fera_project.entity.OrderItem;
import az.coders.fera_project.entity.Product;
import az.coders.fera_project.entity.cart.*;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.enums.OrderStatus;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.*;
import az.coders.fera_project.repository.cart.*;
import az.coders.fera_project.repository.register.UserRepository;
import az.coders.fera_project.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final DiscountRepository discountRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
//    private final EnhancedObjectMapper mapper;

// Получить корзину или создать новую, если её нет

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException("User not found"));
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setSubtotal(BigDecimal.ZERO);
                    cart.setTax(BigDecimal.ZERO);
                    cart.setTotal(BigDecimal.ZERO);
                    return cartRepository.save(cart);
                });
    }

    // Вспомогательный метод: расчет subtotal
    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    //    Основной метод, возвращает
//    корзину с
//    расчетами
    @Override
    public CartDto getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);

        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.15)); // 15% налог
        BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.valueOf(100)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(10);

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (cart.getDiscount() != null) {
            discountAmount = subtotal.multiply(cart.getDiscount().getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100));
        }

        BigDecimal grandTotal = subtotal.add(tax).add(deliveryFee).subtract(discountAmount);

        List<CartItemDto> products = Optional.ofNullable(cart.getCartItems())
                .orElse(Collections.emptyList()) // защищает от null
                .stream()
                .map(item -> new CartItemDto(
                        item.getId(),
                        item.getProduct().getName(),
                        item.getProduct().getImage(),
                        BigDecimal.valueOf(item.getProduct().getPrice()),
                        item.getQuantity(),
                        BigDecimal.valueOf(item.getProduct().getPrice())
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());


        CartDto response = new CartDto();
        response.setProducts(products);
        response.setSubtotal(subtotal);
        response.setTaxes(tax);
        response.setDeliveryFee(deliveryFee);
        response.setDiscount(discountAmount); // ✅ Добавили
        response.setGrandTotal(grandTotal);

        return response;
    }

    @Override
    public CartDto applyDiscount(Long userId, String discountCode) {
        Cart cart = getOrCreateCart(userId);
        Discount discount = discountRepository.findByCode(discountCode).orElseThrow(() -> new NotFoundException("Discount code not found"));
        if (!discount.isActive()) {
            throw new IllegalStateException("Discount is not active");
        }
        cart.setDiscount(discount);
        cartRepository.save(cart);
        return getCart(userId); // возвращаем обновленную корзину с новым grandTotal }
    }

    @Override
    public BigDecimal getGrandTotal(Long userId) {
        CartDto cartDto = getCart(userId);
        return cartDto.getGrandTotal();
    }


    @Transactional
    public void checkout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = getOrCreateCart(userId);

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Проверка наличия товаров на складе
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
            if (item.getQuantity() > stock) {
                throw new BadRequestException("Not enough stock for product: " + product.getName() +
                        ". Only " + stock + " left.");
            }
        }

        // Создаём заказ
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        TotalsDto totals = getCartTotals(userId);
        order.setTotalAmount(totals.getGrandTotal());

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    BigDecimal price = BigDecimal.valueOf(cartItem.getProduct().getPrice());
                    orderItem.setPrice(price);

                    // Уменьшаем количество на складе
                    Product product = cartItem.getProduct();
                    int updatedStock = product.getStockQuantity() - cartItem.getQuantity();
                    product.setStockQuantity(Math.max(0, updatedStock)); // не уйдёт в минус

                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        orderRepository.save(order);
        // продукты уже автоматически обновятся, если правильно настроен каскад или сохранены отдельно

        // очищаем корзину
        cart.getCartItems().clear();
        cart.setDiscount(null);
        cartRepository.save(cart);
    }




    public TotalsDto getCartTotals(Long userId) {
        Cart cart = getOrCreateCart(userId);
        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.15));
        BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.valueOf(100)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(10);

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (cart.getDiscount() != null) {
            discountAmount = subtotal.multiply(cart.getDiscount().getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100));
        }

        BigDecimal grandTotal = subtotal.add(tax).add(deliveryFee).subtract(discountAmount);

        return new TotalsDto(subtotal, tax, deliveryFee, discountAmount, grandTotal);
    }

    @Transactional
    @Override
    public void addProductToCart(Long userId, Integer productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        int currentQuantityInCart = cartItem != null ? cartItem.getQuantity() : 0;
        int totalRequested = currentQuantityInCart + quantity;
        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        // Проверка на наличие
        if (totalRequested > stock) {
            throw new BadRequestException("Only " + stock + " items left in stock for product: " + product.getName());
        }

        if (cartItem != null) {
            cartItem.setQuantity(totalRequested);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        cartItemRepository.save(cartItem);
    }



    //  Метод для удаления товара из корзины
    @Override
    public void removeProductFromCart(Long userId, Integer cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    //       Метод для перемещения товара в список желаемого
    @Override
    public void moveToWishlist(Long userId, Integer cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElseGet(() -> {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setUser(cart.getUser());
            return wishlistRepository.save(newWishlist);
        });
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setProduct(cartItem.getProduct());
        wishlistItem.setWishlist(wishlist);
        wishlistItemRepository.save(wishlistItem);
        cartItemRepository.delete(cartItem);
    }


    // Метод для перемещения нескольких товаров в список желаемого
    @Override
    public void moveMultipleToWishlist(Long userId, List<Long> cartItemIds) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);
        cartItems.forEach(cartItem -> {
            Wishlist wishlist = wishlistRepository.findByUserId(userId).orElseGet(() -> {
                Wishlist newWishlist = new Wishlist();
                newWishlist.setUser(cart.getUser());
                return wishlistRepository.save(newWishlist);
            });
            WishlistItem wishlistItem = new WishlistItem();
            wishlistItem.setProduct(cartItem.getProduct());
            wishlistItem.setWishlist(wishlist);
            wishlistItemRepository.save(wishlistItem);
            cartItemRepository.delete(cartItem);
        });
    }


    //    Метод для удаления нескольких товаров из корзины
    @Override
    public void removeMultipleFromCart(Long userId, List<Long> cartItemIds) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);
        cartItems.forEach(cartItem -> cartItemRepository.delete(cartItem));
    }

//
    //       Метод для получения быстрого отображения корзины
    @Override
    public QuickCartDto getQuickCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItemDto> items = Optional.ofNullable(cart.getCartItems())
                .orElse(Collections.emptyList()).stream().map(item -> {
                    Product product = item.getProduct(); // Преобразуем цену из Double в BigDecimal
                    BigDecimal productPrice = BigDecimal.valueOf(product.getPrice());
                    return new CartItemDto(item.getId(), product.getName(), product.getImage(), productPrice,
                            //                                 Используем BigDecimal для цены
                            item.getQuantity(),
                            productPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
                }).collect(Collectors.toList());
        BigDecimal subtotal = items.stream()
                .map(CartItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new QuickCartDto(subtotal, items);
    }
}


//    Метод для применения скидки
//    @Override
//    public void applyDiscount(Long userId, String discountCode) {
//        Cart cart = getOrCreateCart(userId); // Проверяем наличие скидки по коду
//        Discount discount = discountRepository.findByCode(discountCode)
//                .orElseThrow(() -> new NotFoundException("Discount not found")); // Применяем скидку
//        if (discount.isActive()) {
//            BigDecimal subtotal = cart.getSubtotal();
//            BigDecimal discountAmount = subtotal.multiply(discount.getDiscountPercentage())
//                    .divide(BigDecimal.valueOf(100)); // Перерасчет итоговой суммы с учетом скидки
//            BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.15));
//            BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.valueOf(100)) >= 0 ?
//                    BigDecimal.ZERO : BigDecimal.valueOf(10);
//            BigDecimal grandTotal = subtotal.subtract(discountAmount).add(tax).add(deliveryFee);
//            //               Обновляем данные корзины
//            cart.setDiscount(discount);
//            cart.setTotal(grandTotal);
//            cartRepository.save(cart);
//        }
//    }
//

//
//
//            @Override
//    public TotalsDto getCartTotals(Long userId) {
//        Cart cart = getOrCreateCart(userId);
//
//        // НЕ использовать mapper.convertValue
//        // Используем свой метод конвертации в DTO:
//        CartDto cartDto = convertToCartDto(cart);
//
//        BigDecimal subtotal = cartDto.getItems().stream()
//                .map(CartItemDto::getTotalPrice)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal taxes = subtotal.multiply(BigDecimal.valueOf(0.15));
//        BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.valueOf(100)) >= 0
//                ? BigDecimal.ZERO
//                : BigDecimal.valueOf(10);
//
//        BigDecimal discount = cartDto.getDiscount() != null ? cartDto.getDiscount() : BigDecimal.ZERO;
//
//        BigDecimal grandTotal = subtotal.subtract(discount).add(taxes).add(deliveryFee);
//
//        return new TotalsDto(subtotal.doubleValue(), taxes.doubleValue(), deliveryFee.doubleValue(), grandTotal.doubleValue());
//    }
//
//
//    @Override
//    @Transactional
//    public void checkout(Long userId, OrderDto orderDto) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//
//        Cart cart = getOrCreateCart(userId);
//
//        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
//            throw new NotFoundException("Cart is empty");
//        }
//
//        Order order = new Order();
//        order.setUser(user);
//
//        // Добавлено: получение выбранного адреса доставки из пользователя
//        Address selectedAddress = user.getSelectedAddress();
//        if (selectedAddress == null) {
//            throw new BadRequestException("No selected address found for this user");
//        }
//        order.setShippingAddress(selectedAddress);
//
//        order.setStatus(OrderStatus.PENDING);
//        order.setOrderDate(LocalDateTime.now());
//
//        BigDecimal subtotal = cart.getCartItems().stream()
//                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice())
//                        .multiply(BigDecimal.valueOf(item.getQuantity())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        Discount appliedDiscount = null;
//        BigDecimal discountAmount = BigDecimal.ZERO;
//
//        // Попытка взять скидку из запроса
//        if (orderDto.getDiscountCode() != null && !orderDto.getDiscountCode().isBlank()) {
//            appliedDiscount = discountRepository.findByCode(orderDto.getDiscountCode())
//                    .orElseThrow(() -> new NotFoundException("Invalid discount code"));
//        }
//        // Или из корзины, если в запросе скидки нет
//        else if (cart.getDiscount() != null) {
//            appliedDiscount = cart.getDiscount();
//        }
//
//        // Расчет суммы со скидкой (если нужно)
//        // if (appliedDiscount != null) {
//        //     discountAmount = subtotal.multiply(appliedDiscount.getDiscountPercentage())
//        //             .divide(BigDecimal.valueOf(100));
//        //     order.setDiscount(appliedDiscount);
//        // }
//
//        BigDecimal totalAmount = subtotal.subtract(discountAmount);
//        BigDecimal taxes = calculateTaxes(totalAmount);
//        BigDecimal deliveryFee = calculateDeliveryFee(cart);
//        BigDecimal grandTotal = totalAmount.add(taxes).add(deliveryFee);
//
//        order.setTotalAmount(grandTotal);
//
//        Order savedOrder = orderRepository.save(order);
//
//        for (CartItem cartItem : cart.getCartItems()) {
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(savedOrder);
//            orderItem.setProduct(cartItem.getProduct());
//            orderItem.setQuantity(cartItem.getQuantity());
//            orderItem.setPrice(BigDecimal.valueOf(cartItem.getProduct().getPrice()));
//            orderItemRepository.save(orderItem);
//        }
//
//        clearCart(userId);
//    }
//
//
//
//    private BigDecimal calculateTaxes(BigDecimal totalAmount) {
//        return totalAmount.multiply(BigDecimal.valueOf(0.1));
//    }
//
//    private BigDecimal calculateDeliveryFee(Cart cart) {
//        return BigDecimal.valueOf(5);
//    }
//
//    @Override
//    public void clearCart(Long userId) {
//        Cart cart = getOrCreateCart(userId);
//        cartItemRepository.deleteAllByCartId(cart.getId());
//    }
//
//    @Override
//    public QuickCartDto getQuickCart(Long userId) {
//        Cart cart = getOrCreateCart(userId);
//
//        List<CartItemDto> items = Optional.ofNullable(cart.getCartItems())
//                .orElse(Collections.emptyList())
//                .stream()
//                .map(item -> {
//                    Product product = item.getProduct();
//                    return new CartItemDto(
//                            item.getId(),
//                            product.getName(),
//                            product.getImage(),
//                            product.getPrice(),
//                            item.getQuantity(),
//                            BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity()))
//                    );
//                })
//                .toList();
//
//        int totalItems = items.stream()
//                .mapToInt(CartItemDto::getQuantity)
//                .sum();
//
//        BigDecimal subtotal = items.stream()
//                .map(CartItemDto::getTotalPrice)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        return new QuickCartDto(totalItems, items, subtotal);
//    }
//
//
//    @Override
//    public CartDto getCart(Long userId) {
//        Cart cart = getOrCreateCart(userId);
//        return convertToCartDto(cart);
//    }

//    private CartDto convertToCartDto (Cart cart){
//        List<CartItemDto> itemDtos = Optional.ofNullable(cart.getCartItems())
//                .orElse(Collections.emptyList())
//                .stream()
//                .map(item -> {
//                    Product product = item.getProduct();
//                    return new CartItemDto(
//                            item.getId(), // ✅ передаём ID CartItem
//                            product.getName(),
//                            product.getImageUrl(),
//                            product.getPrice(),
//                            item.getQuantity(),
//                            BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity()))
//                    );
//                })
//                .collect(Collectors.toList());
//
//        BigDecimal subtotal = itemDtos.stream()
//                .map(CartItemDto::getTotalPrice)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.15));
//        BigDecimal discount = cart.getDiscount() != null
//                ? subtotal.multiply(cart.getDiscount().getDiscountPercentage().divide(BigDecimal.valueOf(100)))
//                : BigDecimal.ZERO;
//
//        BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.valueOf(100)) >= 0
//                ? BigDecimal.ZERO
//                : BigDecimal.valueOf(10);
//
//        BigDecimal total = subtotal.subtract(discount).add(tax).add(deliveryFee);
//
//        return new CartDto(itemDtos, subtotal, tax, discount, deliveryFee, total);
//    }
