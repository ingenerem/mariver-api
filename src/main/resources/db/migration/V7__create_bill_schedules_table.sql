CREATE TABLE bill_schedules (
    id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    interval_value INTEGER NOT NULL,
    interval_unit VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    next_due_date DATE NOT NULL,
    auto_pay BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_schedules_bill
        FOREIGN KEY (bill_id)
            REFERENCES bills(id)
            ON DELETE CASCADE
);