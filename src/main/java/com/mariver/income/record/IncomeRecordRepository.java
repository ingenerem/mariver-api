package com.mariver.income.record;

import com.mariver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncomeRecordRepository extends JpaRepository<IncomeRecord, Long> {

    List<IncomeRecord> findByIncomeSourceUserAndRecordMonthAndRecordYear(
            User user,
            Integer recordMonth,
            Integer recordYear
    );

    Optional<IncomeRecord> findByIncomeSourceIdAndRecordMonthAndRecordYear(
            Long incomeSourceId,
            Integer recordMonth,
            Integer recordYear
    );

    List<IncomeRecord> findByIncomeSourceId(Long incomeSourceId);
}
