package com.mariver.transaction;

import com.mariver.transaction.dto.TransactionRequest;
import com.mariver.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            Authentication authentication,
            @RequestBody TransactionRequest request
    ) {
        return ResponseEntity.ok(
                transactionService.createTransaction(authentication.getName(), request)
        );
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            Authentication authentication,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(
                    transactionService.getPostedTransactionsByDateRange(
                            authentication.getName(),
                            startDate,
                            endDate
                    )
            );
        }

        return ResponseEntity.ok(
                transactionService.getPostedTransactions(authentication.getName())
        );
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            Authentication authentication,
            @PathVariable Long transactionId
    ) {
        transactionService.deleteTransaction(authentication.getName(), transactionId);
        return ResponseEntity.noContent().build();
    }
}