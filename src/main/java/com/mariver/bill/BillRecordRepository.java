package com.mariver.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface BillRecordRepository extends JpaRepository<BillRecord, Long> {


    @Query("""
    SELECT br
    FROM BillRecord br
    WHERE br.billSchedule.bill.user.email = :email
    AND   br.billSchedule.bill.active = true
    AND br.paid = false
    AND br.dueDate >= :today
    AND br.dueDate <= :endOfMonth
    ORDER BY br.dueDate ASC""")
    List<BillRecord> findUpcomingBills(
            String email,
            LocalDate today,
            LocalDate endOfMonth
    );

    @Query("""
    SELECT br
    FROM BillRecord br
    WHERE br.billSchedule.bill.user.email = :email
    AND   br.billSchedule.bill.active = true
      AND br.paid = false
      AND br.dueDate < :today
    ORDER BY br.dueDate ASC""")
    List<BillRecord> findOverdueBills(
            String email,
            LocalDate today
    );



    List<BillRecord> findByBillScheduleBillUserEmailAndPaidTrueAndDueDateBetween(String email, LocalDate startDate, LocalDate endDate);

    @Query("""
    SELECT COALESCE(SUM(br.estimatedAmount), 0)
    FROM BillRecord br
    WHERE br.billSchedule.bill.user.email = :email
    AND br.paid = false
    """)
    BigDecimal getTotalUnpaidBills(String email);


   Optional<BillRecord> findByTransaction_Id(Long transactionId);


    @Query("""
    SELECT br
    FROM BillRecord br
    WHERE br.billSchedule.bill.user.email = :email
      AND br.paid = false
      AND br.dueDate <= :today
      AND br.billSchedule.autoPay = true
      AND br.billSchedule.bill.paymentType = :paymentType
""")
    List<BillRecord> findBillsReadyForAutoPay(
            @Param("email") String email,
            @Param("today") LocalDate today,
            @Param("paymentType") PaymentType paymentType
    );

    boolean existsByBillScheduleIdAndDueDate(Long id, LocalDate dueDate);

    Optional<BillRecord> findByBillScheduleIdAndDueDate(
        Long billScheduleId,
        LocalDate dueDate
);
}