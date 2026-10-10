package com.mariver.transaction;

import com.mariver.transaction.dto.TransactionRequest;
import com.mariver.transaction.dto.TransactionResponse;
import com.mariver.transaction.dto.TransactionReversalRequest;
import com.mariver.transaction.dto.TransactionSummaryResponse;
import com.mariver.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionReversalService transactionReversalService;

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
    public ResponseEntity<List<TransactionResponse>> getMonthTransactions(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication authentication
    ) {
        YearMonth selectedMonth = year == null
                ? YearMonth.now()
                : YearMonth.of(year, month);

        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        return ResponseEntity.ok(
                transactionService.getCurrentMonthPostedTransactions(email, selectedMonth));
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

    @PostMapping("/{transactionId}/reverse")
    public ResponseEntity<Void> reverseTransaction(Authentication authentication,
                                                   @PathVariable Long transactionId,
                                                   @RequestBody TransactionReversalRequest request) {

        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();

        transactionReversalService.reverseTransaction(email, transactionId, request.reason());

        return ResponseEntity.noContent().build();
    }
}