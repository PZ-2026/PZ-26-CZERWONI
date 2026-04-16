package pl.edu.ur.teachly.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val userRole: UserRole,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val tokenType: String,
    val role: String,
    val userId: Int
)
