ALTER TABLE users
    ALTER COLUMN user_role TYPE VARCHAR(50)
        USING user_role::VARCHAR;

ALTER TABLE lessons
    ALTER COLUMN format TYPE VARCHAR(50) USING format::VARCHAR,
    ALTER COLUMN lesson_status TYPE VARCHAR(50) USING lesson_status::VARCHAR,
    ALTER COLUMN payment_status TYPE VARCHAR(50) USING payment_status::VARCHAR;

DROP TYPE user_roles CASCADE;
DROP TYPE lesson_formats CASCADE;
DROP TYPE lesson_statuses CASCADE;
DROP TYPE payment_statuses CASCADE;