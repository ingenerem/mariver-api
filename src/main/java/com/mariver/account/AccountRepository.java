package com.mariver.account;

import com.mariver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUser(User user);
    Optional<Account> findByUser(User user);

    Optional<Account> findByUserEmail(String email);
}
