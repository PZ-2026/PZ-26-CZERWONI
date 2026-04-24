package pl.edu.ur.teachly.data.model

data class LessonRequest(
    val tutorId: Int,
    val subjectId: Int,
    val lessonDate: String,
    val timeFrom: String,
    val timeTo: String,
    val format: LessonFormat,
    val lessonStatus: LessonStatus,
    val studentNotes: String?,
    val amount: Double
)

data class LessonStatusRequest(
    val lessonStatus: LessonStatus,
    val tutorNotes: String?
)

data class StudentNotesRequest(val studentNotes: String?)

data class TutorNotesRequest(val tutorNotes: String?)

data class PaymentStatusRequest(val paymentStatus: PaymentStatus)

data class AdminLessonUpdateRequest(
    val lessonDate: String,
    val timeFrom: String,
    val timeTo: String,
    val format: LessonFormat,
    val lessonStatus: LessonStatus,
    val paymentStatus: PaymentStatus,
    val amount: Double,
    val studentNotes: String?,
    val tutorNotes: String?
)

data class LessonResponse(
    val id: Int,
    val tutorId: Int,
    val tutorFirstName: String,
    val tutorLastName: String,
    val studentId: Int,
    val studentFirstName: String,
    val studentLastName: String,
    val subjectId: Int,
    val subjectName: String,
    val lessonDate: String,
    val timeFrom: String,
    val timeTo: String,
    val format: LessonFormat,
    val lessonStatus: LessonStatus,
    val tutorNotes: String?,
    val studentNotes: String?,
    val amount: Double,
    val paymentStatus: PaymentStatus,
    val createdAt: String,
    val updatedAt: String
)