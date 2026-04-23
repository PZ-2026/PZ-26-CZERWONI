package pl.edu.ur.teachly.ui.models

data class Review(
    val id: Int,
    val studentId: Int,
    val authorName: String,
    val text: String,
    val rating: Int,
)
