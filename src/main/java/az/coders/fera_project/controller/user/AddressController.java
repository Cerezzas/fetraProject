package az.coders.fera_project.controller.user;


import az.coders.fera_project.dto.AddressDto;
import az.coders.fera_project.service.AddressService;
import az.coders.fera_project.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/me")
    public ResponseEntity<List<AddressDto>> getAllAddresses() {
        Long userId = AuthUtils.extractUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<AddressDto> addresses = addressService.getAllAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/me/add")
    public ResponseEntity<AddressDto> addNewAddress(@RequestBody AddressDto addressDto) {
        Long userId = AuthUtils.extractUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AddressDto newAddress = addressService.addNewAddress(userId, addressDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAddress);
    }

    @PutMapping("/me/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long addressId, @RequestBody AddressDto addressDto) {
        Long userId = AuthUtils.extractUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AddressDto updatedAddress = addressService.updateAddress(userId, addressId, addressDto);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/me/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        Long userId = AuthUtils.extractUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/{addressId}/set-default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Long addressId) {
        Long userId = AuthUtils.extractUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ Привязка адреса к заказу и установка его дефолтным
     */
    @PutMapping("/assign-to-order/{orderId}/address/{addressId}")
    public ResponseEntity<String> assignAddressToOrder(
            @PathVariable Long orderId,
            @PathVariable Long addressId
    ) {
        Long userId = AuthUtils.extractUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        addressService.assignAddressToOrder(userId, orderId, addressId);
        return ResponseEntity.ok("Address assigned to order and set as default.");
    }
}
