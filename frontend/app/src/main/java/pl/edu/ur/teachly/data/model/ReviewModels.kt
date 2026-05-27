package pl.edu.ur.teachly.data.model

data class ReviewRequest(
    val tutorId: Int,
    val rating: Double,
    val comment: String?
)

data class ReviewResponse(
    val id: Int,
    val tutorId: Int,
    val tutorFirstName: String,
    val tutorLastName: String,
    val studentId: Int,
    val studentFirstName: String,
    val studentLastName: String,
    val rating: Double,
    val comment: String?,
    val createdAt: String,
    val updatedAt: String
)