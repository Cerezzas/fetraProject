package az.coders.fera_project.service;

import az.coders.fera_project.dto.AddressDto;

import java.util.List;

public interface AddressService {

    // Получить все адреса пользователя
    List<AddressDto> getAllAddresses(Long userId);

    // Добавить новый адрес
    AddressDto addNewAddress(Long userId, AddressDto addressDto);

    // Редактировать адрес
    AddressDto updateAddress(Long userId, Long addressId, AddressDto addressDto);

    // Удалить адрес
    void deleteAddress(Long userId, Long addressId);

    void assignAddressToOrder(Long userId, Long orderId, Long addressId);

    // Установить дефолтный адрес
    void setDefaultAddress(Long userId, Long addressId);
}
