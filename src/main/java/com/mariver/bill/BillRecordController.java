package com.mariver.bill;

import com.mariver.bill.dto.BillRecordRequest;
import com.mariver.bill.dto.BillRecordResponse;
import com.mariver.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bill_record")
@RequiredArgsConstructor
public class BillRecordController {
    private final BillRecordService billRecordService;

    @GetMapping
    public List<BillRecordResponse> getUpcomingBills(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return billRecordService.getUpcomingBills(user.getEmail());

    }

    @GetMapping("/overdue")
    public List<BillRecordResponse> getOverDueBills(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return billRecordService.getOverdueBills(user.getEmail());

    }

    @GetMapping("/paid")
    public List<BillRecordResponse> getPaidBills(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return billRecordService.getPaidBills(user.getEmail());

    }


    @PostMapping("/sync")
    public ResponseEntity<Void> synchronize(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        billRecordService.synchronizeBillRecords(user.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{billRecordId}/pay")
    public ResponseEntity<BillRecordResponse> markBillPaid(
            Authentication authentication, @PathVariable Long billRecordId)
    {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(billRecordService.markBillPaid(user.getEmail(), billRecordId));
    }


}
