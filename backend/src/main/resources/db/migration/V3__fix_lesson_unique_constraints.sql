ALTER TABLE lessons
    DROP CONSTRAINT IF EXISTS lessons_lesson_date_tutor_id_time_from_key;

ALTER TABLE lessons
    DROP CONSTRAINT IF EXISTS lessons_lesson_date_student_id_time_from_key;

CREATE UNIQUE INDEX lessons_tutor_slot_unique
    ON lessons (lesson_date, tutor_id, time_from)
    WHERE lesson_status <> 'CANCELLED';

CREATE UNIQUE INDEX lessons_student_slot_unique
    ON lessons (lesson_date, student_id, time_from)
    WHERE lesson_status <> 'CANCELLED';