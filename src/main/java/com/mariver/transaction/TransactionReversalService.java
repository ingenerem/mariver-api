package com.mariver.transaction;

import com.mariver.account.AccountService;
import com.mariver.bill.BillRecordService;
import com.mariver.bill.BillService;
import com.mariver.transaction.dto.TransactionRequest;
import com.mariver.transaction.dto.TransactionResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.mariver.transaction.TransactionSource.*;

@Service
@RequiredArgsConstructor
public class TransactionReversalService {

    private final BillRecordService billRecordService;
    private final TransactionService transactionService;
    private final AccountService accountService;

    @Transactional
    public void reverseTransaction(String email, Long transactionId, String reason) {

        Transaction transaction = transactionService.deleteTransaction(email, transactionId, reason);


        switch (transaction.getTransactionSource()) {
            case BILL -> billRecordService.reversePayment(transaction.getId());

            case INCOME -> {
            //Coming soon
            }


        }

    }
}