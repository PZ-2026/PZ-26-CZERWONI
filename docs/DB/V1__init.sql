--  Enum types

CREATE TYPE user_roles        AS ENUM ('student', 'tutor', 'admin');
CREATE TYPE lesson_formats    AS ENUM ('online', 'in_person');
CREATE TYPE lesson_statuses   AS ENUM ('pending', 'confirmed', 'completed', 'cancelled');
CREATE TYPE payment_statuses  AS ENUM ('pending', 'paid', 'cancelled');

-- Tables

CREATE TABLE subject_categories (
    id             SERIAL        PRIMARY KEY,
    category_name  VARCHAR(100)  NOT NULL UNIQUE
);

CREATE TABLE subjects (
    id            SERIAL        PRIMARY KEY,
    subject_name  VARCHAR(100)  NOT NULL UNIQUE,
    category_id   INT           NOT NULL REFERENCES subject_categories(id)
);

CREATE TABLE users (
    id             SERIAL        PRIMARY KEY,
    first_name     VARCHAR(50)   NOT NULL,
    last_name      VARCHAR(50)   NOT NULL,
    email          VARCHAR(100)  NOT NULL UNIQUE,
    phone_number   VARCHAR(10)   NOT NULL UNIQUE,
    password_hash  VARCHAR(255)  NOT NULL,
    avatar_url     VARCHAR(255),
    user_role      user_roles    NOT NULL,
    is_active      BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP     DEFAULT NOW(),
    updated_at     TIMESTAMP     DEFAULT NOW()
);

CREATE TABLE tutors (
    user_id           INT             PRIMARY KEY REFERENCES users(id),
    bio               TEXT,
    hourly_rate       DECIMAL(10, 2)  NOT NULL CHECK (hourly_rate >= 0),
    offers_online     BOOLEAN         NOT NULL DEFAULT FALSE,
    offers_in_person  BOOLEAN         NOT NULL DEFAULT FALSE
);

CREATE TABLE tutor_subjects (
    id                  SERIAL   PRIMARY KEY,
    tutor_id            INT      NOT NULL REFERENCES tutors(user_id),
    subject_id          INT      NOT NULL REFERENCES subjects(id),
    level_primary       BOOLEAN  NOT NULL DEFAULT FALSE,
    level_high_school   BOOLEAN  NOT NULL DEFAULT FALSE,
    level_university    BOOLEAN  NOT NULL DEFAULT FALSE,
    level_exam_prep     BOOLEAN  NOT NULL DEFAULT FALSE,
    level_professional  BOOLEAN  NOT NULL DEFAULT FALSE,

    UNIQUE (tutor_id, subject_id)
);

CREATE TABLE lessons (
    id              SERIAL            PRIMARY KEY,
    tutor_id        INT               NOT NULL REFERENCES tutors(user_id),
    student_id      INT               NOT NULL REFERENCES users(id),
    subject_id      INT               NOT NULL REFERENCES subjects(id),
    lesson_date     DATE              NOT NULL,
    time_from       TIME              NOT NULL,
    time_to         TIME              NOT NULL,
    format          lesson_formats    NOT NULL,
    lesson_status   lesson_statuses   NOT NULL DEFAULT 'pending',
    tutor_notes     TEXT,
    student_notes   TEXT,
    amount          DECIMAL(10, 2)    NOT NULL CHECK (amount >= 0),
    payment_status  payment_statuses  NOT NULL DEFAULT 'pending',
    payment_date    TIMESTAMP,
    created_at      TIMESTAMP         DEFAULT NOW(),
    updated_at      TIMESTAMP         DEFAULT NOW(),

    CONSTRAINT chk_lesson_time  CHECK (time_to > time_from),
    CONSTRAINT chk_not_self     CHECK (tutor_id <> student_id),

    UNIQUE (lesson_date, tutor_id,   time_from),
    UNIQUE (lesson_date, student_id, time_from)
);

CREATE TABLE reviews (
    id          SERIAL         PRIMARY KEY,
    tutor_id    INT            NOT NULL REFERENCES tutors(user_id),
    user_id     INT            NOT NULL REFERENCES users(id),
    rating      DECIMAL(2, 1)  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  TIMESTAMP      DEFAULT NOW(),
    updated_at  TIMESTAMP      DEFAULT NOW(),

    UNIQUE (tutor_id, user_id)
);

CREATE TABLE tutor_availability_recurring (
    id           SERIAL    PRIMARY KEY,
    tutor_id     INT       NOT NULL REFERENCES tutors(user_id),
    day_of_week  SMALLINT  NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    time_from    TIME      NOT NULL,
    time_to      TIME      NOT NULL,
    date_to      DATE,     -- NULL = obowiązuje bezterminowo

    CONSTRAINT chk_recurring_time CHECK (time_to > time_from),

    UNIQUE (tutor_id, date_to, day_of_week)
);

CREATE TABLE tutor_availability_override (
    id             SERIAL  PRIMARY KEY,
    tutor_id       INT     NOT NULL REFERENCES tutors(user_id),
    override_date  DATE    NOT NULL,
    time_from      TIME,   -- NULL = cały dzień niedostępny
    time_to        TIME,   -- NULL = cały dzień niedostępny

    CONSTRAINT chk_override_both_or_none
        CHECK (
            (time_from IS NULL AND time_to IS NULL) OR
            (time_from IS NOT NULL AND time_to IS NOT NULL)
        ),
    CONSTRAINT chk_override_time
        CHECK (time_from IS NULL OR time_to > time_from),

    UNIQUE (tutor_id, override_date)
);

CREATE TABLE holidays (
    id            SERIAL        PRIMARY KEY,
    holiday_date  DATE          NOT NULL UNIQUE,
    description   VARCHAR(100)
);

-- Triggers

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_lessons_updated_at
    BEFORE UPDATE ON lessons
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_reviews_updated_at
    BEFORE UPDATE ON reviews
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();