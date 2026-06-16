package pl.edu.ur.teachly.admin.dto.response;

/**
 * Odpowiedź ze zagregowanymi statystykami systemu dla panelu administratora.
 *
 * <p>Zawiera liczniki użytkowników (z podziałem na role), lekcji (z podziałem na statusy) oraz
 * łączne liczby przedmiotów, kategorii, dni wolnych i opinii.
 */
public record AdminStatsResponse(
        int totalUsers,
        int totalStudents,
        int totalTutors,
        int totalAdmins,
        int totalLessons,
        int pendingLessons,
        int confirmedLessons,
        int completedLessons,
        int cancelledLessons,
        int totalSubjects,
        int totalCategories,
        int totalHolidays,
        int totalReviews) {}
