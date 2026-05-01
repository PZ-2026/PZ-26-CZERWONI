package pl.edu.ur.teachly.tutor.dto.response;

import java.math.BigDecimal;

public record TutorResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String avatarUrl,
        String bio,
        BigDecimal hourlyRate,
        Boolean offersOnline,
        Boolean offersInPerson) {
}
