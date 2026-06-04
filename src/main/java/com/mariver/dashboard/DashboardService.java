package com.mariver.dashboard;

import com.mariver.account.Account;
import com.mariver.account.AccountRepository;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public DashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal currentBalance = account.getCurrentBalance();

        BigDecimal protectedBills = BigDecimal.ZERO;
        BigDecimal emergencyFund = BigDecimal.ZERO;
        BigDecimal otherSavings = BigDecimal.ZERO;

        BigDecimal spendableAmount = currentBalance
                .subtract(protectedBills)
                .subtract(emergencyFund)
                .subtract(otherSavings);

        return new DashboardResponse(
                currentBalance,
                protectedBills,
                emergencyFund,
                otherSavings,
                spendableAmount
        );
    }
}