package pl.edu.ur.teachly.lesson.dto.response;

import pl.edu.ur.teachly.common.enums.LessonFormat;
import pl.edu.ur.teachly.common.enums.LessonStatus;
import pl.edu.ur.teachly.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record LessonResponse(
        Integer id,

        // Tutor
        Integer tutorId,
        String tutorFirstName,
        String tutorLastName,

        // Student
        Integer studentId,
        String studentFirstName,
        String studentLastName,

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
        LocalDateTime updatedAt
) {
}
