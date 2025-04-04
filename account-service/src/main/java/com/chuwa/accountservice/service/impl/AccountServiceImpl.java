package com.chuwa.accountservice.service.impl;

import com.chuwa.accountservice.dao.UserRepository;
import com.chuwa.accountservice.exception.ResourceNotFoundException;
import com.chuwa.accountservice.model.User;
import com.chuwa.accountservice.payload.UserInfoDTO;
import com.chuwa.accountservice.payload.UserInfoRequestDTO;
import com.chuwa.accountservice.payload.UserPasswordRequestDTO;
import com.chuwa.accountservice.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserInfoDTO updateUserInfo(UUID userId, UserInfoRequestDTO userInfoRequestDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (StringUtils.hasText(userInfoRequestDTO.getEmail())) {
            existingUser.setEmail(userInfoRequestDTO.getEmail());
        }

        if (StringUtils.hasText(userInfoRequestDTO.getUsername())) {
            existingUser.setUsername(userInfoRequestDTO.getUsername());
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToUserInfoDTO(updatedUser);
    }

    @Override
    public UserInfoDTO updateUserPassword(UUID userId, UserPasswordRequestDTO userPasswordRequestDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (StringUtils.hasText(userPasswordRequestDTO.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userPasswordRequestDTO.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToUserInfoDTO(updatedUser);
    }

    @Override
    public UserInfoDTO getAccountById(UUID userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToUserInfoDTO(existingUser);
    }

    @Override
    public Page<UserInfoDTO> getAllAccounts(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToUserInfoDTO);
    }

    private UserInfoDTO convertToUserInfoDTO(User user) {
        return new UserInfoDTO(user.getEmail(), user.getUsername(), user.getRoles());
    }
}
