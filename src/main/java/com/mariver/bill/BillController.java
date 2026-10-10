package com.mariver.bill;

import com.mariver.bill.dto.BillRecordRequest;
import com.mariver.bill.dto.BillRecordResponse;
import com.mariver.bill.dto.BillRequest;
import com.mariver.bill.dto.BillResponse;
import com.mariver.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok(billService.createBill(authentication.getName(), request));
    }

    @PostMapping("/batch")
    public ResponseEntity<String> createBills(
            Authentication authentication,
            @RequestBody List<BillRequest> requests) {

        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        try {
            billService.createBills(email, requests);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Bills saved successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> getActiveBills(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        return ResponseEntity.ok( billService.getActiveBills(email));
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<BillResponse>> getInactiveBills(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        return ResponseEntity.ok(billService.getInactiveBills(email) );
    }

    @PatchMapping("/{billId}")
    public ResponseEntity<BillResponse> updateBill(Authentication authentication, @PathVariable Long billId,
                                                   @RequestBody BillRequest request) {

        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        return ResponseEntity.ok(
                billService.updateBill(email, billId, request)
        );
    }

    @DeleteMapping("/{billId}/deactivate")
    public ResponseEntity<Void> deactivateBill(Authentication authentication, @PathVariable Long billId) {
        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        billService.deactivateBill(email, billId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/allBills")
    public ResponseEntity<List<BillResponse>> getAllBills(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();
        return ResponseEntity.ok( billService.getAllBills(email));
    }

}