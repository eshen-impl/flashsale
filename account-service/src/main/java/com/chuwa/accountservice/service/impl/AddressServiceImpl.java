package com.chuwa.accountservice.service.impl;

import com.chuwa.accountservice.dao.AddressRepository;
import com.chuwa.accountservice.dao.UserRepository;
import com.chuwa.accountservice.exception.ResourceNotFoundException;
import com.chuwa.accountservice.model.Address;
import com.chuwa.accountservice.model.User;
import com.chuwa.accountservice.payload.AddressDTO;
import com.chuwa.accountservice.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AddressDTO addAddress(UUID userId, AddressDTO addressDTO) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = new Address();
        mapToAddress(address, addressDTO);
        address.setUser(existingUser);

        Address savedAddress = addressRepository.save(address);
        return convertToAddressDTO(savedAddress);
    }

    @Override
    public List<AddressDTO> getAddressesByUserId(UUID userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Address> addressesByUser = addressRepository.findAddressesByUser(existingUser);
        return addressesByUser.stream().map(this::convertToAddressDTO).collect(Collectors.toList());
    }

    @Override
    public AddressDTO updateAddress(UUID userId, AddressDTO addressDTO) {

        Address address = addressRepository.findByAddressIdAndUserId(addressDTO.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("User address not found"));

        mapToAddress(address, addressDTO);

        Address savedAddress = addressRepository.save(address);
        return convertToAddressDTO(savedAddress);
    }

    @Override
    public void removeAddress(UUID userId, Long addressId) {
        addressRepository.deleteByAddressIdAndUserId(addressId, userId);
    }

    @Override
    public AddressDTO getAddressByUserIdAndAddressId(UUID userId, Long addressId) {

        Address address = addressRepository.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User address not found"));

        return convertToAddressDTO(address);
    }


    private void mapToAddress(Address address, AddressDTO addressDTO) {
        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setCountry(addressDTO.getCountry());
        address.setType(addressDTO.getType());
        address.setDefault(addressDTO.isDefault());
    }
    private AddressDTO convertToAddressDTO(Address address) {

        return new AddressDTO(
                address.getAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.getType(),
                address.isDefault()
        );

    }
}
