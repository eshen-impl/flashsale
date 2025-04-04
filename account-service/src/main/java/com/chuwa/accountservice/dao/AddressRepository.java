package com.chuwa.accountservice.dao;

import com.chuwa.accountservice.model.Address;
import com.chuwa.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAddressesByUser(User user);
    @Query("SELECT a FROM Address a WHERE a.addressId = :addressId AND a.user.id = :userId")
    Optional<Address> findByAddressIdAndUserId(@Param("addressId") Long addressId, @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Address a WHERE a.addressId = :addressId AND a.user.id = :userId")
    void deleteByAddressIdAndUserId(@Param("addressId") Long addressId, @Param("userId") UUID userId);

}
