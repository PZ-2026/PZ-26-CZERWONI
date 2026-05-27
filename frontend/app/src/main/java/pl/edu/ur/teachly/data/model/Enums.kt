package pl.edu.ur.teachly.data.model

enum class UserRole(val label: String) {
    STUDENT("Uczeń"),
    TUTOR("Korepetytor"),
    ADMIN("Administrator"),
}

enum class LessonStatus(val label: String) {
    PENDING("Oczekująca"),
    CONFIRMED("Potwierdzona"),
    COMPLETED("Zakończona"),
    CANCELLED("Anulowana"),
}

enum class LessonFormat(val label: String) {
    ONLINE("Online"),
    IN_PERSON("Stacjonarnie"),
}

enum class PaymentStatus(val label: String) {
    PENDING("Oczekująca"),
    PAID("Opłacona"),
    CANCELLED("Anulowana"),
}