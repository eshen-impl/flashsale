package com.chuwa.accountservice.controller;

import com.chuwa.accountservice.payload.SignInRequestDTO;
import com.chuwa.accountservice.payload.SignUpRequestDTO;
import com.chuwa.accountservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Register a new user account.",
            description = "Pass 'ROLE_ADMIN' in 'roles' property of the request body for full authorization to all APIs.\n" +
                    "If left blank, will assign 'ROLE_CUSTOMER' by default.\n" +
            "Currently, available roles are ROLE_CUSTOMER, ROLE_ADMIN, ROLE_SELLER\n")

    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        authService.signUp(signUpRequestDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign in with email and password.",
            description = "Return jwt token, username, and roles. " +
            "Click Authorize button at the top right of this page. Copy token value and paste in. ")
    public ResponseEntity<Map<String, String>> signIn(@RequestBody SignInRequestDTO signInRequestDTO) {
        return ResponseEntity.ok(authService.signIn(signInRequestDTO));
    }

    @PostMapping("/sign-out")
    @Operation(summary = "Sign out",
                description = "Remove user session from Redis.")
    public ResponseEntity<String> signOut(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        authService.signOut(userId);
        return ResponseEntity.ok("User signed out successfully");
    }
}
