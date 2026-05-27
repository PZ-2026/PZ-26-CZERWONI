package pl.edu.ur.teachly.data.model

data class SubjectRequest(
    val subjectName: String,
    val categoryId: Int
)

data class SubjectCategoryRequest(
    val categoryName: String
)

data class SubjectResponse(
    val id: Int,
    val subjectName: String,
    val categoryId: Int,
    val categoryName: String
)

data class SubjectCategoryResponse(
    val id: Int,
    val categoryName: String
)