package pl.edu.ur.teachly.ui.models

import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.data.model.TutorResponse
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

fun TutorResponse.toUiTutor(
    subjects: List<String> = emptyList(),
    rating: Double = 0.0,
    reviewCount: Int = 0,
    lessonCount: Int = 0,
): Tutor = Tutor(
    id = id,
    name = "$firstName $lastName".trim(),
    initials = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}",
    subjects = subjects,
    rating = rating,
    reviewCount = reviewCount,
    pricePerHour = hourlyRate.toInt(),
    tags = buildList {
        if (offersOnline) add("Online")
        if (offersInPerson) add("Stacjonarnie")
    },
    isOnline = offersOnline,
    nearestSlots = emptyList(),
    bio = bio ?: "",
    lessonCount = lessonCount,
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
    status = lessonStatus,
)

fun ReviewResponse.toUiReview(): Review = Review(
    authorName = "$studentFirstName $studentLastName".trim(),
    text = comment ?: "",
    rating = rating.toInt().coerceIn(1, 5),
)
