package com.chuwa.accountservice.controller;

import com.chuwa.accountservice.payload.PaymentMethodDTO;
import com.chuwa.accountservice.service.PaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @PostMapping
    @Operation(summary = "Add new payment method for the current user.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<PaymentMethodDTO> addPaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        PaymentMethodDTO createdPaymentMethod = paymentMethodService.addPaymentMethod(userId, paymentMethodDTO);
        return new ResponseEntity<>(createdPaymentMethod, HttpStatus.CREATED);
    }


    @GetMapping("/all")
    @Operation(summary = "Get all payment methods for the current user.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethodsByUserId(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        List<PaymentMethodDTO> paymentMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get a specific payment method for the current user.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<PaymentMethodDTO> getPaymentMethodByUserIdAndPaymentMethodId(@RequestParam("paymentMethodId") Long paymentMethodId, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        PaymentMethodDTO paymentMethods = paymentMethodService.getPaymentMethodByUserIdAndPaymentMethodId(userId, paymentMethodId);
        return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
    }


    @PutMapping
    @Operation(summary = "Update current user's one specific payment method. Pass in paymentMethodId and new values in the request body.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        PaymentMethodDTO updatedPaymentMethod = paymentMethodService.updatePaymentMethod(userId, paymentMethodDTO);
        return new ResponseEntity<>(updatedPaymentMethod, HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Delete current user's one specific address.",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<Void> removePaymentMethod(@RequestParam("paymentMethodId") Long paymentMethodId, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        paymentMethodService.removePaymentMethod(userId, paymentMethodId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
