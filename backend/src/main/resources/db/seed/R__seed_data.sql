-- 1. subject_categories
INSERT INTO subject_categories (category_name)
VALUES ('Matematyka i nauki ścisłe'),
       ('Języki obce'),
       ('Nauki humanistyczne'),
       ('Informatyka'),
       ('Nauki przyrodnicze'),
       ('Muzyka i sztuka'),
       ('Przygotowanie do egzaminów'),
       ('Nauki społeczne'),
       ('Przedmioty zawodowe'),
       ('Edukacja wczesnoszkolna')
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
       ('Fortepian', (SELECT id FROM cat WHERE category_name = 'Muzyka i sztuka')),
       ('Egzamin ósmoklasisty', (SELECT id FROM cat WHERE category_name = 'Przygotowanie do egzaminów')),
       ('Matura podstawowa', (SELECT id FROM cat WHERE category_name = 'Przygotowanie do egzaminów')),
       ('Wiedza o społeczeństwie', (SELECT id FROM cat WHERE category_name = 'Nauki społeczne')),
       ('Podstawy przedsiębiorczości', (SELECT id FROM cat WHERE category_name = 'Nauki społeczne')),
       ('Plastyka', (SELECT id FROM cat WHERE category_name = 'Muzyka i sztuka')),
       ('Historia sztuki', (SELECT id FROM cat WHERE category_name = 'Muzyka i sztuka')),
       ('Rachunkowość', (SELECT id FROM cat WHERE category_name = 'Przedmioty zawodowe')),
       ('Logistyka', (SELECT id FROM cat WHERE category_name = 'Przedmioty zawodowe')),
       ('Czytanie i pisanie', (SELECT id FROM cat WHERE category_name = 'Edukacja wczesnoszkolna')),
       ('Matematyka dla dzieci', (SELECT id FROM cat WHERE category_name = 'Edukacja wczesnoszkolna'))
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
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'ADMIN', '/uploads/avatars/user_16.jpg'),
       ('Magdalena', 'Bąk', 'magdalena.bak@example.com', '601200301',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_17.jpg'),
       ('Dawid', 'Kwiatkowski', 'dawid.kwiatkowski@example.com', '601200302',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_18.jpg'),
       ('Igor', 'Kaczmarek', 'igor.kaczmarek@example.com', '602300401',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_19.jpg'),
       ('Oliwia', 'Zając', 'oliwia.zajac@example.com', '602300402',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_20.jpg'),
       ('Jan', 'Kowalski', 'jan.kowal@example.com', '701100101',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_21.jpg'),
       ('Zbigniew', 'Wodecki', 'zbigniew.wodecki@example.com', '701100102',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_22.jpg'),
       ('Patryk', 'Dudek', 'patryk.dudek@example.com', '702200201',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_23.jpg'),
       ('Zuzanna', 'Kowal', 'zuzanna.kowal@example.com', '702200202',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_24.jpg'),
       ('Kornelia', 'Mikołajczak', 'kornelia.mikolajczak@example.com', '801100101',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_25.jpg'),
       ('Ignacy', 'Wilk', 'ignacy.wilk@example.com', '802200201',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_26.jpg'),
       ('Matylda', 'Lis', 'matylda.lis@example.com', '802200202',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_27.jpg'),
       ('Krzysztof', 'Tomaszewski', 'krzysztof.tomaszewski@example.com', '901100101',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_28.jpg'),
       ('Weronika', 'Polańska', 'weronika.polanska@example.com', '901100102',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_29.jpg'),
       ('Szymon', 'Kowalczyk', 'szymon.kowalczyk@example.com', '901100103',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'TUTOR', '/uploads/avatars/user_30.jpg'),
       ('Alicja', 'Bielska', 'alicja.bielska@example.com', '902200201',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_31.jpg'),
       ('Dominik', 'Mazurek', 'dominik.mazurek@example.com', '902200202',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_32.jpg'),
       ('Klaudia', 'Jabłońska', 'klaudia.jablonska@example.com', '902200203',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_33.jpg'),
       ('Olaf', 'Stępień', 'olaf.stepien@example.com', '902200204',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_34.jpg'),
       ('Nina', 'Szewczyk', 'nina.szewczyk@example.com', '902200205',
        '$2a$12$rKqh3gMWzdPTHj5bPCMOa.n1tnVriPkHW7i.WuXi5vfazkUX2AZoy', 'STUDENT', '/uploads/avatars/user_35.jpg')
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
        75.00, TRUE, TRUE),
       ((SELECT id FROM u WHERE email = 'magdalena.bak@example.com'),
        'Doświadczona nauczycielka i egzaminatorka OKE. Skutecznie przygotowuję do egzaminu ósmoklasisty oraz matury.',
        120.00, TRUE, TRUE),
       ((SELECT id FROM u WHERE email = 'dawid.kwiatkowski@example.com'),
        'Nauczyciel licealny. Prowadzę zajęcia wyrównawcze oraz poszerzające wiedzę z WOS-u i Podstaw Przedsiębiorczości.',
        150.00, TRUE, FALSE),
       ((SELECT id FROM u WHERE email = 'jan.kowal@example.com'),
        'Nauczyciel plastyki i historii sztuki. Przygotowuję do matury z historii sztuki oraz pomagam przy tworzeniu teczki na ASP.',
        100.00, FALSE, TRUE),
       ((SELECT id FROM u WHERE email = 'zbigniew.wodecki@example.com'),
        'Nauczyciel w technikum ekonomicznym. Pomagam nadrobić zaległości z rachunkowości i logistyki.',
        130.00, TRUE, TRUE),
       ((SELECT id FROM u WHERE email = 'kornelia.mikolajczak@example.com'),
        'Nauczycielka edukacji wczesnoszkolnej. Pomagam najmłodszym w nauce czytania, pisania i podstaw matematyki.',
        90.00, TRUE, TRUE),
       ((SELECT id FROM u WHERE email = 'krzysztof.tomaszewski@example.com'),
        'Pomogę zrozumieć najtrudniejsze zagadnienia z fizyki i matematyki w prosty i logiczny sposób.',
        85.00, TRUE, TRUE),
       ((SELECT id FROM u WHERE email = 'weronika.polanska@example.com'),
        'Oferuję konwersacje i przygotowanie do egzaminów językowych. Posiadam wspaniały akcent i materiały.',
        95.00, TRUE, FALSE),
       ((SELECT id FROM u WHERE email = 'szymon.kowalczyk@example.com'),
        'Przekazuję wiedzę o świecie w fascynujący sposób, wplatam wiele ciekawostek geograficznych i biologicznych.',
        75.00, FALSE, TRUE)
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
     (SELECT id FROM s WHERE subject_name = 'Gitara'), TRUE, TRUE, FALSE, FALSE, FALSE),
    -- Magdalena
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'magdalena.bak@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Egzamin ósmoklasisty'), TRUE, FALSE, FALSE, TRUE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'magdalena.bak@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Matura podstawowa'), FALSE, TRUE, FALSE, TRUE, FALSE),
    -- Dawid
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'dawid.kwiatkowski@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Wiedza o społeczeństwie'), TRUE, TRUE, FALSE, TRUE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'dawid.kwiatkowski@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Podstawy przedsiębiorczości'), FALSE, TRUE, FALSE, FALSE, FALSE),
    -- Jan
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'jan.kowal@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Plastyka'), TRUE, TRUE, FALSE, FALSE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'jan.kowal@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Historia sztuki'), TRUE, TRUE, FALSE, TRUE, FALSE),
    -- Zbigniew
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'zbigniew.wodecki@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Rachunkowość'), FALSE, TRUE, TRUE, TRUE, TRUE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'zbigniew.wodecki@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Logistyka'), FALSE, TRUE, TRUE, TRUE, TRUE),
    -- Kornelia
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'kornelia.mikolajczak@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Czytanie i pisanie'), TRUE, FALSE, FALSE, FALSE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'kornelia.mikolajczak@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Matematyka dla dzieci'), TRUE, FALSE, FALSE, FALSE, FALSE),
    -- Krzysztof
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'krzysztof.tomaszewski@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Matematyka'), TRUE, TRUE, TRUE, TRUE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'krzysztof.tomaszewski@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Fizyka'), FALSE, TRUE, TRUE, TRUE, FALSE),
    -- Weronika
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'weronika.polanska@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Język angielski'), TRUE, TRUE, TRUE, TRUE, TRUE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'weronika.polanska@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Język francuski'), TRUE, TRUE, TRUE, TRUE, TRUE),
    -- Szymon
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'szymon.kowalczyk@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Geografia'), TRUE, TRUE, FALSE, TRUE, FALSE),
    ((SELECT user_id FROM t WHERE user_id = (SELECT id FROM u WHERE email = 'szymon.kowalczyk@example.com')),
     (SELECT id FROM s WHERE subject_name = 'Biologia'), TRUE, TRUE, FALSE, TRUE, FALSE)
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
    ((SELECT user_id FROM t WHERE user_id = 5), 5, '10:00', '14:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 17), 2, '16:00', '20:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 17), 4, '16:00', '20:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 18), 1, '10:00', '15:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 18), 3, '10:00', '15:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 21), 0, '09:00', '12:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 21), 1, '09:00', '12:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 22), 2, '14:00', '19:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 22), 5, '08:00', '14:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 25), 0, '15:00', '18:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 25), 2, '15:00', '18:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 25), 4, '15:00', '18:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 28), 1, '16:00', '21:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 28), 3, '16:00', '21:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 29), 0, '12:00', '16:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 29), 2, '12:00', '16:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 30), 1, '17:00', '20:00', NULL),
    ((SELECT user_id FROM t WHERE user_id = 30), 3, '17:00', '20:00', NULL)
ON CONFLICT DO NOTHING;

-- 7. tutor_availability_override
WITH t AS (SELECT user_id FROM tutors)
INSERT
INTO tutor_availability_override (tutor_id, override_date, time_from, time_to)
VALUES ((SELECT user_id FROM t WHERE user_id = 1), CURRENT_DATE + 5, NULL, NULL),
       ((SELECT user_id FROM t WHERE user_id = 2), CURRENT_DATE + 6, '10:00', '14:00'),
       ((SELECT user_id FROM t WHERE user_id = 3), CURRENT_DATE + 7, '10:00', '11:00'),
       ((SELECT user_id FROM t WHERE user_id = 4), CURRENT_DATE + 8, NULL, NULL)
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
     CURRENT_DATE - 3, '18:00', '19:30', 'ONLINE', 'COMPLETED', 'Elektromagnetyzm - prawa Faradaya i Lenza.',
     'Bardzo przejrzyste wyjaśnienia.', 105.00, 'PAID', (CURRENT_DATE - 3) + TIME '19:40:00'),

    ((SELECT user_id FROM t WHERE user_id = 3), (SELECT id FROM s WHERE id = 7),
     (SELECT id FROM sub WHERE subject_name = 'Język angielski'),
     CURRENT_DATE - 2, '09:00', '10:00', 'IN_PERSON', 'COMPLETED', 'Rozmowy o podróżach i pracy.', NULL, 90.00, 'PAID',
     (CURRENT_DATE - 2) + TIME '10:10:00'),

    ((SELECT user_id FROM t WHERE user_id = 4), (SELECT id FROM s WHERE id = 12),
     (SELECT id FROM sub WHERE subject_name = 'Biologia'),
     CURRENT_DATE - 1, '15:00', '16:30', 'IN_PERSON', 'COMPLETED', 'Budowa DNA i RNA.', 'Teraz rozumiem lepiej genetykę!',
     65.00, 'PAID', (CURRENT_DATE - 1) + TIME '16:35:00'),

    ((SELECT user_id FROM t WHERE user_id = 5), (SELECT id FROM s WHERE id = 13),
     (SELECT id FROM sub WHERE subject_name = 'Fortepian'),
     CURRENT_DATE, '14:00', '15:00', 'IN_PERSON', 'COMPLETED', 'Ćwiczenie sonaty Mozarta.', NULL, 75.00, 'PAID',
     CURRENT_DATE + TIME '15:10:00'),

    -- potwierdzone / płatność oczekująca
    ((SELECT user_id FROM t WHERE user_id = 1), (SELECT id FROM s WHERE id = 8),
     (SELECT id FROM sub WHERE subject_name = 'Matematyka'),
     CURRENT_DATE + 1, '17:00', '18:30', 'ONLINE', 'CONFIRMED', 'Przygotowanie do matury próbnej.', NULL, 120.00, 'PENDING',
     NULL),

    ((SELECT user_id FROM t WHERE user_id = 3), (SELECT id FROM s WHERE id = 14),
     (SELECT id FROM sub WHERE subject_name = 'Język niemiecki'),
     CURRENT_DATE + 2, '10:00', '11:30', 'ONLINE', 'CONFIRMED', 'Konwersacje na poziomie B2.', NULL, 90.00, 'PENDING',
     NULL),

    -- oczekujące (pending)
    ((SELECT user_id FROM t WHERE user_id = 2), (SELECT id FROM s WHERE id = 6),
     (SELECT id FROM sub WHERE subject_name = 'Algorytmy'),
     CURRENT_DATE + 3, '18:00', '19:00', 'ONLINE', 'PENDING', 'Rozwiązywanie zadań z algorytmiki.', NULL, 70.00, 'PENDING',
     NULL),

    ((SELECT user_id FROM t WHERE user_id = 5), (SELECT id FROM s WHERE id = 15),
     (SELECT id FROM sub WHERE subject_name = 'Gitara'),
     CURRENT_DATE + 4, '15:00', '16:00', 'IN_PERSON', 'PENDING', 'Ćwiczenie akordów i rytmiki.', NULL, 75.00, 'PENDING',
     NULL),

    -- anulowane
    ((SELECT user_id FROM t WHERE user_id = 1), (SELECT id FROM s WHERE id = 7),
     (SELECT id FROM sub WHERE subject_name = 'Matematyka'),
     CURRENT_DATE + 5, '17:00', '18:00', 'ONLINE', 'CANCELLED', 'Uczeń odwołał lekcję.', NULL, 80.00, 'CANCELLED', NULL),

    ((SELECT user_id FROM t WHERE user_id = 4), (SELECT id FROM s WHERE id = 9),
     (SELECT id FROM sub WHERE subject_name = 'Chemia'),
     CURRENT_DATE + 6, '16:00', '17:00', 'IN_PERSON', 'CANCELLED', NULL, 'Choroba - przepraszam.', 65.00, 'CANCELLED', NULL),

    ((SELECT user_id FROM t WHERE user_id = 21), (SELECT id FROM s WHERE id = 23),
     (SELECT id FROM sub WHERE subject_name = 'Historia sztuki'),
     CURRENT_DATE + 7, '10:00', '11:00', 'IN_PERSON', 'COMPLETED', 'Analiza obrazów impresjonistów.', 'Bardzo fajne i zwięzłe notatki, dziękuję!', 100.00, 'PAID', (CURRENT_DATE + 7) + TIME '11:30:00'),
    ((SELECT user_id FROM t WHERE user_id = 22), (SELECT id FROM s WHERE id = 24),
     (SELECT id FROM sub WHERE subject_name = 'Rachunkowość'),
     CURRENT_DATE + 8, '18:00', '19:00', 'ONLINE', 'CONFIRMED', 'Bilans i rachunek wyników na zaliczenie w technikum.', NULL, 130.00, 'PENDING', NULL),
    ((SELECT user_id FROM t WHERE user_id = 25), (SELECT id FROM s WHERE id = 26),
     (SELECT id FROM sub WHERE subject_name = 'Czytanie i pisanie'),
     CURRENT_DATE + 9, '15:00', '16:00', 'IN_PERSON', 'COMPLETED', 'Rozpoznawanie samogłosek i sylabizowanie.', 'Super podejście do dziecka!', 90.00, 'PAID', (CURRENT_DATE + 9) + TIME '16:30:00'),
    ((SELECT user_id FROM t WHERE user_id = 28), (SELECT id FROM s WHERE id = 31),
     (SELECT id FROM sub WHERE subject_name = 'Matematyka'),
     CURRENT_DATE + 10, '16:00', '17:30', 'ONLINE', 'COMPLETED', 'Funkcje kwadratowe.', 'Na maksa logicznie wytłumaczone.', 127.50, 'PAID', (CURRENT_DATE + 10) + TIME '17:40:00'),
    ((SELECT user_id FROM t WHERE user_id = 28), (SELECT id FROM s WHERE id = 32),
     (SELECT id FROM sub WHERE subject_name = 'Fizyka'),
     CURRENT_DATE + 11, '14:00', '15:00', 'IN_PERSON', 'CONFIRMED', 'Dynamika Newtona', NULL, 85.00, 'PENDING', NULL),
    ((SELECT user_id FROM t WHERE user_id = 29), (SELECT id FROM s WHERE id = 33),
     (SELECT id FROM sub WHERE subject_name = 'Język angielski'),
     CURRENT_DATE + 12, '17:00', '18:00', 'ONLINE', 'PENDING', 'Czasy przeszłe w storytellingu', NULL, 95.00, 'PENDING', NULL),
    ((SELECT user_id FROM t WHERE user_id = 29), (SELECT id FROM s WHERE id = 34),
     (SELECT id FROM sub WHERE subject_name = 'Język francuski'),
     CURRENT_DATE + 13, '16:00', '17:00', 'ONLINE', 'COMPLETED', 'Francuska wymowa - nosówki.', 'Bardzo fajne materiały.', 95.00, 'PAID', (CURRENT_DATE + 13) + TIME '17:10:00'),
    ((SELECT user_id FROM t WHERE user_id = 30), (SELECT id FROM s WHERE id = 35),
     (SELECT id FROM sub WHERE subject_name = 'Geografia'),
     CURRENT_DATE + 14, '15:00', '16:00', 'IN_PERSON', 'CANCELLED', NULL, 'Choroba.', 75.00, 'CANCELLED', NULL),
    ((SELECT user_id FROM t WHERE user_id = 30), (SELECT id FROM s WHERE id = 31),
     (SELECT id FROM sub WHERE subject_name = 'Biologia'),
     CURRENT_DATE + 15, '11:00', '12:00', 'IN_PERSON', 'COMPLETED', 'Cytologia - budowa komórki', 'Mnóstwo ciekawostek naukowych.', 75.00, 'PAID', (CURRENT_DATE + 15) + TIME '12:15:00'),
    ((SELECT user_id FROM t WHERE user_id = 1), (SELECT id FROM s WHERE id = 32),
     (SELECT id FROM sub WHERE subject_name = 'Matematyka'),
     CURRENT_DATE + 16, '16:00', '17:00', 'IN_PERSON', 'COMPLETED', 'Równania liniowe.', 'Jasno i przejrzyście', 80.00, 'PAID', (CURRENT_DATE + 16) + TIME '17:00:00'),
    ((SELECT user_id FROM t WHERE user_id = 2), (SELECT id FROM s WHERE id = 33),
     (SELECT id FROM sub WHERE subject_name = 'Programowanie'),
     CURRENT_DATE + 17, '18:00', '19:30', 'ONLINE', 'CONFIRMED', 'Wprowadzenie do Pythona.', NULL, 105.00, 'PENDING', NULL),
    ((SELECT user_id FROM t WHERE user_id = 17), (SELECT id FROM s WHERE id = 34),
     (SELECT id FROM sub WHERE subject_name = 'Egzamin ósmoklasisty'),
     CURRENT_DATE + 16, '16:00', '17:00', 'ONLINE', 'COMPLETED', 'Powtórzenie przed egzaminem.', 'Syn czuje się wreszcie przygotowany.', 120.00, 'PAID', (CURRENT_DATE + 16) + TIME '17:30:00'),
    ((SELECT user_id FROM t WHERE user_id = 28), (SELECT id FROM s WHERE id = 35),
     (SELECT id FROM sub WHERE subject_name = 'Fizyka'),
     CURRENT_DATE + 17, '16:00', '17:00', 'IN_PERSON', 'PENDING', 'Termodynamika.', NULL, 85.00, 'PENDING', NULL),
    ((SELECT user_id FROM t WHERE user_id = 29), (SELECT id FROM s WHERE id = 31),
     (SELECT id FROM sub WHERE subject_name = 'Język angielski'),
     CURRENT_DATE + 18, '18:00', '19:00', 'ONLINE', 'CANCELLED', NULL, 'Zmiana planów', 95.00, 'CANCELLED', NULL)
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
        'Gitara z Moniką to super przygoda. Nauczyłem się już kilku akordów.'),
       ((SELECT user_id FROM t WHERE user_id = 17), 19, 5.0,
        'Pani Magda to niesamowita nauczycielka, wreszcie nie stresuję się egzaminem ósmoklasisty.'),
       ((SELECT user_id FROM t WHERE user_id = 18), 20, 5.0,
        'Trudne tematy z WOS-u Pan Dawid tłumaczy na chłopski rozum, świetne przygotowanie do matury.'),
       ((SELECT user_id FROM t WHERE user_id = 21), 23, 5.0,
        'Zawsze miałem problem z zapamiętaniem epok, ale zajęcia z Panem Jankiem mega mi pomogły.'),
       ((SELECT user_id FROM t WHERE user_id = 22), 24, 5.0,
        'Rachunkowość z Panem Zbigniewem w końcu weszła mi do głowy. Sprawdzian zaliczony na 4+.'),
       ((SELECT user_id FROM t WHERE user_id = 25), 26, 5.0,
        'Pani Kornelia ma anielską cierpliwość na zajęciach. Synek wreszcie zaczął składać literki bez płaczu.'),
       ((SELECT user_id FROM t WHERE user_id = 25), 27, 4.5,
        'Dzięki korepetycjom córeczka pokochała dodawanie i odejmowanie za pomocą jabłuszek i klocków.'),
       ((SELECT user_id FROM t WHERE user_id = 28), 31, 5.0,
        'Pan Krzysztof uratował moją ocenę z matematyki. Nareszcie kapuję funkcje kwadratowe.'),
       ((SELECT user_id FROM t WHERE user_id = 28), 32, 4.0,
        'Bardzo fajnie i konkretnie. Czekamy na kolejną lekcję.'),
       ((SELECT user_id FROM t WHERE user_id = 29), 34, 5.0,
        'Dobre przygotowanie materiałów i świetna atmosfera na zajęciach, bardzo polecam Weronikę.'),
       ((SELECT user_id FROM t WHERE user_id = 30), 31, 4.5,
        'Prawdziwy pasjonat, nigdy nie przypuszczałam że biologia komórki może być tak ciekawa.'),
       ((SELECT user_id FROM t WHERE user_id = 1), 32, 5.0,
        'Jestem w szoku jak Pani Anna szybko wyłapuje luki w wiedzy z matematyki!'),
       ((SELECT user_id FROM t WHERE user_id = 17), 34, 5.0,
        'Z taką nauczycielką żaden egzamin nie jest straszny.')
ON CONFLICT DO NOTHING;
