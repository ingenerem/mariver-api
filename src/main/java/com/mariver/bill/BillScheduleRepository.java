package com.mariver.bill;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillScheduleRepository extends JpaRepository<BillSchedule, Long> {

    Optional<BillSchedule> findByBill(Bill bill);

    List<BillSchedule> findByBillUserEmailAndBillActiveTrue(String email);

    List<BillSchedule> findByActiveTrueAndNextDueDateLessThanEqual(LocalDate endOfMonth);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT bs
    FROM BillSchedule bs
    WHERE bs.active = true
      AND bs.nextDueDate <= :endOfMonth
    ORDER BY bs.id
    """)
    List<BillSchedule> findSchedulesForSynchronization(
            @Param("endOfMonth") LocalDate endOfMonth
    );
}