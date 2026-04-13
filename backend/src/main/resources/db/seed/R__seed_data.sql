-- 1. subject_categories
INSERT INTO subject_categories (category_name)
VALUES ('Matematyka i nauki ścisłe'),
       ('Języki obce'),
       ('Nauki humanistyczne'),
       ('Informatyka'),
       ('Nauki przyrodnicze'),
       ('Muzyka i sztuka')
ON CONFLICT (category_name) DO NOTHING;

-- 2. subjects
WITH cat AS (SELECT id, category_name
             FROM subject_categories)
INSERT
INTO subjects (subject_name, category_id)
VALUES ('Matematyka', (SELECT id FROM cat WHERE category_name = 'Matematyka i nauki ścisłe')),
       ('Fizyka', (SELECT id FROM cat WHERE category_name = 'Matematyka i nauki ścisłe')),
       ('Chemia', (SELECT id FROM cat WHERE category_name = 'Matematyka i nauki ścisłe')),
       ('Statystyka', (SELECT id FROM cat WHERE category_name = 'Matematyka i nauki ścisłe')),
       ('Język angielski', (SELECT id FROM cat WHERE category_name = 'Języki obce')),
       ('Język niemiecki', (SELECT id FROM cat WHERE category_name = 'Języki obce')),
       ('Język hiszpański', (SELECT id FROM cat WHERE category_name = 'Języki obce')),
       ('Język francuski', (SELECT id FROM cat WHERE category_name = 'Języki obce')),
       ('Historia', (SELECT id FROM cat WHERE category_name = 'Nauki humanistyczne')),
       ('Język polski', (SELECT id FROM cat WHERE category_name = 'Nauki humanistyczne')),
       ('Filozofia', (SELECT id FROM cat WHERE category_name = 'Nauki humanistyczne')),
       ('Programowanie', (SELECT id FROM cat WHERE category_name = 'Informatyka')),
       ('Bazy danych', (SELECT id FROM cat WHERE category_name = 'Informatyka')),
       ('Algorytmy', (SELECT id FROM cat WHERE category_name = 'Informatyka')),
       ('Biologia', (SELECT id FROM cat WHERE category_name = 'Nauki przyrodnicze')),
       ('Geografia', (SELECT id FROM cat WHERE category_name = 'Nauki przyrodnicze')),
       ('Gitara', (SELECT id FROM cat WHERE category_name = 'Muzyka i sztuka')),
       ('Fortepian', (SELECT id FROM cat WHERE category_name = 'Muzyka i sztuka'))
ON CONFLICT (subject_name) DO NOTHING;

-- 3. users
INSERT INTO users (first_name, last_name, email, phone_number, password_hash, user_role, avatar_url)
VALUES ('Anna', 'Kowalska', 'anna.kowalska@example.com', '501100001',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_1.jpg'),
       ('Marek', 'Nowak', 'marek.nowak@example.com', '501100002',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_2.jpg'),
       ('Katarzyna', 'Wiśniewska', 'k.wisniewska@example.com', '501100003',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_3.jpg'),
       ('Piotr', 'Zając', 'piotr.zajac@example.com', '501100004',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_4.jpg'),
       ('Monika', 'Lewandowska', 'monika.lewandowska@example.com', '501100005',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_5.jpg'),
       ('Tomasz', 'Adamski', 'tomasz.adamski@example.com', '502200001',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_6.jpg'),
       ('Zofia', 'Dąbrowska', 'zofia.dabrowska@example.com', '502200002',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_7.jpg'),
       ('Michał', 'Szymański', 'michal.szymanski@example.com', '502200003',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_8.jpg'),
       ('Natalia', 'Wójcik', 'natalia.wojcik@example.com', '502200004',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_9.jpg'),
       ('Kamil', 'Kowalczyk', 'kamil.kowalczyk@example.com', '502200005',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_10.jpg'),
       ('Aleksandra', 'Mazur', 'ola.mazur@example.com', '502200006',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_11.jpg'),
       ('Bartosz', 'Krawczyk', 'bartosz.krawczyk@example.com', '502200007',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_12.jpg'),
       ('Emilia', 'Piotrowska', 'emilia.piotrowska@example.com', '502200008',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_13.jpg'),
       ('Jakub', 'Grabowski', 'jakub.grabowski@example.com', '502200009',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_14.jpg'),
       ('Julia', 'Nowakowska', 'julia.nowakowska@example.com', '502200010',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_15.jpg'),
       ('Adam', 'Szczur', 'admin@example.com', '503300001',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'ADMIN', '/uploads/avatars/user_16.jpg')
ON CONFLICT (email) DO NOTHING;

-- 4. tutors
WITH u AS (SELECT id, email
           FROM users)
INSERT
INTO tutors (user_id, bio, hourly_rate, offers_online, offers_in_person)
VALUES ((SELECT id FROM u WHERE email = 'anna.kowalska@example.com'),
        'Nauczyciel matematyki z 10-letnim doświadczeniem. Specjalizuję się w przygotowaniu do matur i olimpiad.',
        80.00, TRUE, TRUE),
       ((SELECT id FROM u WHERE email = 'marek.nowak@example.com'),
        'Fizyk i programista. Prowadzę zajęcia z fizyki, algorytmów i programowania w Pythonie/C++.',
        70.00, TRUE, FALSE),
       ((SELECT id FROM u WHERE email = 'k.wisniewska@example.com'),
        'Absolwentka filologii angielskiej i germańskiej. Certyfikat CAE i Goethe-Zertifikat C2.',
        90.00, TRUE, TRUE),
       ((SELECT id FROM u WHERE email = 'piotr.zajac@example.com'),
        'Korepetytor chemii i biologii. Przygotowuję uczniów do matury rozszerzonej oraz olimpiad.',
        65.00, FALSE, TRUE),
       ((SELECT id FROM u WHERE email = 'monika.lewandowska@example.com'),
        'Pianistka i nauczycielka muzyki. Uczę gry na fortepianie i gitarze na każdym poziomie zaawansowania.',
        75.00, TRUE, TRUE)
ON CONFLICT (user_id) DO NOTHING;

-- 5. tutor_subjects
WITH t AS (SELECT user_id
           FROM tutors),
     s AS (SELECT id, subject_name
           FROM subjects),
     u AS (SELECT id, email
           FROM users)
INSERT
INTO tutor_subjects (tutor_id, subject_id, level_primary, level_high_school, level_university, level_exam_prep,
                     level_professional)
VALUES
    -- Anna
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'anna.kowalska@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Matematyka'), FALSE, TRUE, TRUE, TRUE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'anna.kowalska@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Statystyka'), FALSE, FALSE, TRUE, FALSE, TRUE),
    -- Marek
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'marek.nowak@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Fizyka'), FALSE, TRUE, TRUE, TRUE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'marek.nowak@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Programowanie'), FALSE, TRUE, TRUE, FALSE, TRUE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'marek.nowak@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Algorytmy'), FALSE, FALSE, TRUE, FALSE, TRUE),
    -- Katarzyna
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'k.wisniewska@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Język angielski'), TRUE, TRUE, TRUE, TRUE, TRUE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'k.wisniewska@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Język niemiecki'), FALSE, TRUE, TRUE, TRUE, FALSE),
    -- Piotr
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'piotr.zajac@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Chemia'), FALSE, TRUE, TRUE, TRUE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'piotr.zajac@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Biologia'), FALSE, TRUE, TRUE, TRUE, FALSE),
    -- Monika
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'monika.lewandowska@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Fortepian'), TRUE, TRUE, FALSE, FALSE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'monika.lewandowska@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Gitara'), TRUE, TRUE, FALSE, FALSE, FALSE)
ON CONFLICT (tutor_id, subject_id) DO NOTHING;

-- 6. tutor_availability_recurring
WITH t AS (SELECT user_id FROM tutors)
INSERT
INTO tutor_availability_recurring (tutor_id, day_of_week, time_from, time_to, date_to)
VALUES
    -- Anna: pon–pt 16:00–20:00, sob 10:00–14:00
    ((SELECT user_id FROM t WHERE user_id = 1), 0, '16:00', '20:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 1), 1, '16:00', '20:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 1), 2, '16:00', '20:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 1), 3, '16:00', '20:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 1), 4, '16:00', '20:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 1), 5, '10:00', '14:00', NULL),
    -- Marek: wt/czw 17:00–21:00, sob 09:00–13:00
    ((SELECT user_id FROM t WHERE user_id = 2), 1, '17:00', '21:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 2), 3, '17:00', '21:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 2), 5, '09:00', '13:00', NULL),
    -- Katarzyna: codziennie 08:00–12:00
    ((SELECT user_id FROM t WHERE user_id = 3), 0, '08:00', '12:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 3), 1, '08:00', '12:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 3), 2, '08:00', '12:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 3), 3, '08:00', '12:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 3), 4, '08:00', '12:00', NULL),
    -- Piotr: śr/pt 15:00–19:00
    ((SELECT user_id FROM t WHERE user_id = 4), 2, '15:00', '19:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 4), 4, '15:00', '19:00', NULL),
    -- Monika: wt/czw/sob 14:00–18:00
    ((SELECT user_id FROM t WHERE user_id = 5), 1, '14:00', '18:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 5), 3, '14:00', '18:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 5), 5, '10:00', '14:00', NULL)
ON CONFLICT DO NOTHING;

-- 7. tutor_availability_override
WITH t AS (SELECT user_id FROM tutors)
INSERT
INTO tutor_availability_override (tutor_id, override_date, time_from, time_to)
VALUES ((SELECT user_id FROM t WHERE user_id = 1), '2025-12-24', NULL, NULL),
       ((SELECT user_id FROM t WHERE user_id = 2), '2025-11-10', '10:00', '14:00'),
       ((SELECT user_id FROM t WHERE user_id = 3), '2025-11-15', '10:00', '11:00'),
       ((SELECT user_id FROM t WHERE user_id = 4), '2025-12-31', NULL, NULL)
ON CONFLICT DO NOTHING;

-- 8. holidays
INSERT INTO holidays (holiday_date, description)
VALUES ('2026-01-01', 'Nowy Rok'),
       ('2026-01-06', 'Trzech Króli'),
       ('2026-04-05', 'Wielkanoc'),
       ('2026-04-06', 'Poniedziałek Wielkanocny'),
       ('2026-05-01', 'Święto Pracy'),
       ('2026-05-03', 'Święto Konstytucji 3 Maja'),
       ('2026-06-08', 'Zielone Świątki'),
       ('2026-06-18', 'Boże Ciało'),
       ('2026-08-15', 'Wniebowzięcie NMP'),
       ('2026-11-01', 'Wszystkich Świętych'),
       ('2026-11-11', 'Święto Niepodległości'),
       ('2026-12-25', 'Boże Narodzenie'),
       ('2026-12-26', 'Drugi dzień Bożego Narodzenia')
ON CONFLICT (holiday_date) DO NOTHING;

-- 9. lessons
WITH t AS (SELECT user_id FROM tutors),
     s AS (SELECT id FROM users),
     sub AS (SELECT id, subject_name FROM subjects)
INSERT
INTO lessons
(tutor_id, student_id, subject_id, lesson_date, time_from, time_to, format, lesson_status, tutor_notes, student_notes,
 amount, payment_status, payment_date)
VALUES
    -- zakończone / opłacone
    ((SELECT user_id FROM t WHERE user_id = 2), (SELECT id FROM s WHERE id = 9),
     (SELECT id FROM sub WHERE subject_name = 'Fizyka'),
     '2025-10-10', '18:00', '19:30', 'ONLINE', 'COMPLETED', 'Elektromagnetyzm - prawa Faradaya i Lenza.',
     'Bardzo przejrzyste wyjaśnienia.', 105.00, 'PAID', '2025-10-10 19:40:00'),

    ((SELECT user_id FROM t WHERE user_id = 3), (SELECT id FROM s WHERE id = 7),
     (SELECT id FROM sub WHERE subject_name = 'Język angielski'),
     '2025-10-12', '09:00', '10:00', 'IN_PERSON', 'COMPLETED', 'Rozmowy o podróżach i pracy.', NULL, 90.00, 'PAID',
     '2025-10-12 10:10:00'),

    ((SELECT user_id FROM t WHERE user_id = 4), (SELECT id FROM s WHERE id = 12),
     (SELECT id FROM sub WHERE subject_name = 'Biologia'),
     '2025-10-14', '15:00', '16:30', 'IN_PERSON', 'COMPLETED', 'Budowa DNA i RNA.', 'Teraz rozumiem lepiej genetykę!',
     65.00, 'PAID', '2025-10-14 16:35:00'),

    ((SELECT user_id FROM t WHERE user_id = 5), (SELECT id FROM s WHERE id = 13),
     (SELECT id FROM sub WHERE subject_name = 'Fortepian'),
     '2025-10-15', '14:00', '15:00', 'IN_PERSON', 'COMPLETED', 'Ćwiczenie sonaty Mozarta.', NULL, 75.00, 'PAID',
     '2025-10-15 15:10:00'),

    -- potwierdzone / płatność oczekująca
    ((SELECT user_id FROM t WHERE user_id = 1), (SELECT id FROM s WHERE id = 8),
     (SELECT id FROM sub WHERE subject_name = 'Matematyka'),
     '2025-11-02', '17:00', '18:30', 'ONLINE', 'CONFIRMED', 'Przygotowanie do matury próbnej.', NULL, 120.00, 'PENDING',
     NULL),

    ((SELECT user_id FROM t WHERE user_id = 3), (SELECT id FROM s WHERE id = 14),
     (SELECT id FROM sub WHERE subject_name = 'Język niemiecki'),
     '2025-11-03', '10:00', '11:30', 'ONLINE', 'CONFIRMED', 'Konwersacje na poziomie B2.', NULL, 90.00, 'PENDING',
     NULL),

    -- oczekujące (pending)
    ((SELECT user_id FROM t WHERE user_id = 2), (SELECT id FROM s WHERE id = 6),
     (SELECT id FROM sub WHERE subject_name = 'Algorytmy'),
     '2025-11-05', '18:00', '19:00', 'ONLINE', 'PENDING', 'Rozwiązywanie zadań z algorytmiki.', NULL, 70.00, 'PENDING',
     NULL),

    ((SELECT user_id FROM t WHERE user_id = 5), (SELECT id FROM s WHERE id = 15),
     (SELECT id FROM sub WHERE subject_name = 'Gitara'),
     '2025-11-06', '15:00', '16:00', 'IN_PERSON', 'PENDING', 'Ćwiczenie akordów i rytmiki.', NULL, 75.00, 'PENDING',
     NULL),

    -- anulowane
    ((SELECT user_id FROM t WHERE user_id = 1), (SELECT id FROM s WHERE id = 7),
     (SELECT id FROM sub WHERE subject_name = 'Matematyka'),
     '2025-11-07', '17:00', '18:00', 'ONLINE', 'CANCELLED', 'Uczeń odwołał lekcję.', NULL, 80.00, 'CANCELLED', NULL),

    ((SELECT user_id FROM t WHERE user_id = 4), (SELECT id FROM s WHERE id = 9),
     (SELECT id FROM sub WHERE subject_name = 'Chemia'),
     '2025-11-08', '16:00', '17:00', 'IN_PERSON', 'CANCELLED', NULL, 'Choroba - przepraszam.', 65.00, 'CANCELLED', NULL)
ON CONFLICT DO NOTHING;

-- 10. reviews
WITH t AS (SELECT user_id FROM tutors),
     s AS (SELECT id FROM users)
INSERT
INTO reviews (tutor_id, user_id, rating, comment)
VALUES ((SELECT user_id FROM t WHERE user_id = 1), 6, 5.0,
        'Anna świetnie tłumaczy, bardzo polecam! Dzięki niej zdałem maturę z matematyki.'),
       ((SELECT user_id FROM t WHERE user_id = 1), 7, 4.5,
        'Bardzo dobry korepetytor, zawsze przygotowany. Czasem tempo jest trochę za szybkie.'),
       ((SELECT user_id FROM t WHERE user_id = 1), 8, 5.0, 'Polecam z całego serca - cierpliwa i dokładna.'),
       ((SELECT user_id FROM t WHERE user_id = 2), 8, 4.0,
        'Marek dobrze wyjaśnia fizykę, ale na programowanie woli zajęcia grupowe.'),
       ((SELECT user_id FROM t WHERE user_id = 2), 13, 5.0, 'Najlepszy nauczyciel Pythona, jakiego miałem!'),
       ((SELECT user_id FROM t WHERE user_id = 3), 9, 5.0,
        'Katarzyna mówi po angielsku jak native speaker. Konwersacje są rewelacyjne.'),
       ((SELECT user_id FROM t WHERE user_id = 3), 6, 4.5,
        'Bardzo dobra z niemieckiego, materiał jest ciekawy i dobrze ustrukturyzowany.'),
       ((SELECT user_id FROM t WHERE user_id = 4), 10, 4.0,
        'Piotr zna się na chemii, ale woli zajęcia stacjonarne niż online.'),
       ((SELECT user_id FROM t WHERE user_id = 4), 14, 5.0,
        'Biologia z Piotrem to czysta przyjemność - dużo ciekawostek!'),
       ((SELECT user_id FROM t WHERE user_id = 5), 11, 5.0,
        'Monika jest fantastyczną nauczycielką fortepianu. Gram już pierwszy utwór Chopina!'),
       ((SELECT user_id FROM t WHERE user_id = 5), 15, 4.5,
        'Gitara z Moniką to super przygoda. Nauczyłem się już kilku akordów.')
ON CONFLICT DO NOTHING;
