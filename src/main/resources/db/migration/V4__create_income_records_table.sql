CREATE TABLE income_records (
    id BIGSERIAL PRIMARY KEY,
    income_source_id BIGINT NOT NULL,
    record_month INTEGER NOT NULL,
    record_year INTEGER NOT NULL,
    expected_amount NUMERIC(12,2) NOT NULL,
    received_amount NUMERIC(12,2),
    received BOOLEAN NOT NULL DEFAULT FALSE,
    received_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note VARCHAR(255),
    CONSTRAINT fk_income_records_income_source
        FOREIGN KEY (income_source_id)
            REFERENCES income_sources(id)
            ON DELETE CASCADE,
    CONSTRAINT chk_income_records_month
        CHECK (record_month BETWEEN 1 AND 12),
    CONSTRAINT chk_income_records_year
        CHECK (record_year > 2000),
    CONSTRAINT chk_income_records_expected_amount
        CHECK (expected_amount > 0),
    CONSTRAINT chk_income_records_received_amount
        CHECK (received_amount IS NULL OR received_amount > 0),
    CONSTRAINT uq_income_record_source_month_year
        UNIQUE (income_source_id, record_month, record_year)
);