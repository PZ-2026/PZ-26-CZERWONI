package pl.edu.ur.teachly.lesson.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.PaymentStatus;

/** Odpowiedź zawierająca pełne dane lekcji, w tym dane korepetytora, ucznia i przedmiotu. */
public record LessonResponse(
        Integer id,

        // Tutor
        Integer tutorId,
        String tutorFirstName,
        String tutorLastName,
        String tutorAvatarUrl,
        String tutorCity,

        // Student
        Integer studentId,
        String studentFirstName,
        String studentLastName,
        String studentAvatarUrl,

        // Subject
        Integer subjectId,
        String subjectName,

        // Lesson details
        LocalDate lessonDate,
        LocalTime timeFrom,
        LocalTime timeTo,
        LessonFormat format,
        LessonStatus lessonStatus,

        // Notes
        String tutorNotes,
        String studentNotes,

        // Payment
        BigDecimal amount,
        PaymentStatus paymentStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
