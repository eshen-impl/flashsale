package com.chuwa.accountservice.payload;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequestDTO {
    @NotBlank(message = "User email is required")
    private String email;

    @NotBlank(message = "User password is required")
    private String password;
}
