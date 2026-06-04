package com.mariver.income.record;

import com.mariver.income.source.IncomeSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "income_records",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_income_record_source_month_year",
                        columnNames = {
                                "income_source_id",
                                "record_month",
                                "record_year"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_source_id", nullable = false)
    private IncomeSource incomeSource;

    @Column(name = "record_month", nullable = false)
    private Integer recordMonth;

    @Column(name = "record_year", nullable = false)
    private Integer recordYear;

    @Column(name = "expected_amount",
            nullable = false,
            precision = 12,
            scale = 2)
    private BigDecimal expectedAmount;

    @Column(name = "received_amount",
            precision = 12,
            scale = 2)
    private BigDecimal receivedAmount;

    @Column(nullable = false)
    private boolean received = false;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 255)
    private String note;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void markReceived(BigDecimal receivedAmount, LocalDateTime now, String note)
    {
        this.received = true;
        this.receivedAmount = receivedAmount;
        this.receivedAt = LocalDateTime.now();
        this.note = note;
    }
}