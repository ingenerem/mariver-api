package com.mariver.transaction;

import com.mariver.account.Account;
import com.mariver.account.AccountRepository;
import com.mariver.transaction.dto.TransactionRequest;
import com.mariver.transaction.dto.TransactionResponse;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponse createTransaction(String email, TransactionRequest request) {
        User user = getUserByEmail(email);
        Account account = getUserAccount(user);

        validateTransactionRequest(request);

        Transaction transaction = Transaction.builder()
                .user(user)
                .account(account)
                .amount(request.amount())
                .type(request.type())
                .category(request.category())
                .description(request.description())
                .transactionDate(
                        request.transactionDate() != null
                                ? request.transactionDate()
                                : LocalDate.now()
                )
                .status(TransactionStatus.POSTED)
                .build();

        applyTransactionToAccount(account, transaction);

        Transaction savedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);

        return mapToResponse(savedTransaction);
    }

    public List<TransactionResponse> getPostedTransactions(String email) {
        return transactionRepository
                .findByUserEmailAndStatusOrderByTransactionDateDescCreatedAtDesc(
                        email,
                        TransactionStatus.POSTED
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TransactionResponse> getPostedTransactionsByDateRange(
            String email,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return transactionRepository
                .findByUserEmailAndStatusAndTransactionDateBetweenOrderByTransactionDateDescCreatedAtDesc(
                        email,
                        TransactionStatus.POSTED,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteTransaction(String email, Long transactionId) {
        User user = getUserByEmail(email);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Transaction does not belong to user");
        }

        if (transaction.getStatus() == TransactionStatus.DELETED) {
            throw new RuntimeException("Transaction is already deleted");
        }

        Account account = transaction.getAccount();

        reverseTransactionFromAccount(account, transaction);

        transaction.setStatus(TransactionStatus.DELETED);
        transaction.setDeletedAt(LocalDateTime.now());
        transaction.setDeleteReason("Deleted by user");

        transactionRepository.save(transaction);
        accountRepository.save(account);
    }

    private void applyTransactionToAccount(Account account, Transaction transaction) {
        if (transaction.getType() == TransactionType.INCOME) {
            account.setCurrentBalance(account.getCurrentBalance().add(transaction.getAmount()));
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount()));
        } else {
            throw new RuntimeException("Unsupported transaction type");
        }
    }

    private void reverseTransactionFromAccount(Account account, Transaction transaction) {
        if (transaction.getType() == TransactionType.INCOME) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount()));
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            account.setCurrentBalance(account.getCurrentBalance().add(transaction.getAmount()));
        } else {
            throw new RuntimeException("Unsupported transaction type");
        }
    }

    private void validateTransactionRequest(TransactionRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transaction amount must be greater than zero");
        }

        if (request.type() == null) {
            throw new RuntimeException("Transaction type is required");
        }

        if (request.category() == null) {
            throw new RuntimeException("Transaction category is required");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Account getUserAccount(User user) {
        return accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
}