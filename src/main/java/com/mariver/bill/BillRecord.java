package com.mariver.bill;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "bill_records",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_bill_record_per_schedule_month",
                        columnNames = {"bill_schedule_id", "record_month", "record_year"}
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

    /*
     * The bill this payment belongs to.
     *
     * Example:
     * Bill = Electricity
     * BillRecord = June 2026 electricity payment
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    /*
     * The schedule occurrence this payment came from.
     *
     * Example:
     * Schedule = monthly on the 20th
     * BillRecord = June 2026 occurrence was paid
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_schedule_id", nullable = false)
    private BillSchedule billSchedule;

    /*
     * The billing period this record belongs to.
     *
     * This is different from paidAt.
     *
     * Example:
     * June electricity bill paid late on July 2:
     * recordMonth = 6
     * recordYear = 2026
     * paidAt = July 2, 2026
     */
    @Column(name = "record_month", nullable = false)
    private Integer recordMonth;

    @Column(name = "record_year", nullable = false)
    private Integer recordYear;

    /*
     * The actual amount paid.
     *
     * For fixed bills, this is usually the bill's estimatedAmount.
     * For variable bills, this is entered when the real bill amount is known.
     */
    @Column(name = "actual_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal actualAmount;

    /*
     * When the bill was actually paid.
     *
     * This can be different from the scheduled due date.
     */
    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(nullable = false)
    private boolean paid =false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.paidAt == null) {
            this.paidAt = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}