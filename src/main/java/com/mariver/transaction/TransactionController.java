package com.mariver.transaction;

import com.mariver.transaction.dto.TransactionRequest;
import com.mariver.transaction.dto.TransactionResponse;
import com.mariver.transaction.dto.TransactionSummaryResponse;
import com.mariver.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/batch")
    public ResponseEntity<String> createTransactions(
            Authentication authentication,
            @RequestBody List<TransactionRequest> requests) {

        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();

        try {
            transactionService.createTransactions(email, requests);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("transactions saved successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }

    }


    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getCurrentMonthTransactions(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
            return ResponseEntity.ok(
                    transactionService.getCurrentMonthPostedTransactions(email));
        }



    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(Authentication authentication, @PathVariable Long transactionId) {
        transactionService.deleteTransaction(authentication.getName(), transactionId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryResponse> getTransactionsSummary(Authentication authentication)
    {

        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        TransactionSummaryResponse summary =
                transactionService.calculateSummaryTransactions(email);
        return ResponseEntity.ok(summary);
    }
}