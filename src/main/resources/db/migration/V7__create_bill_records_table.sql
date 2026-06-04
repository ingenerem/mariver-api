CREATE TABLE bill_records (
    id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    bill_schedule_id BIGINT NOT NULL,
    record_month INTEGER NOT NULL,
    record_year INTEGER NOT NULL,
    actual_amount NUMERIC(12,2) NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_records_bill
        FOREIGN KEY (bill_id)
            REFERENCES bills(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_bill_records_bill_schedule
        FOREIGN KEY (bill_schedule_id)
            REFERENCES bill_schedules(id)
            ON DELETE CASCADE,
    CONSTRAINT unique_bill_record_per_schedule_month
        UNIQUE (bill_schedule_id, record_month, record_year)
);