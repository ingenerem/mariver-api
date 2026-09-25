CREATE UNIQUE INDEX uk_bills_user_name_ci
    ON bills (user_id, LOWER(TRIM(name)));