package com.mariver.bill;

import com.mariver.transaction.Transaction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "bill_records",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_bill_record_per_schedule_date",
                        columnNames = {"bill_schedule_id", "due_date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_schedule_id", nullable = false)
    private BillSchedule billSchedule;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "estimated_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedAmount;

    @Column(name = "actual_amount", precision = 12, scale = 2)
    private BigDecimal actualAmount;

    @Column(nullable = false)
    @Builder.Default
    private boolean paid = false;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}