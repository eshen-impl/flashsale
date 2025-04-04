package com.chuwa.accountservice.service.impl;

import com.chuwa.accountservice.dao.PaymentMethodRepository;
import com.chuwa.accountservice.dao.UserRepository;
import com.chuwa.accountservice.exception.ResourceNotFoundException;
import com.chuwa.accountservice.model.PaymentMethod;
import com.chuwa.accountservice.model.User;
import com.chuwa.accountservice.payload.PaymentMethodDTO;
import com.chuwa.accountservice.service.PaymentMethodService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository, UserRepository userRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
    }


    @Override
    public PaymentMethodDTO addPaymentMethod(UUID userId, PaymentMethodDTO paymentMethodDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PaymentMethod paymentMethod = new PaymentMethod();
        mapToPaymentMethod(paymentMethod, paymentMethodDTO);
        paymentMethod.setUser(existingUser);

        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return convertToPaymentMethodDTO(savedPaymentMethod);
    }

    @Override
    public List<PaymentMethodDTO> getPaymentMethodsByUserId(UUID userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<PaymentMethod> paymentMethodsByUser = paymentMethodRepository.findPaymentMethodsByUser(existingUser);
        return paymentMethodsByUser.stream().map(this::convertToPaymentMethodDTO).collect(Collectors.toList());

    }

    @Override
    public PaymentMethodDTO updatePaymentMethod(UUID userId, PaymentMethodDTO paymentMethodDTO) {

        PaymentMethod paymentMethod = paymentMethodRepository.findByPaymentMethodIdAndUserId(paymentMethodDTO.getPaymentMethodId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("User payment method not found"));

        mapToPaymentMethod(paymentMethod, paymentMethodDTO);
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return convertToPaymentMethodDTO(savedPaymentMethod);

    }

    @Override
    public void removePaymentMethod(UUID userId, Long paymentMethodId) {
        paymentMethodRepository.deleteByPaymentMethodIdAndUserId(paymentMethodId, userId);
    }

    @Override
    public PaymentMethodDTO getPaymentMethodByUserIdAndPaymentMethodId(UUID userId, Long paymentMethodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByPaymentMethodIdAndUserId(paymentMethodId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User payment method not found"));

        return convertToPaymentMethodDTO(paymentMethod);
    }


    private void mapToPaymentMethod(PaymentMethod paymentMethod, PaymentMethodDTO paymentMethodDTO) {
        paymentMethod.setType(paymentMethodDTO.getType());
        paymentMethod.setCardNumber(paymentMethodDTO.getCardNumber());
        paymentMethod.setNameOnCard(paymentMethodDTO.getNameOnCard());
        paymentMethod.setExpirationDate(paymentMethodDTO.getExpirationDate());
    }

    private PaymentMethodDTO convertToPaymentMethodDTO(PaymentMethod paymentMethod) {
        return new PaymentMethodDTO(
                paymentMethod.getPaymentMethodId(),
                paymentMethod.getType(),
                paymentMethod.getCardNumber(),
                paymentMethod.getNameOnCard(),
                paymentMethod.getExpirationDate()
        );
    }
}
