package az.coders.fera_project.repository;

import az.coders.fera_project.entity.Address;
import az.coders.fera_project.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    Optional<Address> findByUserIdAndAddressType(Long userId, AddressType addressType);

    void deleteByIdAndUserId(Long addressId, Long userId);

    Optional<Address> findByIdAndUserId(Long addressId, Long userId);

    boolean existsByIdAndUserId(Long addressId, Long userId);
}
