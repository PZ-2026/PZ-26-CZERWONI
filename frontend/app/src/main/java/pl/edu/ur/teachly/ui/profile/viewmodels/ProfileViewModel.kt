package pl.edu.ur.teachly.ui.profile.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StudentProfile(
    val firstName: String = "Jan",
    val lastName: String = "Kowalski",
    val email: String = "jan@example.com",
    val level: String = "Szkoła średnia - Klasa maturalna",
    val subjects: List<String> = listOf("Matematyka", "Język angielski", "Fizyka"),
    val lessonsCount: Int = 12,
    val goalsCount: Int = 4
) {
    val fullName get() = "$firstName $lastName"
    val initials get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}"
    val subjectsText get() = subjects.joinToString(", ")
}

data class ProfileEditState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val level: String = "",
    val subjectsText: String = ""
)

class ProfileViewModel : ViewModel() {

    private val _profile = MutableStateFlow(StudentProfile())
    val profile: StateFlow<StudentProfile> = _profile.asStateFlow()

    private val _editState = MutableStateFlow(ProfileEditState())
    val editState: StateFlow<ProfileEditState> = _editState.asStateFlow()

    fun startEditing() {
        val p = _profile.value
        _editState.value = ProfileEditState(
            firstName = p.firstName,
            lastName = p.lastName,
            email = p.email,
            level = p.level,
            subjectsText = p.subjectsText
        )
    }

    fun onFirstNameChange(value: String) = _editState.update { it.copy(firstName = value) }
    fun onLastNameChange(value: String) = _editState.update { it.copy(lastName = value) }
    fun onEmailChange(value: String) = _editState.update { it.copy(email = value) }
    fun onLevelChange(value: String) = _editState.update { it.copy(level = value) }
    fun onSubjectsChange(value: String) = _editState.update { it.copy(subjectsText = value) }

    fun saveProfile() {
        val edit = _editState.value
        _profile.update {
            it.copy(
                firstName = edit.firstName.trim(),
                lastName = edit.lastName.trim(),
                email = edit.email.trim(),
                level = edit.level.trim(),
                subjects = edit.subjectsText
                    .split(",")
                    .map { s -> s.trim() }
                    .filter { s -> s.isNotEmpty() }
            )
        }
    }
}