CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    account_id BIGINT NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    type VARCHAR(30) NOT NULL,
    category VARCHAR(50),
    description VARCHAR(255),
    transaction_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'POSTED',
    deleted_at TIMESTAMP,
    delete_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id)
            REFERENCES accounts(id)
            ON DELETE CASCADE
);