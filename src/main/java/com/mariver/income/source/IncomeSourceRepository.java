package com.mariver.income.source;

import com.mariver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeSourceRepository extends JpaRepository<IncomeSource, Long> {

    List<IncomeSource> findByUserAndActiveTrue(User user);

    List<IncomeSource> findByUser(User user);
}