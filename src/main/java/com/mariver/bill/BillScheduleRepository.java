package com.mariver.bill;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillScheduleRepository extends JpaRepository<BillSchedule, Long> {

    Optional<BillSchedule> findByBill(Bill bill);
}