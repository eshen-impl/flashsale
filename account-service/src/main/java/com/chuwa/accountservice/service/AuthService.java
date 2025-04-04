package com.chuwa.accountservice.service;

import com.chuwa.accountservice.payload.SignInRequestDTO;
import com.chuwa.accountservice.payload.SignUpRequestDTO;

import java.util.Map;
import java.util.UUID;

public interface AuthService {
    void signUp(SignUpRequestDTO signUpRequestDTO);

    Map<String, String> signIn(SignInRequestDTO signInRequestDTO);

    void signOut(UUID userId);
}
