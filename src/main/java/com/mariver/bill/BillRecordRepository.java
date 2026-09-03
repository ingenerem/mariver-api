package com.mariver.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface BillRecordRepository extends JpaRepository<BillRecord, Long> {

    /*
     * SELECT *
     * FROM bill_records br
     * JOIN bills b ON br.bill_id = b.id
     * JOIN users u ON b.user_id = u.id
     * WHERE u.email = ?
     *   AND br.record_month = ?
     *   AND br.record_year = ?;
     *
     * Returns all paid bill records for a user in a specific month/year.
     */
    List<BillRecord> findByBillUserEmailAndRecordMonthAndRecordYear(
            String email,
            Integer recordMonth,
            Integer recordYear
    );

    /*
     * SELECT *
     * FROM bill_records br
     * WHERE br.bill_schedule_id = ?
     *   AND br.record_month = ?
     *   AND br.record_year = ?;
     *
     * Used to check whether a specific scheduled bill occurrence
     * has already been paid.
     */
    Optional<BillRecord> findByBillScheduleIdAndRecordMonthAndRecordYear(
            Long billScheduleId,
            Integer recordMonth,
            Integer recordYear
    );

    Optional<BillRecord> findByIdAndBillUserEmail(Long billRecordId, String email);

    @Query("""
    SELECT br
    FROM BillRecord br
    WHERE br.bill.user.email = :email
      AND br.paid = false
      AND br.recordYear = :recordYear
      AND br.recordMonth = :recordMonth
      AND br.billSchedule.dueDay >= :currentDay
    ORDER BY br.billSchedule.dueDay ASC""")
    List<BillRecord> findUpcomingBills(
            String email,
            Integer recordYear,
            Integer recordMonth,
            Integer currentDay
    );

    @Query("""
    SELECT br
    FROM BillRecord br
    WHERE br.bill.user.email = :email
      AND br.paid = false
      AND (br.recordYear < :recordYear
          OR br.recordYear = :recordYear AND br.recordMonth < :recordMonth
          OR br.recordYear = :recordYear AND br.recordMonth = :recordMonth AND br.billSchedule.dueDay < :currentDay)
    ORDER BY br.billSchedule.dueDay ASC""")
    List<BillRecord> findOverdueBills(
            String email,
            Integer recordYear,
            Integer recordMonth,
            Integer currentDay
    );


    List<BillRecord> findByBillUserEmailAndRecordYearAndRecordMonth(String email, int year, int month);

    List<BillRecord> findByBillUserEmailAndRecordYearAndRecordMonthAndPaidTrueOrderByPaidAtDesc(String email, int currentYear, Integer currentMonth, Integer currentDay);

    @Query("""
    SELECT COALESCE(SUM(br.actualAmount), 0)
    FROM BillRecord br
    WHERE br.bill.user.email = :email
    AND br.paid = false
    """)
    BigDecimal getTotalUnpaidBills(String email);




}