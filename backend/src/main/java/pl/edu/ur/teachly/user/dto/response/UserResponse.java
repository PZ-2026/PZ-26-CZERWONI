package pl.edu.ur.teachly.user.dto.response;

import java.time.LocalDateTime;
import pl.edu.ur.teachly.common.enums.UserRole;

public record UserResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String avatarUrl,
        UserRole role,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
