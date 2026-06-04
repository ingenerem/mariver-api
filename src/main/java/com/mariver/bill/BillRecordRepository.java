package com.mariver.bill;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

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

    /*
     * SELECT *
     * FROM bill_records br
     * JOIN bills b ON br.bill_id = b.id
     * JOIN users u ON b.user_id = u.id
     * WHERE u.email = ?
     *   AND b.id = ?
     * ORDER BY br.record_year DESC,
     *          br.record_month DESC,
     *          br.paid_at DESC;
     *
     * Returns payment history for one bill.
     */
    List<BillRecord> findByBillUserEmailAndBillIdOrderByRecordYearDescRecordMonthDescPaidAtDesc(
            String email,
            Long billId
    );
}