package com.mariver.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /*
     * SELECT *
     * FROM transactions t
     * WHERE t.user.email = ?
     *   AND t.status = ?
     * ORDER BY t.transaction_date DESC,
     *          t.created_at DESC;
     *
     * Returns all transactions for a user with a given status.
     * Most commonly used for:
     * - All POSTED transactions
     * - All DELETED transactions
     */
    List<Transaction> findByUserEmailAndStatusOrderByTransactionDateDescCreatedAtDesc(
            String email,
            TransactionStatus status
    );

    /*
     * SELECT *
     * FROM transactions t
     * WHERE t.user.email = ?
     *   AND t.status = ?
     *   AND t.transaction_date BETWEEN ? AND ?
     * ORDER BY t.transaction_date DESC,
     *          t.created_at DESC;
     *
     * Returns transactions for a user within a date range.
     * Useful for:
     * - Current month spending
     * - Dashboard calculations
     * - Monthly reports
     */
    List<Transaction> findByUserEmailAndStatusAndTransactionDateBetweenOrderByTransactionDateDescCreatedAtDesc(
            String email,
            TransactionStatus status,
            LocalDate startDate,
            LocalDate endDate
    );
}