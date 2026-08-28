package com.mariver.bill;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // For MVP: one bill has one schedule.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false, unique = true)
    private Bill bill;

    @Column(name = "interval_value", nullable = false)
    private Integer intervalValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "interval_unit", nullable = false, length = 20)
    private IntervalUnit intervalUnit;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "due_day")
    private Integer dueDay;

    @Column(name = "due_month")
    private Integer dueMonth;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public BillSchedule( Bill bill, Integer intervalValue, IntervalUnit intervalUnit, LocalDate startDate,
                         Integer dueDay, Integer dueMonth)
    {
        this.bill = bill;
        this.intervalValue = intervalValue;
        this.intervalUnit = intervalUnit;
        this.startDate = startDate;
        this.dueDay = dueDay;
        this.dueMonth = dueMonth;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Add getters and setters
}