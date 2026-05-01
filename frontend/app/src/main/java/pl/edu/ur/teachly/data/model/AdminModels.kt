package pl.edu.ur.teachly.data.model

data class AdminStatsResponse(
    val totalUsers: Long,
    val totalStudents: Long,
    val totalTutors: Long,
    val totalAdmins: Long,
    val totalLessons: Long,
    val pendingLessons: Long,
    val confirmedLessons: Long,
    val completedLessons: Long,
    val cancelledLessons: Long,
    val totalSubjects: Long,
    val totalCategories: Long,
    val totalHolidays: Long,
    val totalReviews: Long
)
