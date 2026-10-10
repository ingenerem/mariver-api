package com.mariver.bill;


import com.mariver.bill.dto.BillResponse;
import com.mariver.bill.dto.BillRequest;
import com.mariver.bill.dto.BillScheduleResponse;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final BillScheduleRepository billScheduleRepository;
    private final UserRepository userRepository;
    private final BillRecordService billRecordService;

    public BillService(
            BillRepository billRepository,
            BillScheduleRepository billScheduleRepository,
            UserRepository userRepository, BillRecordService billRecordService)
    {
        this.billRepository = billRepository;
        this.billScheduleRepository = billScheduleRepository;
        this.userRepository = userRepository;
        this.billRecordService = billRecordService;
    }

    @Transactional
    public BillResponse createBill(String email, BillRequest request) {

        User user = getUserByEmail(email);
        return createBillHelper(user, request);
    }

    public List<BillResponse> getActiveBills(String email) {
        User user = getUserByEmail(email);

        return billRepository.findByUserAndActiveTrue(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public List<BillResponse> getAllBills(String email) {
        User user = getUserByEmail(email);
        return billRepository.findAllByUserEmail(email).stream().map(this::toResponse).toList();

    }

    public List<BillResponse> getInactiveBills(String email) {
        User user = getUserByEmail(email);

        return billRepository.findByUserAndActiveFalse(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BillResponse updateBill(String email, Long billId, BillRequest request) {
        User user = getUserByEmail(email);
        validateSchedule(request);

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (!bill.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bill does not belong to user");
        }

        bill.setName(request.name());

        bill.setEstimatedAmount(request.amount());
        bill.setCategory(request.category());

        BillSchedule schedule = billScheduleRepository.findByBill(bill)
                .orElseThrow(() -> new RuntimeException("Bill schedule not found"));

        schedule.setIntervalValue(request.intervalValue());
        schedule.setIntervalUnit(request.intervalUnit());
        schedule.setNextDueDate(request.startDate());

        return toResponse(bill, schedule);
    }

    @Transactional
    public BillResponse deactivateBill(String email, Long billId) {
        Bill bill = getUserBill(email, billId);
        bill.deactivate();
        return toResponse(bill);
    }

    @Transactional
    public BillResponse activateBill(String email, Long billId) {
        Bill bill = getUserBill(email, billId);
        bill.activate();
        return toResponse(bill);
    }

    private void validateSchedule(BillRequest request) {

        if (request.startDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (request.intervalValue() == null || request.intervalValue() <= 0) {
            throw new IllegalArgumentException("Interval value must be greater than 0");
        }

        if (request.intervalUnit() == null) {
            throw new IllegalArgumentException("Interval unit is required");
        }
    }

    private Bill getUserBill(String email, Long billId) {
        User user = getUserByEmail(email);

        return billRepository.findByIdAndUser(billId, user)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private BillResponse toResponse(Bill bill) {
        BillSchedule schedule = billScheduleRepository.findByBill(bill)
                .orElseThrow(() -> new RuntimeException("Bill schedule not found"));

        return toResponse(bill, schedule);
    }

    private BillResponse toResponse(Bill bill, BillSchedule schedule) {
        return new BillResponse(
                bill.getId(),
                bill.getName(),
                bill.getEstimatedAmount(),
                bill.getCategory(),
                bill.isActive(),
                new BillScheduleResponse(
                        schedule.getId(),
                        schedule.isActive(),
                        schedule.getIntervalValue(),
                        schedule.getIntervalUnit(),
                        schedule.getStartDate(),
                        schedule.isAutoPay(),
                        schedule.getNextDueDate()
                )
        );
    }

    @Transactional
    public void createBills(String email, List<BillRequest> requests) {
        User user = getUserByEmail(email);
        requests.forEach(request -> createBillHelper(user, request));
    }

    private BillResponse createBillHelper(User user, BillRequest request) {

        String billName = request.name().trim();

        if (request.autoPay() && request.paymentType() == PaymentType.VARIABLE) {
            throw new IllegalArgumentException("Auto pay is not supported for variable bills");
        }
        if (billRepository.existsByUserAndNameIgnoreCase(user, billName)) {
            throw new IllegalArgumentException("A bill named '" + billName +"' already exists");
        }

        validateSchedule(request);

        Bill bill = Bill.builder()
                .user(user)
                .name(request.name().trim())
                .category(request.category())
                .estimatedAmount(request.amount())
                .paymentType(request.paymentType())
                .build();

        billRepository.save(bill);

        Bill savedBill = billRepository.save(bill);

        BillSchedule billSchedule = BillSchedule.builder()
                .bill(savedBill)
                .intervalValue(request.intervalValue())
                .intervalUnit(request.intervalUnit())
                .startDate(request.startDate())
                .nextDueDate(request.startDate())
                .autoPay(request.autoPay())
                .build();

        BillSchedule savedSchedule = billScheduleRepository.save(billSchedule);

        return toResponse(savedBill, savedSchedule);
    }

}