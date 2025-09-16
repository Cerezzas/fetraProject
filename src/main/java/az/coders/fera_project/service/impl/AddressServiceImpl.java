package az.coders.fera_project.service.impl;

import az.coders.fera_project.config.EnhancedObjectMapper;
import az.coders.fera_project.dto.AddressDto;
import az.coders.fera_project.entity.Address;
import az.coders.fera_project.entity.Order;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.repository.AddressRepository;
import az.coders.fera_project.repository.OrderRepository;
import az.coders.fera_project.repository.register.UserRepository;
import az.coders.fera_project.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final EnhancedObjectMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> getAllAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return mapper.convertList(addresses, AddressDto.class);
    }

    @Override
    public AddressDto addNewAddress(Long userId, AddressDto addressDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Address newAddress = mapper.convertValue(addressDto, Address.class);
        newAddress.setUser(user);

        if (addressDto.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(address -> {
                        address.setDefault(false);
                        addressRepository.save(address);
                    });
        }

        Address saved = addressRepository.save(newAddress);
        return mapper.convertValue(saved, AddressDto.class);
    }

    @Override
    public AddressDto updateAddress(Long userId, Long addressId, AddressDto addressDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        if (addressDto.getName() != null) address.setName(addressDto.getName());
        if (addressDto.getPhoneNumber() != null) address.setPhoneNumber(addressDto.getPhoneNumber());
        if (addressDto.getStreet() != null) address.setStreet(addressDto.getStreet());
        if (addressDto.getCity() != null) address.setCity(addressDto.getCity());
        if (addressDto.getCountry() != null) address.setCountry(addressDto.getCountry());
        if (addressDto.getAddressType() != null) address.setAddressType(addressDto.getAddressType());

        if (addressDto.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(a -> {
                        a.setDefault(false);
                        addressRepository.save(a);
                    });
            address.setDefault(true);
        }

        Address updated = addressRepository.save(address);
        return mapper.convertValue(updated, AddressDto.class);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean exists = addressRepository.existsByIdAndUserId(addressId, userId);
        if (!exists) {
            throw new NotFoundException("Address not found");
        }

        addressRepository.deleteByIdAndUserId(addressId, userId);
    }

    @Override
    public void setDefaultAddress(Long userId, Long addressId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(a -> {
                    a.setDefault(false);
                    addressRepository.save(a);
                });

        address.setDefault(true);
        addressRepository.save(address);
    }

    /**
     * ✅ Назначить адрес конкретному заказу + установить как дефолтный
     */
    @Override
    public void assignAddressToOrder(Long userId, Long orderId, Long addressId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found or doesn't belong to user"));

        // Назначаем адрес заказу
        order.setShippingAddress(address);
        orderRepository.save(order);

        // Делаем адрес дефолтным для пользователя
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(existing -> {
                    existing.setDefault(false);
                    addressRepository.save(existing);
                });

        address.setDefault(true);
        addressRepository.save(address);
    }
}
