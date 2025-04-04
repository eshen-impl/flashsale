package com.chuwa.accountservice.service;

import com.chuwa.accountservice.payload.AddressDTO;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressDTO addAddress(UUID userId, AddressDTO addressDTO);
    List<AddressDTO> getAddressesByUserId(UUID userId);
    AddressDTO updateAddress(UUID userId, AddressDTO addressDTO);
    void removeAddress(UUID userId, Long addressId);


    AddressDTO getAddressByUserIdAndAddressId(UUID userId, Long addressId);
}
