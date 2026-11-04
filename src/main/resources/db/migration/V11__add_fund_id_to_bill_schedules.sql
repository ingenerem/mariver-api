ALTER TABLE bill_schedules
ADD COLUMN fund_id BIGINT;

ALTER TABLE bill_schedules
ADD CONSTRAINT fk_bill_schedules_fund
FOREIGN KEY (fund_id) REFERENCES funds(id);