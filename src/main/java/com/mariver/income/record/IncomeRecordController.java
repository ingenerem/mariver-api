package com.mariver.income.record;

import com.mariver.income.record.dto.IncomeRecordRequest;
import com.mariver.income.record.dto.IncomeRecordResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/income-sources")
public class IncomeRecordController {

    private final IncomeRecordService incomeRecordService;

    public IncomeRecordController(IncomeRecordService incomeRecordService) {
        this.incomeRecordService = incomeRecordService;
    }

    @PatchMapping("/{incomeSourceId}/received")
    public ResponseEntity<IncomeRecordResponse> recordIncome(
            Authentication authentication,
            @PathVariable Long incomeSourceId,
            @RequestBody IncomeRecordRequest request
    ) {
        String email = authentication.getName();

        IncomeRecordResponse response =
                incomeRecordService.markIncomeReceived(
                        email,
                        incomeSourceId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}