CREATE TABLE bill_records (
    id BIGSERIAL PRIMARY KEY,
    bill_schedule_id BIGINT NOT NULL,
    due_date DATE NOT NULL,
    estimated_amount NUMERIC(12,2) NOT NULL,
    actual_amount NUMERIC(12,2),
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    paid_date DATE,
    transaction_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_records_bill_schedule
        FOREIGN KEY (bill_schedule_id)
            REFERENCES bill_schedules(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_bill_records_transaction
        FOREIGN KEY (transaction_id)
            REFERENCES transactions(id),
    CONSTRAINT unique_bill_record_per_schedule_date
        UNIQUE (bill_schedule_id, due_date)
);