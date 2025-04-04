package com.chuwa.accountservice.service;


import com.chuwa.accountservice.payload.UserInfoDTO;
import com.chuwa.accountservice.payload.UserInfoRequestDTO;
import com.chuwa.accountservice.payload.UserPasswordRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface AccountService {

    UserInfoDTO updateUserInfo(UUID userId, UserInfoRequestDTO userInfoRequestDTO);

    UserInfoDTO updateUserPassword(UUID userId, UserPasswordRequestDTO userPasswordRequestDTO);

    UserInfoDTO getAccountById(UUID userId);
    Page<UserInfoDTO> getAllAccounts(Pageable pageable);
}
