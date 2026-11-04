package com.mariver.account;

import com.mariver.transaction.Transaction;
import com.mariver.transaction.TransactionService;
import com.mariver.transaction.TransactionSource;
import com.mariver.transaction.TransactionType;
import com.mariver.transaction.dto.TransactionRequest;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

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

        TransactionRequest transactionRequest = new TransactionRequest(
                "Balance",
                request.currentBalance(),
                TransactionType.ADJUSTMENT,
                "Adjusting Balance",
                TransactionSource.INCOME,
                "Balance",
                LocalDate.now(), null);
        Transaction savedTransaction = transactionService.createTransaction(user.getEmail(),transactionRequest, false);

        return mapToResponse(savedTransaction.getAccount());
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

    public Account getAccountByEmail(String email) {
        return accountRepository.findByUserEmail(email).
                orElseThrow(()-> new RuntimeException("User account not found"));
    }
}