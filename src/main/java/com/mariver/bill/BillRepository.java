package com.mariver.bill;

import com.mariver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByUserAndActiveTrue(User user);

    List<Bill> findByUserAndActiveFalse(User user);

    Optional<Bill> findByIdAndUser(Long billId, User user);

    boolean existsByUserAndNameIgnoreCase(User user, String billName);
}