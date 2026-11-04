ALTER TABLE transactions
ADD COLUMN fund_id BIGINT;

ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_fund
FOREIGN KEY (fund_id) REFERENCES funds(id);