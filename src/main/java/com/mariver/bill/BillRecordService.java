package com.mariver.bill;

import com.mariver.account.Account;
import com.mariver.account.AccountService;
import com.mariver.bill.dto.BillOccurrenceKey;
import com.mariver.bill.dto.BillRecordRequest;
import com.mariver.bill.dto.BillRecordResponse;
import com.mariver.common.utils.TimeSnapshot;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillRecordService {

    private final BillRepository billRepository;
    private final BillScheduleRepository billScheduleRepository;
    private final BillRecordRepository billRecordRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;

    @Transactional
    public BillRecordResponse markBillPaid(String email, Long billRecordID) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BillRecord billRecord = billRecordRepository.findById(billRecordID)
                .orElseThrow(() -> new RuntimeException("Bill record not found"));

        if (!billRecord.getBill().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bill does not belong to user");
        }

        Account account = accountService.getAccountByEmail(email);

        account.setCurrentBalance(
                account.getCurrentBalance().subtract(billRecord.getActualAmount())
        );


        billRecord.setPaid(true);
         billRecord.setPaidAt(LocalDateTime.now());

        BillRecord savedRecord = billRecordRepository.save(billRecord);

        return mapToResponse(savedRecord);
    }

    public BigDecimal getTotalUnpaidBills(String email){

        return billRecordRepository.getTotalUnpaidBills(email);
    }

    public List<BillRecordResponse> getUpcomingBills(String email) {
        synchronizeBillRecords(email);

        LocalDate today = LocalDate.now();

        int currentYear = today.getYear();
        Integer currentMonth = today.getMonthValue();
        Integer currentDay = today.getDayOfMonth();
        return billRecordRepository
                .findUpcomingBills( email, currentYear, currentMonth, currentDay)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public List<BillRecordResponse> getOverdueBills(String email) {

        LocalDate today = LocalDate.now();

        int currentYear = today.getYear();
        Integer currentMonth = today.getMonthValue();
        Integer currentDay = today.getDayOfMonth();
        return billRecordRepository
                .findOverdueBills( email, currentYear, currentMonth, currentDay)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<BillRecordResponse> getPaidBills(String email) {

        LocalDate today = LocalDate.now();

        int currentYear = today.getYear();
        Integer currentMonth = today.getMonthValue();
        Integer currentDay = today.getDayOfMonth();
        return billRecordRepository
                .findByBillUserEmailAndRecordYearAndRecordMonthAndPaidTrueOrderByPaidAtDesc( email, currentYear, currentMonth, currentDay)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public void synchronizeBillRecords(String email) {

        // Find new schedules for the current period
        List<BillSchedule> newSchedules = billScheduleRepository.findByBillUserEmailAndBillActiveTrue(email);
        // Create a timeSnapShot for today
        TimeSnapshot timeSnapshot = TimeSnapshot.currentDate();
        //Retrieve existing records per user
        List<BillRecord> currentBillRecords = billRecordRepository.findByBillUserEmailAndRecordYearAndRecordMonth
                (email, timeSnapshot.year(), timeSnapshot.month());

        Set<BillOccurrenceKey> existingKeys = currentBillRecords.stream()
                .map(record -> new BillOccurrenceKey(record.getBillSchedule().getId(), record.getRecordMonth(), record.getRecordYear()))
                .collect(Collectors.toSet());


        for(BillSchedule schedule : newSchedules){

            BillOccurrenceKey newKey = new BillOccurrenceKey(
                    schedule.getId(),
                    timeSnapshot.month(),
                    timeSnapshot.year()
            );

            //Save the new record if it doesn't exist already
            if(!existingKeys.contains(newKey)){
                //Add the record to the db
                BillRecord newBillRecord = BillRecord.builder()
                        .bill(schedule.getBill())
                        .billSchedule(schedule)
                        .recordMonth(timeSnapshot.month())
                        .recordYear(timeSnapshot.year())
                        .actualAmount(schedule.getBill().getEstimatedAmount())
                        .paidAt(null)
                        .build();
                billRecordRepository.save(newBillRecord);
            }
        }

    }

//    public List<BillRecordResponse> getPaidBillsThisMonth(String email) {
//
//        LocalDate today = LocalDate.now();
//        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
//        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
//
//        return billRecordRepository
//                .findByBillUserEmailAndPaidTrueAndDueDateBetweenOrderByDueDateDesc(
//                        email,startOfMonth, endOfMonth).stream().map(this::mapToResponse).toList();
//    }

//    public List<BillRecordResponse> getOverdueBills(String email) {
//
//        LocalDate today = LocalDate.now();
//
//        return billRecordRepository
//                .findByBillUserEmailAndPaidFalseAndDueDateBeforeOrderByDueDateAsc(email, today)
//                .stream().map(this::mapToResponse)
//                .toList();
//    }

    private BillRecordResponse mapToResponse(BillRecord billRecord) {
        return new BillRecordResponse(
                billRecord.getId(),
                billRecord.getBill().getId(),
                billRecord.getBillSchedule().getId(),
                billRecord.getBill().getName(),
                billRecord.getRecordMonth(),
                billRecord.getRecordYear(),
                billRecord.getBillSchedule().getDueDay(),
                billRecord.getActualAmount(),
                billRecord.getPaidAt(),
                billRecord.getCreatedAt()
        );

    }


}