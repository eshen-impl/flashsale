package com.chuwa.accountservice.controller;


import com.chuwa.accountservice.payload.UserInfoDTO;
import com.chuwa.accountservice.payload.UserInfoRequestDTO;
import com.chuwa.accountservice.payload.UserPasswordRequestDTO;
import com.chuwa.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/user/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PutMapping("/info")
    @Operation(summary = "Update username and user email",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<UserInfoDTO> updateUserInfo(@Valid @RequestBody UserInfoRequestDTO userInfoRequestDTO, @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        UserInfoDTO updatedUserInfo = accountService.updateUserInfo(userId, userInfoRequestDTO);
        return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
    }


    @PutMapping("/password")
    @Operation(summary = "Update user account password",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<UserInfoDTO> updateUserPassword(@RequestBody UserPasswordRequestDTO userPasswordRequestDTO, @RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        UserInfoDTO updatedUserInfo = accountService.updateUserPassword(userId, userPasswordRequestDTO);
        return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
        //frontend receive 200 code and prompt user to sign in with new password
        //redirect /api/v1/auth/sign-out (having a button to sign in on the sign-out success page)
    }

    @GetMapping
    @Operation(summary = "Get user account info",
            description = "Required to be authenticated (have signed in).")
    public ResponseEntity<UserInfoDTO> getAccountById(@RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        UserInfoDTO userInfoDTO = accountService.getAccountById(userId);
        return new ResponseEntity<>(userInfoDTO, HttpStatus.OK);
    }



}
