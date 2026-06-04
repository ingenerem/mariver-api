package com.mariver.bill;

import com.mariver.bill.dto.BillRecordRequest;
import com.mariver.bill.dto.BillRecordResponse;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillRecordService {

    private final BillRepository billRepository;
    private final BillScheduleRepository billScheduleRepository;
    private final BillRecordRepository billRecordRepository;
    private final UserRepository userRepository;

    @Transactional
    public BillRecordResponse markBillPaid(String email, BillRecordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bill bill = billRepository.findById(request.billId())
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (!bill.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bill does not belong to user");
        }

        BillSchedule schedule = billScheduleRepository.findById(request.billScheduleId())
                .orElseThrow(() -> new RuntimeException("Bill schedule not found"));

        if (!schedule.getBill().getId().equals(bill.getId())) {
            throw new RuntimeException("Bill schedule does not belong to this bill");
        }

        billRecordRepository
                .findByBillScheduleIdAndRecordMonthAndRecordYear(
                        schedule.getId(),
                        request.recordMonth(),
                        request.recordYear()
                )
                .ifPresent(existingRecord -> {
                    throw new RuntimeException("Bill already marked as paid for this period");
                });

        BillRecord billRecord = BillRecord.builder()
                .bill(bill)
                .billSchedule(schedule)
                .recordMonth(request.recordMonth())
                .recordYear(request.recordYear())
                .actualAmount(request.actualAmount())
                .paidAt(request.paidAt())
                .build();

        BillRecord savedRecord = billRecordRepository.save(billRecord);

        return mapToResponse(savedRecord);
    }

    private BillRecordResponse mapToResponse(BillRecord billRecord) {
        return new BillRecordResponse(
                billRecord.getId(),
                billRecord.getBill().getId(),
                billRecord.getBillSchedule().getId(),
                billRecord.getBill().getName(),
                billRecord.getRecordMonth(),
                billRecord.getRecordYear(),
                billRecord.getActualAmount(),
                billRecord.getPaidAt(),
                billRecord.getCreatedAt()
        );
    }
}