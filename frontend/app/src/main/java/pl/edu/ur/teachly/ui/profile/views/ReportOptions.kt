package pl.edu.ur.teachly.ui.profile.views

import pl.edu.ur.teachly.data.model.UserRole

/**
 * Czyste funkcje pomocnicze definiujące dostępne typy raportów oraz pola do wyboru w zależności
 * od roli użytkownika i wybranego typu raportu. Wydzielone z [ReportDownloadSection], aby odchudzić
 * główny composable i ułatwić testowanie tej logiki.
 */

/** Zwraca listę par (klucz, etykieta) typów raportów dostępnych dla danej roli. */
internal fun reportTypesFor(role: UserRole): List<Pair<String, String>> = when (role) {
    UserRole.STUDENT -> listOf(
        "LESSONS" to "Historia lekcji",
        "EXPENSES" to "Podsumowanie wydatków",
        "ANALYTICS" to "Czas nauki i analiza"
    )

    UserRole.TUTOR -> listOf(
        "LESSONS" to "Historia zajęć",
        "REVENUE" to "Podsumowanie przychodów",
        "STUDENTS" to "Analiza uczniów"
    )

    UserRole.ADMIN -> listOf(
        "LESSONS" to "Wszystkie lekcje platformy",
        "REVENUE" to "Obrót finansowy platformy",
        "USERS" to "Analiza zarejestrowanych kont"
    )
}

/** Zwraca listę par (etykieta, klucz) pól możliwych do uwzględnienia w danym typie raportu. */
internal fun availableFieldsFor(role: UserRole, reportKey: String): List<Pair<String, String>> {
    val list = mutableListOf<Pair<String, String>>()
    when (reportKey) {
        "LESSONS" -> {
            list.add("Data" to "date")
            list.add("Czas" to "time")
            list.add("Przedmiot" to "subject")
            if (role == UserRole.STUDENT) {
                list.add("Cena" to "price")
            } else {
                list.add("Zarobki" to "price")
            }
            list.add("Statusy lekcji" to "status")
            if (role == UserRole.STUDENT || role == UserRole.ADMIN) {
                list.add("Dane korepetytora" to "tutor")
            }
            if (role == UserRole.TUTOR || role == UserRole.ADMIN) {
                list.add("Dane ucznia" to "student")
            }
        }

        "REVENUE" -> {
            list.add("Przedmiot" to "subject")
            list.add("Zarobki" to "price")
            list.add("Liczba lekcji" to "status")
            list.add("Wykresy i wizualizacje" to "charts")
        }

        "EXPENSES" -> {
            list.add("Przedmiot" to "subject")
            list.add("Kwota" to "price")
            list.add("Korepetytor" to "tutor")
            list.add("Data" to "date")
            list.add("Wykresy i wizualizacje" to "charts")
        }

        "ANALYTICS" -> {
            list.add("Korepetytor" to "tutor")
            list.add("Przedmiot" to "subject")
            list.add("Czas nauki" to "status")
            list.add("Wykresy i wizualizacje" to "charts")
        }

        "STUDENTS" -> {
            list.add("Dane ucznia" to "student")
            list.add("Przedmiot" to "subject")
            list.add("Przeprowadzone lekcje" to "status")
            list.add("Wykresy i wizualizacje" to "charts")
        }

        "USERS" -> {
            list.add("Tabela użytkowników" to "student")
            list.add("Wykresy i wizualizacje" to "charts")
        }
    }
    return list
}
