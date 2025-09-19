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

    // ===== Вспомогательные методы =====

    private Cart getOrCreateCart(Long userId, String sessionKey) {
        if (userId != null) {
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
        } else if (sessionKey != null) {
            return cartRepository.findBySessionKey(sessionKey)
                    .orElseGet(() -> {
                        Cart cart = new Cart();
                        cart.setSessionKey(sessionKey);
                        cart.setSubtotal(BigDecimal.ZERO);
                        cart.setTax(BigDecimal.ZERO);
                        cart.setTotal(BigDecimal.ZERO);
                        return cartRepository.save(cart);
                    });
        } else {
            throw new BadRequestException("Either userId or sessionKey must be provided");
        }
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ===== Методы сервиса =====

    @Override
    public CartDto getCart(Long userId, String sessionKey) {
        Cart cart = getOrCreateCart(userId, sessionKey);

        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.15));
        BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.valueOf(100)) >= 0
                ? BigDecimal.ZERO : BigDecimal.valueOf(10);

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (cart.getDiscount() != null) {
            discountAmount = subtotal.multiply(cart.getDiscount().getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100));
        }

        BigDecimal grandTotal = subtotal.add(tax).add(deliveryFee).subtract(discountAmount);

        List<CartItemDto> products = Optional.ofNullable(cart.getCartItems())
                .orElse(Collections.emptyList())
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
        response.setDiscount(discountAmount);
        response.setGrandTotal(grandTotal);

        return response;
    }

    @Override
    public CartDto applyDiscount(Long userId, String sessionKey, String discountCode) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        Discount discount = discountRepository.findByCode(discountCode)
                .orElseThrow(() -> new NotFoundException("Discount code not found"));
        if (!discount.isActive()) {
            throw new IllegalStateException("Discount is not active");
        }
        cart.setDiscount(discount);
        cartRepository.save(cart);
        return getCart(userId, sessionKey);
    }

    @Override
    public BigDecimal getGrandTotal(Long userId, String sessionKey) {
        CartDto cartDto = getCart(userId, sessionKey);
        return cartDto.getGrandTotal();
    }
    @Transactional
    @Override
    public void checkout(Long userId, String sessionKey) {
        if (userId == null) {
            throw new BadRequestException("You must be logged in to checkout"); //  текст для гостей
        }

        Cart cart = getOrCreateCart(userId, sessionKey);

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Проверка наличия товаров
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
            if (item.getQuantity() > stock) {
                throw new BadRequestException("Not enough stock for product: " + product.getName() +
                        ". Only " + stock + " left.");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        TotalsDto totals = getCartTotals(userId, sessionKey);
        order.setTotalAmount(totals.getGrandTotal());

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(BigDecimal.valueOf(cartItem.getProduct().getPrice()));

                    Product product = cartItem.getProduct();
                    int updatedStock = product.getStockQuantity() - cartItem.getQuantity();
                    product.setStockQuantity(Math.max(0, updatedStock));
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        cart.getCartItems().clear();
        cart.setDiscount(null);
        cartRepository.save(cart);
    }


    public TotalsDto getCartTotals(Long userId, String sessionKey) {
        Cart cart = getOrCreateCart(userId, sessionKey);
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
    public void addProductToCart(Long userId, String sessionKey, Integer productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId, sessionKey);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        int currentQuantityInCart = cartItem != null ? cartItem.getQuantity() : 0;
        int totalRequested = currentQuantityInCart + quantity;
        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

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

    @Override
    public void removeProductFromCart(Long userId, String sessionKey, Integer cartItemId) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void moveToWishlist(Long userId, String sessionKey, Integer cartItemId) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        Wishlist wishlist = (userId != null)
                ? wishlistRepository.findByUserId(userId).orElseGet(() -> {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setUser(cart.getUser());
            return wishlistRepository.save(newWishlist);
        })
                : wishlistRepository.findBySessionKey(sessionKey).orElseGet(() -> {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setSessionKey(sessionKey);
            return wishlistRepository.save(newWishlist);
        });

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setProduct(cartItem.getProduct());
        wishlistItem.setWishlist(wishlist);
        wishlistItemRepository.save(wishlistItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void moveMultipleToWishlist(Long userId, String sessionKey, List<Long> cartItemIds) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);

        cartItems.forEach(cartItem -> {
            Wishlist wishlist = (userId != null)
                    ? wishlistRepository.findByUserId(userId).orElseGet(() -> {
                Wishlist newWishlist = new Wishlist();
                newWishlist.setUser(cart.getUser());
                return wishlistRepository.save(newWishlist);
            })
                    : wishlistRepository.findBySessionKey(sessionKey).orElseGet(() -> {
                Wishlist newWishlist = new Wishlist();
                newWishlist.setSessionKey(sessionKey);
                return wishlistRepository.save(newWishlist);
            });

            WishlistItem wishlistItem = new WishlistItem();
            wishlistItem.setProduct(cartItem.getProduct());
            wishlistItem.setWishlist(wishlist);
            wishlistItemRepository.save(wishlistItem);
            cartItemRepository.delete(cartItem);
        });
    }

    @Override
    public void removeMultipleFromCart(Long userId, String sessionKey, List<Long> cartItemIds) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);
        cartItems.forEach(cartItemRepository::delete);
    }

    @Transactional
    public void mergeCart(Long userId, String sessionKey) {
        if (sessionKey == null) return;

        // 1. Берём гостевую корзину
        Cart guestCart = cartRepository.findBySessionKey(sessionKey).orElse(null);
        if (guestCart == null) return;

        // 2. Ищем корзину пользователя
        Cart userCart = cartRepository.findByUserId(userId).orElse(null);

        // 3. Если корзины нет → создаём новую
        if (userCart == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            userCart = new Cart();
            userCart.setUser(user);
            userCart = cartRepository.save(userCart);
        }

        // 4. Мержим товары
        for (CartItem guestItem : guestCart.getCartItems()) {
            Optional<CartItem> existing = userCart.getCartItems().stream()
                    .filter(i -> i.getProduct().getId().equals(guestItem.getProduct().getId()))
                    .findFirst();

            if (existing.isPresent()) {
                existing.get().setQuantity(existing.get().getQuantity() + guestItem.getQuantity());
            } else {
                guestItem.setCart(userCart); // переназначаем корзину
                userCart.getCartItems().add(guestItem);
            }
        }

        // 5. Сохраняем результат
        cartRepository.save(userCart);

        // 6. Удаляем гостевую корзину
        cartRepository.delete(guestCart);
    }



    @Override
    public QuickCartDto getQuickCart(Long userId, String sessionKey) {
        Cart cart = getOrCreateCart(userId, sessionKey);
        List<CartItemDto> items = Optional.ofNullable(cart.getCartItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> {
                    Product product = item.getProduct();
                    BigDecimal productPrice = BigDecimal.valueOf(product.getPrice());
                    return new CartItemDto(
                            item.getId(),
                            product.getName(),
                            product.getImage(),
                            productPrice,
                            item.getQuantity(),
                            productPrice.multiply(BigDecimal.valueOf(item.getQuantity()))
                    );
                })
                .collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(CartItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new QuickCartDto(subtotal, items);
    }
}

