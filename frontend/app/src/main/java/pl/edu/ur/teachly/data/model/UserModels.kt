package pl.edu.ur.teachly.data.model

data class UserUpdateRequest(
    val firstName: String,
    val lastName: String,
    val avatarUrl: String?
)

data class AdminUserUpdateRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val userRole: UserRole,
    val avatarUrl: String?
)

data class PasswordChangeRequest(
    val currentPassword: String,
    val newPassword: String
)

data class UserResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val avatarUrl: String?,
    val role: UserRole?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)