package az.coders.fera_project.service.impl.cart;

import az.coders.fera_project.config.EnhancedObjectMapper;
import az.coders.fera_project.dto.cart.WishlistItemDto;
import az.coders.fera_project.entity.Product;
import az.coders.fera_project.entity.cart.Wishlist;
import az.coders.fera_project.entity.cart.WishlistItem;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.exception.BadRequestException;
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

    // Получаем или создаём вишлист для юзера или гостя
    private Wishlist getOrCreateWishlist(Long userId, String sessionKey) {
        if (userId != null) {
            return wishlistRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NotFoundException("User not found"));
                        Wishlist wishlist = new Wishlist();
                        wishlist.setUser(user);
                        return wishlistRepository.save(wishlist);
                    });
        } else if (sessionKey != null && !sessionKey.isBlank()) {
            return wishlistRepository.findBySessionKey(sessionKey)
                    .orElseGet(() -> {
                        Wishlist wishlist = new Wishlist();
                        wishlist.setSessionKey(sessionKey);
                        return wishlistRepository.save(wishlist);
                    });
        } else {
            throw new BadRequestException("Either userId or sessionKey must be provided");
        }
    }

    @Override
    public List<WishlistItemDto> getWishlistItems(Long userId, String sessionKey) {
        Wishlist wishlist = getOrCreateWishlist(userId, sessionKey);
        return mapper.convertList(wishlist.getItems(), WishlistItemDto.class);
    }

    @Override
    public void addProductToWishlist(Long userId, String sessionKey, Integer productId) {
        Wishlist wishlist = getOrCreateWishlist(userId, sessionKey);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        boolean exists = wishlist.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (exists) {
            throw new BadRequestException("Product already in wishlist");
        }

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setProduct(product);
        wishlistItem.setWishlist(wishlist);
        wishlistItemRepository.save(wishlistItem);
    }

    @Override
    public void removeProductFromWishlist(Long userId, String sessionKey, Integer wishlistItemId) {
        Wishlist wishlist = getOrCreateWishlist(userId, sessionKey);

        WishlistItem item = wishlistItemRepository.findById(Long.valueOf(wishlistItemId))
                .orElseThrow(() -> new NotFoundException("Wishlist item not found"));

        if (!item.getWishlist().getId().equals(wishlist.getId())) {
            throw new NotFoundException("Wishlist item does not belong to this user/session");
        }

        wishlistItemRepository.delete(item);
    }
}
