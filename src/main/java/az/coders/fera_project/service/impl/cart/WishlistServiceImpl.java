package az.coders.fera_project.service.impl.cart;

import az.coders.fera_project.config.EnhancedObjectMapper;
import az.coders.fera_project.dto.cart.WishlistItemDto;
import az.coders.fera_project.entity.Product;
import az.coders.fera_project.entity.cart.Wishlist;
import az.coders.fera_project.entity.cart.WishlistItem;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.ProductRepository;
import az.coders.fera_project.repository.cart.WishlistItemRepository;
import az.coders.fera_project.repository.cart.WishlistRepository;
import az.coders.fera_project.repository.register.UserRepository;
import az.coders.fera_project.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EnhancedObjectMapper mapper;

    public Wishlist getOrCreateWishlist(Integer userId) {
        return wishlistRepository.findByUserId(Long.valueOf(userId))
                .orElseGet(() -> {
                    User user = userRepository.findById(Long.valueOf(userId))
                            .orElseThrow(() -> new NotFoundException("User not found"));
                    Wishlist wishlist = new Wishlist();
                    wishlist.setUser(user);
                    return wishlistRepository.save(wishlist);
                });
    }

    public List<WishlistItemDto> getWishlistItems(Integer userId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        return mapper.convertList(wishlist.getItems(), WishlistItemDto.class);
    }

    public void addProductToWishlist(Integer userId, Integer productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Проверка на дубликаты, чтобы не добавлять один и тот же товар несколько раз
        boolean exists = wishlist.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (exists) {
            throw new IllegalStateException("Product already in wishlist");
        }

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setProduct(product);
        wishlistItem.setWishlist(wishlist);

        wishlistItemRepository.save(wishlistItem);
    }

    public void removeProductFromWishlist(Integer userId, Integer wishlistItemId) {
        Wishlist wishlist = getOrCreateWishlist(userId);

        WishlistItem item = wishlistItemRepository.findById(Long.valueOf(wishlistItemId))
                .orElseThrow(() -> new NotFoundException("Wishlist item not found"));

        if (!item.getWishlist().equals(wishlist)) {
            throw new NotFoundException("Wishlist item does not belong to user");
        }

        wishlistItemRepository.delete(item);
    }

    // Можно добавить метод очистки или перемещения в корзину, если надо
}
