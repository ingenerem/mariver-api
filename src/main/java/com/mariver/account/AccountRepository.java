package com.mariver.account;

import com.mariver.user.User;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUser(User user);

    Optional<Account> findByUser(User user);

    Optional<Account> findByUserEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT a FROM Account a
                WHERE a.user.email = :email
            """)
    Optional<Account> findByUserEmailForUpdate(@Param("email") String email);
}
