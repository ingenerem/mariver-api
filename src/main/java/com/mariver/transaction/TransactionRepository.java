package com.mariver.transaction;

import com.mariver.transaction.dto.TransactionCategoryTotal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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


    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.user.email = :email
      AND t.type = :type
      AND t.transactionSource = :transactionSource
      AND t.status = :status
      AND t.transactionDate >= :startDate
      AND t.transactionDate < :endDate
    """)
    BigDecimal sumAmountByTypeAndSourceAndDateRange(
            @Param("email") String email,
            @Param("type") TransactionType type,
            @Param("transactionSource") TransactionSource transactionSource,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT
        t.category AS category,
        SUM(t.amount) AS totalAmount
    FROM Transaction t
    WHERE t.user.email = :email
      AND t.type = 'EXPENSE'
      AND t.transactionSource = :source
      AND t.status = 'POSTED'
      AND t.transactionDate >= :startDate
      AND t.transactionDate < :endDate
    GROUP BY t.category
    ORDER BY SUM(t.amount) DESC
    """)
    List<TransactionCategoryTotal> findTopSpendingCategory(
            String email,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable,
            TransactionSource source
    );
}