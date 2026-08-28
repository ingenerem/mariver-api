package com.mariver.account;

import com.mariver.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    public AccountResponse getMyAccount(Authentication authentication) {


        User user = (User) authentication.getPrincipal();
        assert user != null;
        String email = user.getEmail();
        return accountService.getMyAccount(email);
    }

    @PatchMapping("/me")
    public AccountResponse updateMyAccount(@RequestBody @Valid AccountRequest request, Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return accountService.updateMyAccount(request, user.getEmail());
    }
}