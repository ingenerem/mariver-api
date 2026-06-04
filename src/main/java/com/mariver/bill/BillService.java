package com.mariver.bill;


import com.mariver.bill.dto.BillResponse;
import com.mariver.bill.dto.BillRequest;
import com.mariver.bill.dto.BillScheduleResponse;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final BillScheduleRepository billScheduleRepository;
    private final UserRepository userRepository;

    public BillService(
            BillRepository billRepository,
            BillScheduleRepository billScheduleRepository,
            UserRepository userRepository)
    {
        this.billRepository = billRepository;
        this.billScheduleRepository = billScheduleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BillResponse createBill(String email, BillRequest request) {
        User user = getUserByEmail(email);
        validateSchedule(request);

        Bill bill = new Bill(
                user,
                request.name(),
                request.amount(),
                request.category()
        );

        Bill savedBill = billRepository.save(bill);

        BillSchedule schedule = new BillSchedule(
                savedBill,
                request.intervalValue(),
                request.intervalUnit(),
                request.dueDay(),
                request.dueMonth()
        );

        BillSchedule savedSchedule = billScheduleRepository.save(schedule);

        return toResponse(savedBill, savedSchedule);
    }

    public List<BillResponse> getActiveBills(String email) {
        User user = getUserByEmail(email);

        return billRepository.findByUserAndActiveTrue(user)
                .stream()
                .map(this::toResponse)
                .toList();
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
        schedule.setDueDay(request.dueDay());
        schedule.setDueMonth(request.dueMonth());

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
        if (request.intervalValue() != 1) {
            throw new RuntimeException("Only monthly and yearly bills are supported for now");
        }

        if (request.intervalUnit() == IntervalUnit.MONTH && request.dueMonth() != null) {
            throw new RuntimeException("Monthly bills should not have a due month");
        }

        if (request.intervalUnit() == IntervalUnit.YEAR && request.dueMonth() == null) {
            throw new RuntimeException("Yearly bills must have a due month");
        }

        if (request.intervalUnit() == IntervalUnit.WEEK) {
            throw new RuntimeException("Weekly bills are not supported yet");
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
                        schedule.getIntervalValue(),
                        schedule.getIntervalUnit(),
                        schedule.getDueDay(),
                        schedule.getDueMonth()
                )
        );
    }
}