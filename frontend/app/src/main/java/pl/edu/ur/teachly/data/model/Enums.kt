package pl.edu.ur.teachly.data.model

enum class UserRole {
    STUDENT,
    TUTOR,
    ADMIN
}

enum class LessonStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}

enum class LessonFormat {
    ONLINE,
    IN_PERSON
}

enum class PaymentStatus {
    PENDING,
    PAID,
    CANCELLED
}