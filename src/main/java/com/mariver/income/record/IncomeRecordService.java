package com.mariver.income.record;

import com.mariver.income.record.dto.IncomeRecordRequest;
import com.mariver.income.record.dto.IncomeRecordResponse;
import com.mariver.income.source.IncomeSource;
import com.mariver.income.source.IncomeSourceRepository;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class IncomeRecordService {

    private final IncomeRecordRepository incomeRecordRepository;
    private final IncomeSourceRepository incomeSourceRepository;
    private final UserRepository userRepository;

    public IncomeRecordService(IncomeRecordRepository incomeRecordRepository,
                               IncomeSourceRepository incomeSourceRepository,
                               UserRepository userRepository)
    {
        this.incomeRecordRepository = incomeRecordRepository;
        this.incomeSourceRepository = incomeSourceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public IncomeRecordResponse markIncomeReceived(String email, Long incomeSourceId,
                                                   IncomeRecordRequest request)
    {
        User user = findUserByEmail(email);

        IncomeSource incomeSource = incomeSourceRepository.findById(incomeSourceId)
                .orElseThrow(() -> new RuntimeException("Income source not found"));

        if (!incomeSource.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Income source does not belong to user");
        }

        LocalDate today = LocalDate.now();
        Integer recordMonth = today.getMonthValue();
        Integer recordYear = today.getYear();

        IncomeRecord incomeRecord = incomeRecordRepository
                .findByIncomeSourceIdAndRecordMonthAndRecordYear(
                        incomeSourceId,
                        recordMonth,
                        recordYear
                )
                .orElseGet(() -> {
                    IncomeRecord record = new IncomeRecord();

                    record.setIncomeSource(incomeSource);
                    record.setRecordMonth(recordMonth);
                    record.setRecordYear(recordYear);
                    record.setExpectedAmount(incomeSource.getAmount());
                    record.setReceived(false);
                    return record;

                });

        incomeRecord.markReceived(
                request.receivedAmount(),
                LocalDateTime.now(),
                request.note()
        );

        IncomeRecord savedIncomeRecord =
                incomeRecordRepository.save(incomeRecord);

        return toResponse(savedIncomeRecord);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private IncomeRecordResponse toResponse(IncomeRecord incomeRecord) {
        return new IncomeRecordResponse(
                incomeRecord.getId(),
                incomeRecord.getIncomeSource().getId(),
                incomeRecord.getRecordMonth(),
                incomeRecord.getRecordYear(),
                incomeRecord.getExpectedAmount(),
                incomeRecord.getReceivedAmount(),
                incomeRecord.isReceived(),
                incomeRecord.getReceivedAt(),
                incomeRecord.getNote()
        );
    }
}