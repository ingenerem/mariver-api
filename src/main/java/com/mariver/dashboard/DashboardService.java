package com.mariver.dashboard;

import com.mariver.account.Account;
import com.mariver.account.AccountRepository;
import com.mariver.bill.BillRecordService;
import com.mariver.funds.FundService;
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
        private final BillRecordService billRecordService;
        private final FundService fundService;

        public DashboardResponse getDashboardSummary(String email) {

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Account account = accountRepository.findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Account not found"));

                BigDecimal currentBalance = account.getCurrentBalance();

                BigDecimal protectedBills = billRecordService.getTotalUnpaidBillsForCurrentMonth(email);

                BigDecimal totalFunds = fundService.getTotalFundBalance(account.getId());

                BigDecimal accountBalance = currentBalance.subtract(totalFunds);

                BigDecimal spendableAmount = currentBalance
                                .subtract(protectedBills)
                                .subtract(totalFunds);

                return new DashboardResponse(currentBalance, protectedBills, accountBalance, spendableAmount);
        }
}