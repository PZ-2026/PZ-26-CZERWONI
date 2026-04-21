# PZ-26-CZERWONI

## Spis treści

<details>
<summary><strong>Rozwiń</strong></summary>

- [PZ-26-CZERWONI](#pz-26-czerwoni)
  - [Spis treści](#spis-treści)
- [Skład:](#skład)
- [Aplikacja *Teachly*](#aplikacja-teachly)
  - [1. Stack technologiczny](#1-stack-technologiczny)
  - [2. Architektura backendu](#2-architektura-backendu)
  - [Przegląd API](#przegląd-api)
  - [Struktura projektu](#struktura-projektu)
  - [3. Backend](#3-backend)
    - [Uruchomienie z Dockerem](#uruchomienie-z-dockerem)
    - [Uruchomienie lokalne (bez Dockera)](#uruchomienie-lokalne-bez-dockera)
  - [4. Połączenie frontendu z backendem](#4-połączenie-frontendu-z-backendem)

</details>

---

# Skład:
- **Adrian Raczek** - Lider
- Maciej Pintal
- Krystian Zygmunt
- Szymon Barwa
- Norbert Zdziarski

# Aplikacja *Teachly*

Mobilna platforma do korepetycji. Łączy uczniów szukających korepetytorów z nauczycielami oferującymi prywatne lekcje. 
Umożliwia przeglądanie profili, rezerwację lekcji w dostępnych terminach, zarządzanie harmonogramem oraz wystawianie opinii po zakończonych zajęciach.

## 1. Stack technologiczny

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.4-green?logo=springboot)
![Android](https://img.shields.io/badge/Android-12%2B-brightgreen?logo=android)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)

**Backend**
- Java 21, Spring Boot 4.0.4, Gradle
- PostgreSQL 18, Flyway (migracje schematu i dane seed)
- Spring Security + JWT
- MapStruct (mapowanie Entity <-> DTO), Lombok

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
| Mapper     | Konwersja Entity <-> DTO (MapStruct)       |

## Przegląd API

| Metoda | Endpoint                                 | Opis                        |
|-------|-------------------------------------------|-----------------------------|
| POST  | `/api/auth/register`                      | Rejestracja                 |
| POST  | `/api/auth/login`                         | Logowanie, zwraca token JWT |
| GET   | `/api/tutors`                             | Lista korepetytorów         |
| GET   | `/api/tutors/{id}`                        | Profil korepetytora         |
| GET   | `/api/tutors/{id}/subjects`               | Przedmioty korepetytora     |
| GET   | `/api/tutors/{id}/availability/timetable` | Dostępne terminy            |
| POST  | `/api/lessons/student/{id}`               | Rezerwacja lekcji           |
| GET   | `/api/lessons/student/{id}`               | Lekcje ucznia               |
| GET   | `/api/lessons/tutor/{id}`                 | Lekcje korepetytora         |
| PATCH | `/api/lessons/{id}/status`                | Zmiana statusu lekcji       |
| GET   | `/api/reviews/tutor/{id}`                 | Opinie o korepetytorze      |
| POST  | `/api/reviews/student/{id}`               | Dodanie opinii              |
| GET   | `/api/users/{id}`                         | Dane użytkownika            |
| PUT   | `/api/users/{id}`                         | Edycja profilu              |

## Struktura projektu

```
.
├── backend/
│   ├── src/main/java/pl/edu/ur/teachly/
│   │   ├── auth/          # Rejestracja, logowanie, JWT
│   │   ├── user/          # Użytkownicy
│   │   ├── tutor/         # Profile korepetytorów, dostępność
│   │   ├── lesson/        # Lekcje, rezerwacje
│   │   ├── review/        # Opinie
│   │   ├── subject/       # Przedmioty
│   │   ├── holiday/       # Dni wolne
│   │   └── common/        # Wyjątki, security, enumy
│   └── src/main/resources/
│       ├── db/
│       │   ├── migration/ # Migracje Flyway (schemat)
│       │   ├── seed/      # Dane testowe
│       └── application.properties
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

## 3. Backend

### Uruchomienie z Dockerem

>[!TIP] Zalecane rozwiązanie.

<details>

<summary><strong>Rozwiń</strong></summary>

---

**1. Przygotuj plik `.env`:**

Skopiuj plik `.env.example` i zmień nazwę na `.env`

Lub

```bash
cp .env.example .env
```

>[!CAUTION] Poniższych instrukcji nie należy wykonywać na `.env.example`. Ten plik jest wysyłany na repozytorium.

Otwórz `.env` i ustaw klucz JWT (wygeneruj np. w Powershellu lub za pomocą OpenSSL w terminalu):

Powershell:
```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { [byte](Get-Random -Max 256) }))
```

OpenSSL:
```powershell
openssl rand -base64 32
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

**1. Przygotuj plik application-dev.properties:**

W folderze `backend/src/main/resources/` skopiuj plik `application.properties` i zmień nazwę na `application-dev.properties`.

Otwórz `application-dev.properties` i kontynuuj za instrukcjami.

>[!CAUTION] Poniższych instrukcji nie należy wykonywać na `application.properties`. Ten plik jest wysyłany na repozytorium.

1. Usuń poniższe wiersze:
```
# IMPORTANT: application.properties is only an example file. Production properties file is application-dev.properties
# Delete spring.profiles.active in application-dev.properties
spring.profiles.active=dev
```
1. W poniższych wierszach uzupełnij hasło użytkownika bazy danych i ewentualnie zmień adres lub login:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/teachly
spring.datasource.username=postgres
spring.datasource.password=
```

1. Uzupełnij JWT secret key (Dla generacji, patrz w punkcie pierwszym rozdziału [Uruchomienie z Dockerem](#uruchomienie-z-dockerem)):
```
application.security.jwt.secret-key=
```

1. Uruchom przez IntelliJ lub Gradle:
```bash
cd backend
./gradlew bootRun
```

</details>

## 4. Połączenie frontendu z backendem

<details>

<summary><strong>Rozwiń</strong></summary>

---

1. Domyślnie frontend łączy się z adresem `http://10.0.2.2:8080/` (emulator Android). Jest jednak możliwość zmiany adresu połączenia.

>[!TIP] Działanie opcjonalne: W pliku `frontend/local.properties` ustaw adres backendu (jedna z opcji):

```properties
# Emulator Android (backend lokalny)
BASE_URL="http://10.0.2.2:8080/"

# Fizyczne urządzenie (backend lokalny) — podaj IP swojego komputera
BASE_URL="http://192.168.X.X:8080/"

# Serwer zdalny
BASE_URL="http://adres-serwera:8080/"
```

2. Uruchom przez Android Studio.

</details>
