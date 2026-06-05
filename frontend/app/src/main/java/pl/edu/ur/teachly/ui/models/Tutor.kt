package pl.edu.ur.teachly.ui.models

data class Tutor(
    val id: Int,
    val name: String,
    val initials: String,
    val subjects: List<String>,
    val subjectsByLevel: List<SubjectsByLevelGroup> = emptyList(),
    val subjectsWithoutLevel: List<String> = emptyList(),
    val rating: Double,
    val reviewCount: Int,
    val pricePerHour: Int,
    val tags: List<String>,
    val isOnline: Boolean,
    val nearestSlots: List<String>,
    val bio: String = "",
    val lessonCount: Int = 0,
    val avatarUrl: String? = null
)
