package az.coders.fera_project.service.impl.cart;

import az.coders.fera_project.dto.cart.CartItemDto;
import az.coders.fera_project.entity.Product;
import az.coders.fera_project.entity.cart.Cart;
import az.coders.fera_project.entity.cart.CartItem;
import az.coders.fera_project.entity.cart.Wishlist;
import az.coders.fera_project.entity.cart.WishlistItem;
import az.coders.fera_project.exception.BadRequestException;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.ProductRepository;
import az.coders.fera_project.repository.cart.CartItemRepository;
import az.coders.fera_project.repository.cart.CartRepository;
import az.coders.fera_project.repository.cart.WishlistItemRepository;
import az.coders.fera_project.repository.cart.WishlistRepository;
import az.coders.fera_project.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
//    private final EnhancedObjectMapper mapper;

    @Override
    public void addProductToCart(Integer userId, Integer productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(Long.valueOf(userId))
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int currentQuantity = cartItem != null ? cartItem.getQuantity() : 0;
        int totalRequested = currentQuantity + quantity;

        if (totalRequested > stock) {
            throw new BadRequestException("Only " + stock + " items in stock for product: " + product.getName());
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
    public void removeProductFromCart(Integer cartItemId) {
        CartItem item = cartItemRepository.findById(Long.valueOf(cartItemId))
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        cartItemRepository.delete(item);
    }

    @Override
    public CartItemDto getCartItemById(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        return mapToDto(item);  // вызываем метод маппинга
    }

    // приватный метод маппинга — сюда, внутри сервиса
    private CartItemDto mapToDto(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setProductName(item.getProduct().getName());
        dto.setImage(item.getProduct().getImage());


        BigDecimal price = BigDecimal.valueOf(item.getProduct().getPrice());
        dto.setProductPrice(BigDecimal.valueOf(price.doubleValue()));


        int quantity = item.getQuantity();
        dto.setQuantity(quantity);

        dto.setTotalPrice(price.multiply(BigDecimal.valueOf(quantity)));

        return dto;
    }



    @Override
    public void removeItem(Integer cartItemId) {
        removeProductFromCart(cartItemId);
    }

    @Override
    public void removeItems(List<Long> cartItemIds) {
        List<CartItem> items = cartItemRepository.findAllById(cartItemIds);
        cartItemRepository.deleteAll(items);
    }

    @Override
    public void updateQuantity(Integer cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(Long.valueOf(cartItemId))
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        Product product = item.getProduct();
        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        if (quantity > stock) {
            throw new BadRequestException("Only " + stock + " items in stock for product: " + product.getName());
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }


    @Override
    public void moveToWishlist(Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(Long.valueOf(cartItemId))
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        Long userId = Long.valueOf(cartItem.getCart().getUser().getId());

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist w = new Wishlist();
                    w.setUser(cartItem.getCart().getUser());
                    return wishlistRepository.save(w);
                });

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setProduct(cartItem.getProduct());
        wishlistItem.setWishlist(wishlist);

        wishlistItemRepository.save(wishlistItem);

        cartItemRepository.delete(cartItem);
    }
}
