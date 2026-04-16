package pl.edu.ur.teachly.ui.components

import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.data.model.TutorResponse
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

fun TutorResponse.toUiTutor(subjects: List<String> = emptyList()): Tutor = Tutor(
    id = id,
    name = "$firstName $lastName".trim(),
    initials = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}",
    subjects = subjects,
    rating = 0.0,
    reviewCount = 0,
    pricePerHour = hourlyRate.toInt(),
    tags = buildList {
        if (offersOnline) add("Online")
        if (offersInPerson) add("Stacjonarnie")
    },
    isOnline = offersOnline,
    nearestSlots = emptyList(),
    bio = bio ?: "",
)

fun LessonResponse.toScheduledClass(): ScheduledClass = ScheduledClass(
    id = id.toString(),
    subject = subjectName,
    tutorName = "$tutorFirstName $tutorLastName".trim(),
    studentName = "$studentFirstName $studentLastName".trim(),
    day = LocalDate.parse(lessonDate),
    time = timeFrom.take(5),
    durationMinutes = if (timeFrom.isNotEmpty() && timeTo.isNotEmpty()) {
        Duration.between(LocalTime.parse(timeFrom), LocalTime.parse(timeTo))
            .toMinutes().toInt().coerceAtLeast(0)
    } else 0,
    status = lessonStatus.toPolishLabel(),
)

fun LessonStatus.toPolishLabel(): String = when (this) {
    LessonStatus.PENDING -> "Oczekujące"
    LessonStatus.CONFIRMED -> "Zaplanowane"
    LessonStatus.COMPLETED -> "Zakończone"
    LessonStatus.CANCELLED -> "Anulowane"
}

fun ReviewResponse.toUiReview(): Review = Review(
    authorName = "$studentFirstName $studentLastName".trim(),
    text = comment ?: "",
    rating = rating.toInt().coerceIn(1, 5),
)
