# PZ-26-CZERWONI

## Spis treści

<details>
<summary><strong>Rozwiń</strong></summary>

- [PZ-26-CZERWONI](#pz-26-czerwoni)
  - [Spis treści](#spis-treści)
- [Skład](#skład)
- [Aplikacja *Teachly*](#aplikacja-teachly)
  - [1. Stack technologiczny](#1-stack-technologiczny)
  - [2. Architektura backendu](#2-architektura-backendu)
  - [3. Przegląd API](#3-przegląd-api)
  - [4. Struktura projektu](#4-struktura-projektu)
  - [5. Testy](#5-testy)
  - [6. Javadoc](#6-javadoc)
  - [7. Backend — uruchomienie](#7-backend--uruchomienie)
    - [Uruchomienie z Dockerem](#uruchomienie-z-dockerem)
    - [Uruchomienie lokalne (bez Dockera)](#uruchomienie-lokalne-bez-dockera)
  - [8. Połączenie frontendu z backendem](#8-połączenie-frontendu-z-backendem)

</details>

---

# Skład
- **Adrian Raczek** — Lider
- Maciej Pintal
- Krystian Zygmunt
- Szymon Barwa
- Norbert Zdziarski

# Aplikacja *Teachly*

Mobilna platforma do korepetycji. Łączy uczniów szukających korepetytorów z nauczycielami oferującymi prywatne lekcje.
Umożliwia przeglądanie profili, rezerwację lekcji w dostępnych terminach, zarządzanie harmonogramem oraz wystawianie opinii po zakończonych zajęciach. Platforma posiada panel administracyjny do zarządzania użytkownikami, lekcjami i przedmiotami oraz moduł generowania raportów PDF.

## 1. Stack technologiczny

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.4-green?logo=springboot)
![Android](https://img.shields.io/badge/Android-12%2B-brightgreen?logo=android)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)

**Backend**
- Java 21, Spring Boot 4.0.4, Gradle
- PostgreSQL 18, Flyway (migracje schematu i dane seed)
- Spring Security + JWT (HMAC-SHA512)
- MapStruct (mapowanie Entity ↔ DTO), Lombok
- JUnit 5, Mockito (testy jednostkowe)

**Frontend**
- Android (min. API 31 — Android 12)
- Kotlin, Jetpack Compose, Material3
- Retrofit (komunikacja z API)
- Koin (dependency injection)
- DataStore (przechowywanie tokenu JWT)
- Jetpack Navigation

**Infrastruktura**
- Docker, docker-compose

## 2. Architektura backendu

<details>
<summary><strong>Rozwiń</strong></summary>

| Warstwa    | Odpowiedzialność                           |
|------------|--------------------------------------------|
| Controller | Przyjmowanie żądań HTTP, routing           |
| DTO        | Kształt danych wymienianych z klientem     |
| Service    | Logika biznesowa, walidacja                |
| Repository | Zapytania do bazy danych (Spring Data JPA) |
| Entity     | Reprezentacja tabel w bazie danych         |
| Mapper     | Konwersja Entity ↔ DTO (MapStruct)         |

Bezpieczeństwo opiera się na bezstanowej sesji JWT. Każde żądanie (poza `/api/auth/**` i `/uploads/**`) musi zawierać nagłówek `Authorization: Bearer <token>`. Uprawnienia są weryfikowane przez adnotacje `@PreAuthorize` na poziomie metod kontrolera.

</details>

## 3. Przegląd API

<details>
<summary><strong>Rozwiń</strong></summary>

### Uwierzytelnianie (`/api/auth`)
| Metoda | Endpoint              | Opis                        | Dostęp   |
|--------|-----------------------|-----------------------------|----------|
| POST   | `/api/auth/register`  | Rejestracja nowego konta    | Publiczny |
| POST   | `/api/auth/login`     | Logowanie, zwraca token JWT | Publiczny |

### Użytkownicy (`/api/users`)
| Metoda | Endpoint                  | Opis                              | Dostęp          |
|--------|---------------------------|-----------------------------------|-----------------|
| GET    | `/api/users`              | Lista użytkowników (z filtrowaniem) | ADMIN          |
| GET    | `/api/users/{id}`         | Dane użytkownika                  | ADMIN / właściciel |
| PUT    | `/api/users/{id}`         | Edycja profilu                    | ADMIN / właściciel |
| PUT    | `/api/users/{id}/admin`   | Edycja konta przez admina         | ADMIN           |
| PATCH  | `/api/users/{id}/activate`| Aktywacja konta                   | ADMIN           |
| DELETE | `/api/users/{id}`         | Blokada konta                     | ADMIN           |
| POST   | `/api/users/{id}/avatar`  | Przesłanie awatara                | ADMIN / właściciel |
| DELETE | `/api/users/{id}/avatar`  | Usunięcie awatara                 | ADMIN / właściciel |

### Korepetytorzy (`/api/tutors`)
| Metoda | Endpoint                                        | Opis                                  | Dostęp          |
|--------|-------------------------------------------------|---------------------------------------|-----------------|
| GET    | `/api/tutors`                                   | Lista aktywnych korepetytorów         | Publiczny       |
| GET    | `/api/tutors/search`                            | Wyszukiwanie z ocenami i przedmiotami (query params: `q`, `subject`, `city`) | Publiczny       |
| GET    | `/api/tutors/{id}`                              | Profil korepetytora                   | Publiczny       |
| GET    | `/api/tutors/{id}/subjects`                     | Przedmioty korepetytora               | Publiczny       |
| PUT    | `/api/tutors/me`                                | Edycja własnego profilu               | TUTOR           |
| POST   | `/api/tutors/me/subjects`                       | Dodanie przedmiotu                    | TUTOR           |
| DELETE | `/api/tutors/me/subjects/{id}`                  | Usunięcie przedmiotu                  | TUTOR           |
| PUT    | `/api/tutors/{id}/admin`                        | Edycja profilu przez admina           | ADMIN           |
| POST   | `/api/tutors/{tutorId}/admin/subjects`          | Dodanie przedmiotu przez admina       | ADMIN           |
| DELETE | `/api/tutors/{tutorId}/admin/subjects/{subjectId}` | Usunięcie przedmiotu przez admina  | ADMIN           |

### Dostępność korepetytora (`/api/tutors/{tutorId}/availability`)
| Metoda | Endpoint            | Opis                              | Dostęp          |
|--------|---------------------|-----------------------------------|-----------------|
| GET    | `/timetable`        | Wolne terminy w zakresie dat      | Publiczny       |
| GET    | `/recurring`        | Cykliczne sloty dostępności       | Publiczny       |
| POST   | `/recurring`        | Dodanie slotu cyklicznego         | ADMIN / właściciel |
| DELETE | `/recurring/{id}`   | Usunięcie slotu cyklicznego       | ADMIN / właściciel |
| GET    | `/override`         | Jednorazowe nadpisania dostępności | Publiczny      |
| POST   | `/override`         | Dodanie nadpisania                | ADMIN / właściciel |
| DELETE | `/override/{id}`    | Usunięcie nadpisania              | ADMIN / właściciel |

### Lekcje (`/api/lessons`)
| Metoda | Endpoint                          | Opis                          | Dostęp          |
|--------|-----------------------------------|-------------------------------|-----------------|
| GET    | `/api/lessons`                    | Wszystkie lekcje (filtrowanie)| ADMIN           |
| POST   | `/api/lessons/student/{id}`       | Rezerwacja lekcji             | STUDENT / ADMIN |
| GET    | `/api/lessons/student/{id}`       | Lekcje ucznia                 | właściciel / ADMIN |
| GET    | `/api/lessons/tutor/{id}`         | Lekcje korepetytora           | właściciel / ADMIN |
| GET    | `/api/lessons/{id}`               | Szczegóły lekcji              | uczestnik / ADMIN |
| PUT    | `/api/lessons/{id}/admin`         | Edycja lekcji przez admina    | ADMIN           |
| PATCH  | `/api/lessons/{id}/status`        | Zmiana statusu lekcji         | uczestnik / ADMIN |
| PATCH  | `/api/lessons/{id}/student-notes` | Notatki ucznia                | STUDENT / ADMIN |
| PATCH  | `/api/lessons/{id}/tutor-notes`   | Notatki korepetytora          | TUTOR / ADMIN   |
| PATCH  | `/api/lessons/{id}/payment`       | Zmiana statusu płatności      | ADMIN           |

### Opinie (`/api/reviews`)
| Metoda | Endpoint                       | Opis                          | Dostęp          |
|--------|--------------------------------|-------------------------------|-----------------|
| GET    | `/api/reviews/tutor/{id}`      | Opinie o korepetytorze        | Publiczny       |
| GET    | `/api/reviews/student/{id}`    | Opinie ucznia                 | właściciel / ADMIN |
| POST   | `/api/reviews/student/{id}`    | Dodanie opinii                | STUDENT         |
| PUT    | `/api/reviews/{id}`            | Edycja opinii                 | autor           |
| DELETE | `/api/reviews/{id}`            | Usunięcie opinii              | autor           |

### Przedmioty (`/api/subjects`)
| Metoda | Endpoint                        | Opis                          | Dostęp   |
|--------|---------------------------------|-------------------------------|----------|
| GET    | `/api/subjects`                 | Lista przedmiotów             | Publiczny |
| POST   | `/api/subjects`                 | Dodanie przedmiotu            | ADMIN    |
| PUT    | `/api/subjects/{id}`            | Edycja przedmiotu             | ADMIN    |
| DELETE | `/api/subjects/{id}`            | Usunięcie przedmiotu          | ADMIN    |
| GET    | `/api/subjects/categories`      | Lista kategorii               | Publiczny |
| POST   | `/api/subjects/categories`      | Dodanie kategorii             | ADMIN    |
| PUT    | `/api/subjects/categories/{id}` | Edycja kategorii              | ADMIN    |
| DELETE | `/api/subjects/categories/{id}` | Usunięcie kategorii           | ADMIN    |

### Dni wolne (`/api/holidays`)
| Metoda | Endpoint               | Opis                  | Dostęp   |
|--------|------------------------|-----------------------|----------|
| GET    | `/api/holidays`        | Lista dni wolnych     | Publiczny |
| POST   | `/api/holidays`        | Dodanie dnia wolnego  | ADMIN    |
| PUT    | `/api/holidays/{id}`   | Edycja dnia wolnego   | ADMIN    |
| DELETE | `/api/holidays/{id}`   | Usunięcie dnia wolnego| ADMIN    |

### Panel admina (`/api/admin`)
| Metoda | Endpoint               | Opis                          | Dostęp |
|--------|------------------------|-------------------------------|--------|
| GET    | `/api/admin/stats`     | Statystyki systemu            | ADMIN  |
| GET    | `/api/admin/reviews`   | Wszystkie opinie (filtrowanie)| ADMIN  |
| DELETE | `/api/admin/reviews/{id}` | Usunięcie opinii           | ADMIN  |

### Raporty (`/api/reports`)
| Metoda | Endpoint          | Opis                              | Dostęp        |
|--------|-------------------|-----------------------------------|---------------|
| GET    | `/api/reports/my` | Generowanie raportu PDF           | Zalogowany    |

Parametry raportu: `startDate`, `endDate`, `type` (LESSONS / REVENUE / EXPENSES / ANALYTICS / STUDENTS / USERS), `includeFields`.

</details>

## 4. Struktura projektu

<details>
<summary><strong>Rozwiń</strong></summary>

```
.
├── backend/
│   ├── src/main/java/pl/edu/ur/teachly/
│   │   ├── auth/          # Rejestracja, logowanie, JWT
│   │   ├── user/          # Użytkownicy
│   │   ├── tutor/         # Profile korepetytorów, dostępność, plan zajęć
│   │   ├── lesson/        # Lekcje, rezerwacje
│   │   ├── review/        # Opinie
│   │   ├── subject/       # Przedmioty i kategorie
│   │   ├── holiday/       # Dni wolne
│   │   ├── admin/         # Panel administracyjny, statystyki
│   │   ├── report/        # Generowanie raportów PDF
│   │   └── common/        # Security, wyjątki, enumy, konfiguracja
│   ├── src/main/resources/
│   │   ├── db/
│   │   │   ├── migration/ # Migracje Flyway (schemat)
│   │   │   └── seed/      # Dane testowe
│   │   └── application.properties
│   └── src/test/          # Testy jednostkowe
│
├── frontend/
│   ├── app/src/main/java/pl/edu/ur/teachly/
│   │   ├── data/          # Modele, API, repozytoria
│   │   ├── ui/            # Ekrany, ViewModele, komponenty
│   │   └── navigation/    # Nawigacja między ekranami
│   └── local.properties
├── docker-compose.yml
└── .env.example
```

</details>

## 5. Testy

<details>
<summary><strong>Rozwiń</strong></summary>

Projekt zawiera testy jednostkowe dla warstwy serwisów i kontrolerów.

**Uruchomienie testów:**
```bash
cd backend
./gradlew test
```

**Uruchomienie testów z raportem Checkstyle:**
```bash
./gradlew check
```

Raporty z testów generowane są w `backend/build/reports/tests/test/index.html`.

</details>

## 6. Javadoc

<details>
<summary><strong>Rozwiń</strong></summary>

Cały kod backendu jest udokumentowany komentarzami Javadoc.

**Generowanie dokumentacji HTML z IntelliJ IDEA:**

`Tools → Generate JavaDoc` → wybierz zakres *Whole project*, ustaw katalog wyjściowy i kliknij *Generate*.

**Generowanie z linii poleceń:**
```bash
cd backend
./gradlew javadoc
```

Dokumentacja generowana jest w `backend/build/docs/javadoc/index.html`.

</details>

## 7. Backend — uruchomienie

### Uruchomienie z Dockerem

> Zalecane rozwiązanie.

<details>
<summary><strong>Rozwiń</strong></summary>

---

**1. Przygotuj plik `.env`:**

Skopiuj plik `.env.example` i zmień nazwę na `.env`

Lub:

```bash
cp .env.example .env
```

> [!CAUTION] Poniższych instrukcji nie należy wykonywać na `.env.example`. Ten plik jest wysyłany na repozytorium.

Otwórz `.env` i ustaw klucz JWT (wygeneruj np. w Powershellu lub za pomocą OpenSSL w terminalu):

PowerShell:
```powershell
[Convert]::ToBase64String((1..64 | ForEach-Object { [byte](Get-Random -Max 256) }))
```

OpenSSL:
```bash
openssl rand -base64 64
```

Skopiuj wynik jako wartość `JWT_SECRET_KEY` w pliku `.env`.

**2. Uruchom:**

Pierwsze uruchomienie lub kompilacja po zmianie kodu backendu:
```bash
docker-compose up --build
```

Przy kolejnych uruchomieniach (bez zmian w kodzie):
```bash
docker-compose up
```

</details>

---

### Uruchomienie lokalne (bez Dockera)

<details>
<summary><strong>Rozwiń</strong></summary>

---

**Wymagania:** Java 21, PostgreSQL 18

**1. Przygotuj plik `application-dev.properties`:**

W folderze `backend/src/main/resources/` skopiuj plik `application.properties` i zmień nazwę na `application-dev.properties`.

Otwórz `application-dev.properties` i kontynuuj za instrukcjami.

> [!CAUTION] Poniższych instrukcji nie należy wykonywać na `application.properties`. Ten plik jest wysyłany na repozytorium.

1. Usuń poniższe wiersze:
```
# IMPORTANT: application.properties is only an example file. Production properties file is application-dev.properties
# Delete spring.profiles.active in application-dev.properties
spring.profiles.active=dev
```

2. W poniższych wierszach uzupełnij hasło użytkownika bazy danych i ewentualnie zmień adres lub login:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/teachly
spring.datasource.username=postgres
spring.datasource.password=
```

3. Uzupełnij JWT secret key (do generacji — patrz rozdział [Uruchomienie z Dockerem](#uruchomienie-z-dockerem)):
```
application.security.jwt.secret-key=
```

4. Uruchom przez IntelliJ lub Gradle:
```bash
cd backend
./gradlew bootRun
```

</details>

## 8. Połączenie frontendu z backendem

<details>
<summary><strong>Rozwiń</strong></summary>

---

Domyślnie frontend łączy się z adresem `http://10.0.2.2:8080/` (emulator Android). Istnieje możliwość zmiany adresu połączenia.

> W pliku `frontend/local.properties` ustaw adres backendu (jedna z opcji):

```properties
# Emulator Android (backend lokalny)
BASE_URL="http://10.0.2.2:8080/"

# Fizyczne urządzenie (backend lokalny) — podaj IP swojego komputera
BASE_URL="http://192.168.X.X:8080/"

# Serwer zdalny
BASE_URL="http://adres-serwera:8080/"
```

Uruchom przez Android Studio.

</details>
