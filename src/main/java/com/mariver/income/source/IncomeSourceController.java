package com.mariver.income.source;

import com.mariver.income.source.dto.IncomeSourceRequest;
import com.mariver.income.source.dto.IncomeSourceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income-sources")
public class IncomeSourceController {

    private final IncomeSourceService incomeSourceService;

    public IncomeSourceController(IncomeSourceService incomeSourceService)
    {
        this.incomeSourceService = incomeSourceService;
    }

    @GetMapping
    public ResponseEntity<List<IncomeSourceResponse>> getIncomeSources(Authentication authentication)
    {
        String email = authentication.getName();

        List<IncomeSourceResponse> incomeSources =
                incomeSourceService.getActiveIncomeSources(email);

        return ResponseEntity.ok(incomeSources);
    }

    @PostMapping
    public ResponseEntity<IncomeSourceResponse> createIncomeSource(Authentication authentication,
                                                                   @RequestBody IncomeSourceRequest request)
    {
        String email = authentication.getName();

        IncomeSourceResponse response =
                incomeSourceService.createIncomeSource(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{incomeSourceId}")
    public ResponseEntity<IncomeSourceResponse> updateIncomeSource(Authentication authentication,
                                                                   @PathVariable Long incomeSourceId,
                                                                   @RequestBody IncomeSourceRequest request)
    {
        String email = authentication.getName();

        IncomeSourceResponse response =
                incomeSourceService.updateIncomeSource(email, incomeSourceId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{incomeSourceId}/deactivate")
    public ResponseEntity<Void> deactivateIncomeSource(Authentication authentication,
                                                       @PathVariable Long incomeSourceId)
    {
        String email = authentication.getName();

        incomeSourceService.deactivateIncomeSource(email, incomeSourceId);

        return ResponseEntity.noContent().build();
    }
}