package com.mariver.bill;

import com.mariver.bill.dto.BillRecordRequest;
import com.mariver.bill.dto.BillRecordResponse;
import com.mariver.bill.dto.BillRequest;
import com.mariver.bill.dto.BillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;
    private final BillRecordService billRecordService;

    @PostMapping
    public ResponseEntity<BillResponse> createBill(
            Authentication authentication,
            @RequestBody BillRequest request
    ) {
        return ResponseEntity.ok(
                billService.createBill(authentication.getName(), request)
        );
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> getActiveBills(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                billService.getActiveBills(authentication.getName())
        );
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<BillResponse>> getInactiveBills(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                billService.getInactiveBills(authentication.getName())
        );
    }

    @PatchMapping("/{billId}")
    public ResponseEntity<BillResponse> updateBill(
            Authentication authentication,
            @PathVariable Long billId,
            @RequestBody BillRequest request
    ) {
        return ResponseEntity.ok(
                billService.updateBill(authentication.getName(), billId, request)
        );
    }

    @DeleteMapping("/{billId}")
    public ResponseEntity<Void> deactivateBill(
            Authentication authentication,
            @PathVariable Long billId
    ) {
        billService.deactivateBill(authentication.getName(), billId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/records")
    public ResponseEntity<BillRecordResponse> markBillPaid(
            Authentication authentication,
            @RequestBody BillRecordRequest request
    ) {
        return ResponseEntity.ok(
                billRecordService.markBillPaid(authentication.getName(), request)
        );
    }
}