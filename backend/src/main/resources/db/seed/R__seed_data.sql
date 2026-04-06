-- 1. subject_categories
INSERT INTO subject_categories (category_name) VALUES
    ('Matematyka i nauki ścisłe'),
    ('Języki obce'),
    ('Nauki humanistyczne'),
    ('Informatyka'),
    ('Nauki przyrodnicze'),
    ('Muzyka i sztuka');

-- 2. subjects
INSERT INTO subjects (subject_name, category_id) VALUES
    -- Matematyka i nauki ścisłe (1)
    ('Matematyka',          1),
    ('Fizyka',              1),
    ('Chemia',              1),
    ('Statystyka',          1),
    -- Języki obce (2)
    ('Język angielski',     2),
    ('Język niemiecki',     2),
    ('Język hiszpański',    2),
    ('Język francuski',     2),
    -- Nauki humanistyczne (3)
    ('Historia',            3),
    ('Język polski',        3),
    ('Filozofia',           3),
    -- Informatyka (4)
    ('Programowanie',       4),
    ('Bazy danych',         4),
    ('Algorytmy',           4),
    -- Nauki przyrodnicze (5)
    ('Biologia',            5),
    ('Geografia',           5),
    -- Muzyka i sztuka (6)
    ('Gitara',              6),
    ('Fortepian',           6);

-- 3. users  (5 korepetytorów + 10 uczniów + 1 admin)
--    password_hash to bcrypt("123")  – tylko do testów
INSERT INTO users (first_name, last_name, email, phone_number, password_hash, user_role, avatar_url) VALUES
    -- Korepetytorzy (id 1-5)
    ('Anna',      'Kowalska',    'anna.kowalska@example.com',    '501100001', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'tutor', '/uploads/avatars/user_1.jpg'),
    ('Marek',     'Nowak',       'marek.nowak@example.com',      '501100002', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'tutor', '/uploads/avatars/user_2.jpg'),
    ('Katarzyna', 'Wiśniewska',  'k.wisniewska@example.com',     '501100003', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'tutor', '/uploads/avatars/user_3.jpg'),
    ('Piotr',     'Zając',       'piotr.zajac@example.com',      '501100004', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'tutor', '/uploads/avatars/user_4.jpg'),
    ('Monika',    'Lewandowska', 'monika.lewandowska@example.com','501100005', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'tutor', '/uploads/avatars/user_5.jpg'),
    -- Uczniowie (id 6-15)
    ('Tomasz',    'Adamski',     'tomasz.adamski@example.com',   '502200001', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_6.jpg'),
    ('Zofia',     'Dąbrowska',   'zofia.dabrowska@example.com',  '502200002', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_7.jpg'),
    ('Michał',    'Szymański',   'michal.szymanski@example.com', '502200003', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_8.jpg'),
    ('Natalia',   'Wójcik',      'natalia.wojcik@example.com',   '502200004', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_9.jpg'),
    ('Kamil',     'Kowalczyk',   'kamil.kowalczyk@example.com',  '502200005', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_10.jpg'),
    ('Aleksandra','Mazur',       'ola.mazur@example.com',        '502200006', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_11.jpg'),
    ('Bartosz',   'Krawczyk',    'bartosz.krawczyk@example.com', '502200007', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_12.jpg'),
    ('Emilia',    'Piotrowska',  'emilia.piotrowska@example.com','502200008', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_13.jpg'),
    ('Jakub',     'Grabowski',   'jakub.grabowski@example.com',  '502200009', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_14.jpg'),
    ('Julia',     'Nowakowska',  'julia.nowakowska@example.com', '502200010', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'student', '/uploads/avatars/user_15.jpg'),
    -- Admin (id 16)
    ('Adam',     'Szczur',       'admin@example.com',            '503300001', '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'admin', '/uploads/avatars/user_16.jpg');

-- 4. tutors
INSERT INTO tutors (user_id, bio, hourly_rate, offers_online, offers_in_person) VALUES
    (1, 'Nauczyciel matematyki z 10-letnim doświadczeniem. Specjalizuję się w przygotowaniu do matur i olimpiad.', 80.00, TRUE,  TRUE),
    (2, 'Fizyk i programista. Prowadzę zajęcia z fizyki, algorytmów i programowania w Pythonie/C++.',             70.00, TRUE,  FALSE),
    (3, 'Absolwentka filologii angielskiej i germańskiej. Certyfikat CAE i Goethe-Zertifikat C2.',                90.00, TRUE,  TRUE),
    (4, 'Korepetytor chemii i biologii. Przygotowuję uczniów do matury rozszerzonej oraz olimpiad.',              65.00, FALSE, TRUE),
    (5, 'Pianistka i nauczycielka muzyki. Uczę gry na fortepianie i gitarze na każdym poziomie zaawansowania.',   75.00, TRUE,  TRUE);

-- 5. tutor_subjects
INSERT INTO tutor_subjects (tutor_id, subject_id, level_primary, level_high_school, level_university, level_exam_prep, level_professional) VALUES
    -- Anna:
    (1, 1,  FALSE, TRUE, TRUE,  TRUE,  FALSE),   -- Matematyka
    (1, 4,  FALSE, FALSE, TRUE, FALSE, TRUE),     -- Statystyka
    -- Marek:
    (2, 2,  FALSE, TRUE, TRUE,  TRUE,  FALSE),   -- Fizyka
    (2, 12, FALSE, TRUE, TRUE,  FALSE, TRUE),    -- Programowanie
    (2, 14, FALSE, FALSE, TRUE, FALSE, TRUE),    -- Algorytmy
    -- Katarzyna:
    (3, 5,  TRUE,  TRUE, TRUE,  TRUE,  TRUE),    -- Język angielski
    (3, 6,  FALSE, TRUE, TRUE,  TRUE,  FALSE),   -- Język niemiecki
    -- Piotr:
    (4, 3,  FALSE, TRUE, TRUE,  TRUE,  FALSE),   -- Chemia
    (4, 15, FALSE, TRUE, TRUE,  TRUE,  FALSE),   -- Biologia
    -- Monika:
    (5, 18, TRUE,  TRUE, FALSE, FALSE, FALSE),   -- Fortepian
    (5, 17, TRUE,  TRUE, FALSE, FALSE, FALSE);   -- Gitara

-- 6. tutor_availability_recurring
INSERT INTO tutor_availability_recurring (tutor_id, day_of_week, time_from, time_to, date_to) VALUES
    -- Anna: pon–pt 16:00–20:00, sobota 10:00–14:00
    (1, 0, '16:00', '20:00', NULL),
    (1, 1, '16:00', '20:00', NULL),
    (1, 2, '16:00', '20:00', NULL),
    (1, 3, '16:00', '20:00', NULL),
    (1, 4, '16:00', '20:00', NULL),
    (1, 5, '10:00', '14:00', NULL),
    -- Marek: wt/czw 17:00–21:00, sob 09:00–13:00
    (2, 1, '17:00', '21:00', NULL),
    (2, 3, '17:00', '21:00', NULL),
    (2, 5, '09:00', '13:00', NULL),
    -- Katarzyna: codziennie 08:00–12:00
    (3, 0, '08:00', '12:00', NULL),
    (3, 1, '08:00', '12:00', NULL),
    (3, 2, '08:00', '12:00', NULL),
    (3, 3, '08:00', '12:00', NULL),
    (3, 4, '08:00', '12:00', NULL),
    -- Piotr: śr/pt 15:00–19:00
    (4, 2, '15:00', '19:00', NULL),
    (4, 4, '15:00', '19:00', NULL),
    -- Monika: wt/czw/sob 14:00–18:00
    (5, 1, '14:00', '18:00', NULL),
    (5, 3, '14:00', '18:00', NULL),
    (5, 5, '10:00', '14:00', NULL);

-- 7. tutor_availability_override
INSERT INTO tutor_availability_override (tutor_id, override_date, time_from, time_to) VALUES
    -- Anna niedostępna cały dzień 2025-12-24
    (1, '2025-12-24', NULL, NULL),
    -- Marek dostępny wyjątkowo w niedzielę 2025-11-10
    (2, '2025-11-10', '10:00', '14:00'),
    -- Katarzyna skraca dostępność 2025-11-15
    (3, '2025-11-15', '10:00', '11:00'),
    -- Piotr niedostępny 2025-12-31
    (4, '2025-12-31', NULL, NULL);

-- 8. holidays
INSERT INTO holidays (holiday_date, description) VALUES
    ('2026-01-01', 'Nowy Rok'),
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
    ('2026-12-26', 'Drugi dzień Bożego Narodzenia');

-- 9. lessons
INSERT INTO lessons
    (tutor_id, student_id, subject_id, lesson_date, time_from, time_to,
     format, lesson_status, tutor_notes, student_notes, amount, payment_status, payment_date)
VALUES
    -- === Zakończone / opłacone ===
    (1, 6,  1,  '2025-10-01', '17:00', '18:00', 'online',     'completed', 'Przerabialiśmy funkcje kwadratowe.',         'Było super, teraz już rozumiem!', 80.00,  'paid', '2025-10-01 18:30:00'),
    (1, 7,  1,  '2025-10-03', '17:00', '19:00', 'online',     'completed', 'Przygotowanie do matury próbnej.',           NULL,                              160.00, 'paid', '2025-10-03 19:15:00'),
    (2, 8,  2,  '2025-10-05', '18:00', '19:00', 'online',     'completed', 'Kinematyka - ruch jednostajny.',             'Bardzo pomocna lekcja.',          70.00,  'paid', '2025-10-05 19:05:00'),
    (3, 9,  5,  '2025-10-07', '09:00', '10:00', 'in_person',  'completed', 'Konwersacje na poziomie B2.',                NULL,                              90.00,  'paid', '2025-10-07 10:20:00'),
    (4, 10, 3,  '2025-10-08', '16:00', '17:00', 'in_person',  'completed', 'Stechiometria reakcji chemicznych.',         'Chemia jest ciekawa!',            65.00,  'paid', '2025-10-08 17:10:00'),
    (5, 11, 18, '2025-10-10', '14:00', '15:00', 'in_person',  'completed', 'Ćwiczenia skal i gamm.',                     NULL,                              75.00,  'paid', '2025-10-10 15:05:00'),
    (1, 12, 4,  '2025-10-12', '16:00', '17:30', 'online',     'completed', 'Analiza regresji liniowej.',                 NULL,                              120.00, 'paid', '2025-10-12 18:00:00'),
    (2, 13, 12, '2025-10-14', '17:00', '18:00', 'online',     'completed', 'Wprowadzenie do Pythona - listy i słowniki.','Chcę więcej!',                    70.00,  'paid', '2025-10-14 18:20:00'),
    (3, 6,  6,  '2025-10-15', '09:00', '10:00', 'online',     'completed', 'Grammatik - Konjunktiv II.',                 NULL,                              90.00,  'paid', '2025-10-15 10:30:00'),
    (4, 14, 15, '2025-10-17', '16:00', '17:00', 'in_person',  'completed', 'Genetyka - prawa Mendla.',                   'Świetnie wytłumaczone.',          65.00,  'paid', '2025-10-17 17:15:00'),

    -- === Potwierdzone / płatność oczekująca ===
    (1, 7,  1,  '2025-11-03', '17:00', '18:00', 'online',     'confirmed', NULL, NULL, 80.00,  'pending', NULL),
    (2, 8,  2,  '2025-11-04', '18:00', '19:30', 'online',     'confirmed', NULL, NULL, 105.00, 'pending', NULL),
    (3, 9,  5,  '2025-11-05', '09:00', '10:00', 'in_person',  'confirmed', NULL, NULL, 90.00,  'pending', NULL),
    (4, 10, 3,  '2025-11-06', '15:00', '16:00', 'in_person',  'confirmed', NULL, NULL, 65.00,  'pending', NULL),
    (5, 15, 17, '2025-11-07', '14:00', '15:00', 'in_person',  'confirmed', NULL, NULL, 75.00,  'pending', NULL),

    -- === Oczekujące (pending) ===
    (1, 6,  1,  '2025-11-17', '17:00', '18:00', 'online',     'pending',   NULL, NULL, 80.00,  'pending', NULL),
    (2, 13, 14, '2025-11-18', '18:00', '19:00', 'online',     'pending',   NULL, NULL, 70.00,  'pending', NULL),
    (3, 11, 5,  '2025-11-19', '09:00', '10:30', 'online',     'pending',   NULL, NULL, 135.00, 'pending', NULL),
    (4, 7,  15, '2025-11-20', '16:00', '17:00', 'in_person',  'pending',   NULL, NULL, 65.00,  'pending', NULL),
    (5, 12, 18, '2025-11-21', '14:00', '15:00', 'in_person',  'pending',   NULL, NULL, 75.00,  'pending', NULL),

    -- === Anulowane ===
    (1, 8,  1,  '2025-10-20', '17:00', '18:00', 'online',     'cancelled', 'Uczeń odwołał lekcję.',        NULL, 80.00,  'cancelled', NULL),
    (3, 14, 6,  '2025-10-22', '09:00', '10:00', 'online',     'cancelled', NULL, 'Choroba - przepraszam.', 90.00,  'cancelled', NULL);

-- 10. reviews  (wystawiane po ukończonych lekcjach)
INSERT INTO reviews (tutor_id, user_id, rating, comment) VALUES
    (1, 6,  5.0, 'Anna świetnie tłumaczy, bardzo polecam! Dzięki niej zdałem maturę z matematyki.'),
    (1, 7,  4.5, 'Bardzo dobry korepetytor, zawsze przygotowany. Czasem tempo jest trochę za szybkie.'),
    (1, 8,  5.0, 'Polecam z całego serca - cierpliwa i dokładna.'),
    (2, 8,  4.0, 'Marek dobrze wyjaśnia fizykę, ale na programowanie woli zajęcia grupowe.'),
    (2, 13, 5.0, 'Najlepszy nauczyciel Pythona, jakiego miałem!'),
    (3, 9,  5.0, 'Katarzyna mówi po angielsku jak native speaker. Konwersacje są rewelacyjne.'),
    (3, 6,  4.5, 'Bardzo dobra z niemieckiego, materiał jest ciekawy i dobrze ustrukturyzowany.'),
    (4, 10, 4.0, 'Piotr zna się na chemii, ale woli zajęcia stacjonarne niż online.'),
    (4, 14, 5.0, 'Biologia z Piotrem to czysta przyjemność - dużo ciekawostek!'),
    (5, 11, 5.0, 'Monika jest fantastyczną nauczycielką fortepianu. Gram już pierwszy utwór Chopina!'),
    (5, 15, 4.5, 'Gitara z Moniką to super przygoda. Nauczyłem się już kilku akordów.');
