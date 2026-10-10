package com.mariver.bill;

import com.mariver.account.Account;
import com.mariver.account.AccountService;
import com.mariver.bill.dto.BillRecordResponse;
import com.mariver.transaction.*;
import com.mariver.transaction.dto.TransactionRequest;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillRecordService {

    private final BillRepository billRepository;
    private final BillScheduleRepository billScheduleRepository;
    private final BillRecordRepository billRecordRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;


    @Transactional
    public BillRecordResponse markBillPaid(String email, Long billRecordID, LocalDate paidDate, BigDecimal actualAmount, boolean autoPaid) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BillRecord billRecord = billRecordRepository.findById(billRecordID)
                .orElseThrow(() -> new RuntimeException("Bill record not found"));

        Bill bill = billRecord.getBillSchedule().getBill();
        Account account = accountService.getAccountByEmail(email);


        TransactionRequest transactionRequest = new TransactionRequest(
                bill.getName(),
                actualAmount,
                TransactionType.OUTFLOW,
                "Scheduled bill",
                TransactionSource.BILL,
                bill.getCategory().name(),
                paidDate
        );
        Transaction savedTransaction = transactionService.createTransaction(email, transactionRequest, autoPaid);

        billRecord.setPaidDate(paidDate);
        billRecord.setActualAmount(actualAmount);
        billRecord.setPaid(true);
        billRecord.setTransaction(savedTransaction);

        BillRecord savedRecord = billRecordRepository.save(billRecord);
        return mapToResponse(savedRecord);
    }

    public BigDecimal getTotalUnpaidBills(String email){

        return billRecordRepository.getTotalUnpaidBills(email);
    }

    public List<BillRecordResponse> getUpcomingBills(String email) {

        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        return billRecordRepository
                .findUpcomingBills( email, today, endOfMonth)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public List<BillRecordResponse> getOverdueBills(String email) {

        LocalDate today = LocalDate.now();

        return billRecordRepository
                .findOverdueBills( email, today)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<BillRecordResponse> getPaidBills(String email) {

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        return billRecordRepository
                .findByBillScheduleBillUserEmailAndPaidTrueAndDueDateBetween(email, startOfMonth, endOfMonth )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public void synchronizeBillRecords(String email) {

        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<BillSchedule> schedules = billScheduleRepository.findByActiveTrueAndNextDueDateLessThanEqual(endOfMonth);
        for (BillSchedule schedule : schedules) {

            do {
                createBillRecord(schedule);
                updateNextDueDate(schedule);
            } while (!schedule.getNextDueDate().isAfter(endOfMonth));

            billScheduleRepository.save(schedule);
        }
    }

    private void updateNextDueDate(BillSchedule schedule) {

        LocalDate nextDueDate = switch (schedule.getIntervalUnit()) {

            case WEEK -> schedule.getNextDueDate().plusWeeks(schedule.getIntervalValue());
            case MONTH -> schedule.getNextDueDate().plusMonths(schedule.getIntervalValue());
            case YEAR -> schedule.getNextDueDate().plusYears(schedule.getIntervalValue());
        };
        schedule.setNextDueDate(nextDueDate);
    }

    public void createBillRecord(BillSchedule billSchedule) {

        BillRecord billRecord = BillRecord.builder()
                .billSchedule(billSchedule)
                .dueDate(billSchedule.getNextDueDate())
                .estimatedAmount(billSchedule.getBill().getEstimatedAmount())
                .paid(false)
                .build();

        billRecordRepository.save(billRecord);
    }

    private BillRecordResponse mapToResponse(BillRecord billRecord) {

        Bill bill = billRecord.getBillSchedule().getBill();

        return new BillRecordResponse(
                billRecord.getId(),
                bill.getName(),
                bill.getCategory(),
                billRecord.getEstimatedAmount(),
                billRecord.getActualAmount(),
                billRecord.getDueDate(),
                billRecord.isPaid(),
                billRecord.getPaidDate(),
                bill.getPaymentType(),
                billRecord.getBillSchedule().isAutoPay()
        );
    }


    @Transactional
    public void reversePayment(Long transactionId) {


        BillRecord billRecord = billRecordRepository.findByTransaction_Id(transactionId)
                .orElseThrow(() -> new RuntimeException("Bill record not found"));

        billRecord.setPaid(false);
        billRecord.setActualAmount(null);
        billRecord.setPaidDate(null);
        billRecord.setTransaction(null);

        billRecordRepository.save(billRecord);

    }


    @Transactional
    public void processAutoPayBills(String email) {

        LocalDate today = LocalDate.now();

        List<BillRecord> billsToPay = billRecordRepository.findBillsReadyForAutoPay(email, today, PaymentType.FIXED);

        for (BillRecord billRecord : billsToPay) {
            markBillPaid(email, billRecord.getId(), today, billRecord.getEstimatedAmount(), true);
        }
    }
}