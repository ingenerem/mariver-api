CREATE TABLE bill_schedules (
    id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    interval_value INTEGER NOT NULL,
    interval_unit VARCHAR(20) NOT NULL,
    due_day INTEGER,
    due_month INTEGER,
    start_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_schedules_bill
        FOREIGN KEY (bill_id)
            REFERENCES bills(id)
            ON DELETE CASCADE
);