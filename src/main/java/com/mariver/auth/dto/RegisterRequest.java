package com.mariver.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String displayName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
