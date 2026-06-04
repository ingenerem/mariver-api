package com.mariver.auth.controller;

import com.mariver.auth.dto.AuthResponse;
import com.mariver.auth.dto.LoginRequest;
import com.mariver.auth.dto.RegisterRequest;
import com.mariver.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /*
     * Business logic layer.
     */
    private final AuthService authService;

    /*
     * REGISTER ENDPOINT
     */
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    /*
     * LOGIN ENDPOINT
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}