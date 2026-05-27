package pl.edu.ur.teachly.ui.models

data class TutorStats(
    val totalLessons: Int = 0,
    val completedLessons: Int = 0,
    val reviewsCount: Int = 0,
    val avgRating: Double = 0.0,
    val totalEarnings: Double = 0.0,
)