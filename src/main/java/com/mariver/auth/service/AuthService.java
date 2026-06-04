package com.mariver.auth.service;

import com.mariver.account.Account;
import com.mariver.account.AccountRepository;
import com.mariver.auth.dto.AuthResponse;
import com.mariver.auth.dto.LoginRequest;
import com.mariver.auth.dto.RegisterRequest;
import com.mariver.user.User;
import com.mariver.auth.enums.Role;
import com.mariver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    /*
     * Repository used to interact with database.
     */
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    /*
     * Handles password hashing and password verification.
     */
    private final PasswordEncoder passwordEncoder;

    /*
     * Handles JWT generation and validation.
     */
    private final JwtService jwtService;

    /*
     * USER REGISTRATION FLOW
     *
     * 1. Frontend sends register request.
     * 2. Check whether email already exists.
     * 3. Hash password.
     * 4. Create User entity.
     * 5. Save user to database.
     * 6. Generate JWT.
     * 7. Return token to frontend.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        Account account = Account.builder()
                .user(savedUser)
                .currentBalance(BigDecimal.ZERO)
                .build();

        accountRepository.save(account);
        String token = jwtService.generateToken(savedUser.getEmail());
        return new AuthResponse(token, user.getDisplayName(), user.getEmail());
    }
    /*
     * USER LOGIN FLOW
     *
     * 1. Frontend sends email/password.
     * 2. Find user by email.
     * 3. Compare raw password with hashed password.
     * 4. If valid -> generate JWT.
     * 5. Return token.
     */
    public AuthResponse login(LoginRequest request) {

        /*
         * Find user by email.
         */
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        /*
         * Compare raw password against hashed password.
         *
         * passwordEncoder.matches(raw, hashed)
         */
        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        /*
         * Generate new JWT token after successful login.
         */
        String token =
                jwtService.generateToken(user.getEmail());

        /*
         * Return auth response.
         */
        return new AuthResponse(
                token,
                user.getDisplayName(),
                user.getEmail()
        );
    }
}