package com.mariver.account;

import com.mariver.user.User;
import com.mariver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountResponse getMyAccount(String email) {

        User user = getUserByEmail(email);

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return mapToResponse(account);
    }

    public AccountResponse updateMyAccount( AccountRequest request, String email) {

        User user = getUserByEmail(email);

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setCurrentBalance(request.currentBalance());

        Account savedAccount = accountRepository.save(account);

        return mapToResponse(savedAccount);
    }

    private User getUserByEmail(String email) {


        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private AccountResponse mapToResponse(Account account) {

        return new AccountResponse( account.getId(), account.getCurrentBalance(),
                account.getCreatedAt(), account.getUpdatedAt()
        );
    }
}