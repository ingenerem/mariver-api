package com.mariver.funds;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FundRepository extends JpaRepository<Fund, Long> {

    List<Fund> findByAccountId(Long accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT f FROM Fund f
                WHERE f.id = :fundId AND f.account.id = :accountId
            """)
    Optional<Fund> findByIdAndAccountIdForUpdate(
            @Param("fundId") Long fundId,
            @Param("accountId") Long accountId);

    @Query("""
            SELECT COALESCE(SUM(f.balance), 0)
            FROM Fund f
            WHERE f.account.id = :accountId
            """)
    BigDecimal getTotalFundBalance(@Param("accountId") Long accountId);

    Optional<Fund> findByIdAndAccountId(Long id, Long accountId);
}