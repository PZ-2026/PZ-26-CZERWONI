package pl.edu.ur.teachly.admin.dto.response;

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
        int totalReviews) {
}
