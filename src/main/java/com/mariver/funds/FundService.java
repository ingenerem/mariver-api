package com.mariver.funds;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mariver.account.Account;
import com.mariver.account.AccountRepository;
import com.mariver.bill.BillRecordRepository;
import com.mariver.bill.BillRecordService;
import com.mariver.funds.dto.FundAmountRequest;
import com.mariver.funds.dto.FundRequest;
import com.mariver.funds.dto.FundResponse;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FundService {

    private final FundRepository fundRepository;
    private final AccountRepository accountRepository;
    private final BillRecordRepository billRecordRepository;

    @Transactional
    public FundResponse createFund(String email, FundRequest request) {

        Account account = getAccount(email);

        Fund fund = Fund.builder()
                .account(account)
                .name(request.name().trim())
                .balance(BigDecimal.ZERO)
                .build();

        return toResponse(fundRepository.save(fund));
    }

    @Transactional(readOnly = true)
    public List<FundResponse> getFunds(String email) {

        Account account = getAccount(email);

        return fundRepository.findByAccountId(account.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FundResponse allocate(String email, Long fundId, FundAmountRequest request) {

        Account account = getAccountForUpdate(email);
        Fund fund = getFundForUpdate(fundId, account.getId());

        BigDecimal totalReserved = fundRepository.findByAccountId(account.getId())
                .stream()
                .map(Fund::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal available = account.getCurrentBalance().subtract(totalReserved);

        if (request.amount().compareTo(available) > 0) {
            throw new IllegalArgumentException("Insufficient available balance");
        }

        fund.setBalance(fund.getBalance().add(request.amount()));

        return toResponse(fundRepository.save(fund));
    }

    @Transactional
    public FundResponse release(String email, Long fundId, FundAmountRequest request) {

        Account account = getAccountForUpdate(email);
        Fund fund = getFundForUpdate(fundId, account.getId());

        if (request.amount().compareTo(fund.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient fund balance");
        }

        fund.setBalance(fund.getBalance().subtract(request.amount()));

        return toResponse(fundRepository.save(fund));
    }

    private Account getAccount(String email) {
        return accountRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private Account getAccountForUpdate(String email) {

        return accountRepository.findByUserEmailForUpdate(email)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private Fund getFundForUpdate(Long fundId, Long accountId) {
        return fundRepository.findByIdAndAccountIdForUpdate(fundId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Fund not found"));
    }

    private FundResponse toResponse(Fund fund) {
        return new FundResponse(
                fund.getId(),
                fund.getName(),
                fund.getBalance(),
                getAvailableBalance(fund),
                fund.getCreatedAt());
    }

    public BigDecimal getTotalFundBalance(Long accountId) {
        return fundRepository.getTotalFundBalance(accountId);
    }

    public Fund getFundById(Long accountId, Long fundId) {
        return fundRepository.findByIdAndAccountId(fundId, accountId)
                .orElseThrow(() -> new RuntimeException("Fund not found"));
    }

    public void deductFromFund(Long accountId, Long fundId, BigDecimal amount) {

        Fund fund = getFundById(accountId, fundId);

        if (fund.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient fund balance");
        }

        fund.setBalance(fund.getBalance().subtract(amount));
        fundRepository.save(fund);
    }

    public void restoreToFund(Long accountId, Long fundId, BigDecimal amount) {

        Fund fund = getFundById(accountId, fundId);

        fund.setBalance(fund.getBalance().add(amount));

        fundRepository.save(fund);
    }

    public BigDecimal getAvailableBalance(Fund fund) {
        BigDecimal unpaidBills = billRecordRepository.getTotalUnpaidBillsByFundId(fund.getId());

        return fund.getBalance().subtract(unpaidBills);
    }
}