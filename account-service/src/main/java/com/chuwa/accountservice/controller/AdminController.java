package com.chuwa.accountservice.controller;


import com.chuwa.accountservice.payload.UserInfoDTO;
import com.chuwa.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AccountService accountService;

    public AdminController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/all-users")
    @Operation(summary = "List all user accounts and their details",
            description = "Required to have role: 'ROLE_AMIN'")
    public ResponseEntity<Page<UserInfoDTO>> getAllAccounts(@RequestParam(defaultValue = "0", name = "page") int page,
                                                            @RequestParam(defaultValue = "10", name = "size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserInfoDTO> userInfoDTOs = accountService.getAllAccounts(pageable);
        return new ResponseEntity<>(userInfoDTOs, HttpStatus.OK);
    }

}
