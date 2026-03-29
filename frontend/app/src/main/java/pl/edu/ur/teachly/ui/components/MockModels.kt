package pl.edu.ur.teachly.ui.components

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import kotlin.math.roundToInt

// Models
val SUBJECTS = listOf(
    "Wszystkie", "Matematyka", "Fizyka", "Angielski",
    "Chemia", "Historia", "Informatyka", "Biologia",
)

data class Tutor(
    val id: Int,
    val name: String,
    val initials: String,
    val subjects: List<String>,
    val rating: Double,
    val reviewCount: Int,
    val pricePerHour: Int,
    val tags: List<String>,
    val isOnline: Boolean,
    val nearestSlots: List<String>,
    val bio: String = "Doświadczony korepetytor z pasją do nauczania. Pomagam uczniom przygotować się do matury i egzaminów wstępnych. Stosuję indywidualne podejście dostosowane do tempa i stylu nauki każdego ucznia.",
    val lessonCount: Int = 0,
    val yearsExp: Int = 0,
    val responseTime: String = "<1h",
)

data class CalendarDay(val shortName: String, val dayNumber: String)

data class BookingResult(
    val tutor: Tutor,
    val day: CalendarDay,
    val timeSlot: String,
    val durationMinutes: Int,
) {
    val totalPrice: Int get() = (tutor.pricePerHour * durationMinutes / 60.0).roundToInt()
}

data class ScheduledClass(
    val id: String,
    val subject: String,
    val tutor: Tutor,
    val day: LocalDate,
    val time: String,
    val durationMinutes: Int,
    val status: String
)

data class Review(
    val authorName: String,
    val text: String,
    val rating: Int,
)

val AVATAR_COLORS: List<Pair<Color, Color>> = listOf(
    Color(0xFFDBEAFE) to Color(0xFF1D4ED8),
    Color(0xFFDCFCE7) to Color(0xFF15803D),
    Color(0xFFFCE7F3) to Color(0xFF9D174D),
    Color(0xFFFEF3C7) to Color(0xFF92400E),
    Color(0xFFF3E8FF) to Color(0xFF6B21A8),
    Color(0xFFFFEDD5) to Color(0xFF9A3412),
)

fun avatarColors(index: Int) = AVATAR_COLORS[index % AVATAR_COLORS.size]

// Mock data
val MOCK_TUTORS = listOf(
    Tutor(
        1,
        "Anna Kowalska",
        "AK",
        listOf("Matematyka", "Fizyka", "Informatyka"),
        4.9,
        128,
        120,
        listOf("Matura", "Podstawy"),
        true,
        listOf("Dziś", "Jutro", "Śr"),
        lessonCount = 342,
        yearsExp = 5
    ),
    Tutor(
        2,
        "Piotr Nowak",
        "PN",
        listOf("Fizyka"),
        4.8,
        94,
        110,
        listOf("Olimpiada", "Matura"),
        false,
        listOf("Jutro", "Czw", "Pt"),
        lessonCount = 201,
        yearsExp = 4
    ),
    Tutor(
        3,
        "Maria Wiśniewska",
        "MW",
        listOf("Angielski"),
        5.0,
        211,
        100,
        listOf("Matura", "Konwersacje"),
        true,
        listOf("Dziś", "Śr", "Czw"),
        lessonCount = 520,
        yearsExp = 8
    ),
    Tutor(
        4,
        "Tomasz Zieliński",
        "TZ",
        listOf("Chemia"),
        4.7,
        67,
        130,
        listOf("Matura", "Studia"),
        true,
        listOf("Wt", "Pt", "Sob"),
        lessonCount = 178,
        yearsExp = 3
    ),
    Tutor(
        5,
        "Katarzyna Lewandowska",
        "KL",
        listOf("Historia"),
        4.9,
        145,
        95,
        listOf("Matura", "Studia"),
        false,
        listOf("Dziś", "Wt", "Czw"),
        lessonCount = 290,
        yearsExp = 6
    ),
    Tutor(
        6,
        "Michał Dąbrowski",
        "MD",
        listOf("Informatyka"),
        4.8,
        83,
        140,
        listOf("Python", "Algorytmy"),
        true,
        listOf("Śr", "Czw", "Sob"),
        lessonCount = 155,
        yearsExp = 4
    ),
)

val MOCK_REVIEWS = listOf(
    Review("Karolina M.", "Świetne wytłumaczenie całkowania, w końcu rozumiem!", 5),
    Review("Bartek W.", "Polecam! Dostałem 5 na maturze dzięki tym lekcjom.", 5),
    Review("Zuzia K.", "Bardzo cierpliwa i pomocna. Widać że lubi uczyć.", 5),
)

val CALENDAR_DAYS = listOf(
    CalendarDay("Pon", "16"), CalendarDay("Wt", "17"), CalendarDay("Śr", "18"),
    CalendarDay("Czw", "19"), CalendarDay("Pt", "20"), CalendarDay("Sob", "21"),
    CalendarDay("Nd", "22"),
)

val ALL_TIME_SLOTS = listOf(
    "08:00", "09:00", "10:00", "11:00", "12:00",
    "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00",
)

val BOOKED_SLOTS = setOf("10:00", "13:00", "16:00")

val DURATION_OPTIONS = listOf(45, 60, 90)

val MOCK_SCHEDULE = mapOf(
    0 to listOf(
        ScheduledClass(
            "1",
            "Matematyka",
            MOCK_TUTORS[0],
            LocalDate.of(2026, 3, 30),
            "10:00",
            60,
            "Zaplanowane"
        ),
        ScheduledClass(
            "2",
            "Fizyka",
            MOCK_TUTORS[1],
            LocalDate.of(2026, 4, 3),
            "13:00",
            45,
            "Oczekujące"
        )
    ),
    1 to listOf(
        ScheduledClass(
            "3",
            "Angielski",
            MOCK_TUTORS[2],
            LocalDate.of(2026, 3, 31),
            "15:00",
            60,
            "Zaplanowane"
        )
    ),
    2 to emptyList(),
    3 to listOf(
        ScheduledClass(
            "4",
            "Chemia",
            MOCK_TUTORS[3],
            LocalDate.of(2026, 3, 27),
            "17:00",
            90,
            "Zaplanowane"
        )
    ),
    4 to listOf(
        ScheduledClass(
            "5",
            "Angielski",
            MOCK_TUTORS[2],
            LocalDate.of(2026, 3, 22),
            "15:00",
            60,
            "Zakończone"
        )
    ),
    5 to listOf(
        ScheduledClass(
            "6",
            "Chemia",
            MOCK_TUTORS[3],
            LocalDate.of(2026, 3, 24),
            "17:00",
            90,
            "Zakończone"
        )
    ),
)