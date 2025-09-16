package az.coders.fera_project.service.impl.security;

import az.coders.fera_project.config.EnhancedObjectMapper;
import az.coders.fera_project.dto.register.UserDto;
import az.coders.fera_project.entity.Address;
import az.coders.fera_project.entity.cart.Cart;
import az.coders.fera_project.entity.cart.Wishlist;
import az.coders.fera_project.entity.register.Authority;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.models.UserRequest;
import az.coders.fera_project.repository.AddressRepository;
import az.coders.fera_project.repository.cart.CartRepository;
import az.coders.fera_project.repository.cart.WishlistRepository;
import az.coders.fera_project.repository.register.AuthorityRepository;
import az.coders.fera_project.repository.register.UserRepository;
import az.coders.fera_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;



import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final AddressRepository addressRepository;
    private final AuthorityRepository authorityRepository;
    private final EnhancedObjectMapper mapper;
    private final PasswordEncoder passwordEncoder;
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
//    }



    @Override
    public List<UserDto> getUsers() {
        return Arrays.asList(mapper.convertValue(userRepository.findAll(), UserDto[].class));
    }

    @Override
    public UserDto getUserById(Integer id) {
        return mapper.convertValue(findById(id), UserDto.class);
    }


    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapper.convertValue(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = userRepository.save(user);

        // Сразу создаём пустую корзину
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cart.setSubtotal(BigDecimal.ZERO);
        cart.setTax(BigDecimal.ZERO);
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);

        // Создаём пустой Wishlist
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(savedUser);
        wishlistRepository.save(wishlist);

        return mapper.convertValue(savedUser, UserDto.class);
    }

//    @Override
//    public UserDto createUser(UserDto userDto) {
//        User user = mapper.convertValue(userDto, User.class);
//        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        return mapper.convertValue(userRepository.save(user), UserDto.class);
//    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = findById(id);

        // 1. Удалить связанные адреса
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            addressRepository.deleteAll(user.getAddresses());
        }


        // 2. Удалить корзину (если есть)
        if (user.getCart() != null) {
            cartRepository.delete(user.getCart());
        }

        user.setSelectedAddress(null); // удалить или закомментировать

        // 3. Удалить вишлист (если есть)
        if (user.getWishlist() != null) {
            wishlistRepository.delete(user.getWishlist());
        }

        // 4. Удалить пользователя
        userRepository.delete(user);
    }


    @Override // user
    public UserDto updateUser(Integer id, UserDto userDto) {
        User findUser = findById(id);

        if (userDto.getUsername() != null) {
            findUser.setUsername(userDto.getUsername());
        }

        if (userDto.getPassword() != null) {
            findUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (userDto.getAuthorities() != null && !userDto.getAuthorities().isEmpty()) {
            List<Authority> authorities = userDto.getAuthorities().stream()
                    .map(dto -> authorityRepository.findById(dto.getId())
                            .orElseThrow(() -> new RuntimeException("Authority not found with id: " + dto.getId())))
                    .collect(Collectors.toList());

            findUser.setAuthorities(authorities);
        }

        User updated = userRepository.save(findUser);
        return mapper.convertValue(updated, UserDto.class);
    }




    @Override //admin
    public UserRequest updateUser(Integer id, UserRequest userRequest) {
        User findUser = findById(id);

        // Обновляем только переданные поля
        if (userRequest.getUsername() != null) {
            findUser.setUsername(userRequest.getUsername());
        }

        if (userRequest.getPassword() != null) {
            findUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        if (userRequest.getEmail() != null) {
            findUser.setEmail(userRequest.getEmail()); //  обновляем email
        }

        return mapper.convertValue(userRepository.save(findUser), UserRequest.class);
    }

    private User findById(Integer id) {
        return userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("Cant find user by id "));
//                new NotFoundException(ErrorCode.NOT_FOUND));


    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public void selectAddress(Long userId, Integer addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Address address = addressRepository.findById(Long.valueOf(addressId))
                .orElseThrow(() -> new NotFoundException("Address not found"));

        //  Логирование для отладки
        System.out.println("userId param: " + userId);
        System.out.println("address.getUser().getId(): " + address.getUser().getId());

        if (!Objects.equals(userId, address.getUser().getId().longValue())) {
            throw new AccessDeniedException("Address doesn't belong to the user");
        }

        user.setSelectedAddress(address);
        userRepository.save(user);
    }

}
