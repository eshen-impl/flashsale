package com.chuwa.accountservice.controller;

import com.chuwa.accountservice.payload.AddressDTO;
import com.chuwa.accountservice.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;

    }

    @PostMapping
    @Operation(summary = "Add new address for the current user.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<AddressDTO> addAddress(@Valid @RequestBody AddressDTO addressDTO, @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        AddressDTO createdAddress = addressService.addAddress(userId, addressDTO);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all addresses for the current user.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<List<AddressDTO>> getAddressesByUserId(@RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        List<AddressDTO> addresses = addressService.getAddressesByUserId(userId);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get a specific address for the current user.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<AddressDTO> getAddressByUserIdAndAddressId(@RequestParam("addressId") Long addressId, @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        AddressDTO address = addressService.getAddressByUserIdAndAddressId(userId, addressId);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Update current user's one specific address. Pass in addressId and new values in the request body.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO, @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        AddressDTO updatedAddress = addressService.updateAddress(userId, addressDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Delete current user's one specific address.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<Void> removeAddress(@RequestParam("addressId") Long addressId, @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        addressService.removeAddress(userId, addressId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
