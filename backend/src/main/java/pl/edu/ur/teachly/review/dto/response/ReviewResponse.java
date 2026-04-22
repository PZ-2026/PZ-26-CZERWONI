package pl.edu.ur.teachly.review.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewResponse(
        Integer id,

        // Tutor
        Integer tutorId,
        String tutorFirstName,
        String tutorLastName,

        // Student
        Integer studentId,
        String studentFirstName,
        String studentLastName,

        // Review
        BigDecimal rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
