package com.mariver.funds;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.mariver.funds.dto.FundAmountRequest;
import com.mariver.funds.dto.FundRequest;
import com.mariver.funds.dto.FundResponse;
import com.mariver.user.User;

import java.util.List;

@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;

    @PostMapping
    public ResponseEntity<FundResponse> createFund(
            @Valid @RequestBody FundRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        FundResponse response = fundService.createFund( user.getEmail(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FundResponse>> getFunds(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                fundService.getFunds(user.getEmail()));
    }

    @PostMapping("/{fundId}/allocate")
    public ResponseEntity<FundResponse> allocate(
            @PathVariable Long fundId,
            @Valid @RequestBody FundAmountRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(fundService.allocate(user.getEmail(), fundId, request));
    }

    @PostMapping("/{fundId}/release")
    public ResponseEntity<FundResponse> release(
            @PathVariable Long fundId,
            @Valid @RequestBody FundAmountRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(fundService.release(user.getEmail(), fundId, request));
    }
}