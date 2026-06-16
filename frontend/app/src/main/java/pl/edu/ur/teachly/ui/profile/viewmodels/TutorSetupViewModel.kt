package pl.edu.ur.teachly.ui.profile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.SubjectResponse
import pl.edu.ur.teachly.data.model.TutorSelfProfileRequest
import pl.edu.ur.teachly.data.model.TutorSubjectRequest
import pl.edu.ur.teachly.data.model.TutorSubjectResponse
import pl.edu.ur.teachly.data.repository.SubjectRepository
import pl.edu.ur.teachly.data.repository.TutorRepository

const val TUTOR_MIN_HOURLY_RATE = 1.0

data class TutorSetupState(
    val bio: String = "",
    val hourlyRate: String = "",
    val offersOnline: Boolean = false,
    val offersInPerson: Boolean = false,
    val city: String = "",
    val currentSubjects: List<TutorSubjectResponse> = emptyList(),
    val availableSubjects: List<SubjectResponse> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
) {
    val parsedHourlyRate: Double? = hourlyRate.replace(',', '.').toDoubleOrNull()

    val isHourlyRateValid: Boolean =
        parsedHourlyRate != null && parsedHourlyRate >= TUTOR_MIN_HOURLY_RATE

    val hasLessonFormat: Boolean = offersOnline || offersInPerson

    val hasSubjects: Boolean = currentSubjects.isNotEmpty()

    /**
     * Miasto jest wymagane tylko gdy korepetytor oferuje zajęcia stacjonarne; nazwa musi mieć od 2
     * do 50 znaków.
     */
    val hasCityIfInPerson: Boolean = !offersInPerson || city.trim().length in 2..50

    val isFormValid: Boolean =
        isHourlyRateValid && hasLessonFormat && hasSubjects && hasCityIfInPerson
}

class TutorSetupViewModel(
    private val tutorRepository: TutorRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TutorSetupState())
    val state: StateFlow<TutorSetupState> = _state.asStateFlow()

    fun load(tutorId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val tutorDeferred = async { tutorRepository.getTutorById(tutorId) }
            val subjectsDeferred = async { tutorRepository.getTutorSubjects(tutorId) }
            val availableDeferred = async { subjectRepository.getAllSubjects() }

            val tutorResult = tutorDeferred.await()
            val subjectsResult = subjectsDeferred.await()
            val availableResult = availableDeferred.await()

            val tutor = tutorResult.getOrNull()
            val currentSubjects = subjectsResult.getOrDefault(emptyList())
            val availableSubjects = availableResult.getOrDefault(emptyList())

            _state.update {
                it.copy(
                    bio = tutor?.bio ?: "",
                    hourlyRate = tutor?.hourlyRate?.let { r -> if (r > 0) r.toString() else "" } ?: "",
                    offersOnline = tutor?.offersOnline ?: false,
                    offersInPerson = tutor?.offersInPerson ?: false,
                    city = tutor?.city ?: "",
                    currentSubjects = currentSubjects,
                    availableSubjects = availableSubjects,
                    isLoading = false,
                    error = tutorResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun onBioChange(value: String) = _state.update { it.copy(bio = value) }

    fun onHourlyRateChange(value: String) {
        val normalized = value.replace(',', '.')
        if (normalized.isEmpty() || normalized.matches(Regex("^\\d{0,4}(\\.\\d{0,2})?$"))) {
            _state.update { it.copy(hourlyRate = normalized) }
        }
    }

    fun onOffersOnlineChange(value: Boolean) = _state.update { it.copy(offersOnline = value) }
    fun onOffersInPersonChange(value: Boolean) = _state.update { it.copy(offersInPerson = value) }
    fun onCityChange(value: String) = _state.update { it.copy(city = value) }

    fun saveProfile() {
        viewModelScope.launch {
            val current = _state.value
            if (!current.isFormValid) {
                _state.update { it.copy(error = validationErrorMessage(current)) }
                return@launch
            }

            _state.update { it.copy(isSaving = true, error = null) }
            val request =
                TutorSelfProfileRequest(
                    bio = current.bio.ifBlank { null },
                    hourlyRate = current.parsedHourlyRate!!,
                    offersOnline = current.offersOnline,
                    offersInPerson = current.offersInPerson,
                    city = if (current.offersInPerson) current.city.trim().ifBlank { null } else null
                )
            tutorRepository.updateMyProfile(request).fold(
                onSuccess = { _state.update { it.copy(isSaving = false, isSaved = true) } },
                onFailure = { e -> _state.update { it.copy(isSaving = false, error = e.message) } }
            )
        }
    }

    fun addSubject(
        subjectId: Int,
        levelPrimary: Boolean,
        levelHighSchool: Boolean,
        levelUniversity: Boolean,
        levelExamPrep: Boolean,
        levelProfessional: Boolean
    ) {
        viewModelScope.launch {
            _state.update { it.copy(error = null) }
            val request =
                TutorSubjectRequest(
                    subjectId = subjectId,
                    levelPrimary = levelPrimary,
                    levelHighSchool = levelHighSchool,
                    levelUniversity = levelUniversity,
                    levelExamPrep = levelExamPrep,
                    levelProfessional = levelProfessional
                )
            tutorRepository.addMySubject(request).fold(
                onSuccess = { added ->
                    _state.update { it.copy(currentSubjects = it.currentSubjects + added) }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun removeSubject(tutorSubjectId: Int) {
        viewModelScope.launch {
            if (_state.value.currentSubjects.size <= 1) {
                _state.update {
                    it.copy(error = "Musi pozostać co najmniej jeden prowadzony przedmiot")
                }
                return@launch
            }
            tutorRepository.removeMySubject(tutorSubjectId).fold(
                onSuccess = {
                    _state.update {
                        it.copy(currentSubjects = it.currentSubjects.filter { s -> s.id != tutorSubjectId })
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }
    fun clearSaved() = _state.update { it.copy(isSaved = false) }

    private fun validationErrorMessage(state: TutorSetupState): String =
        when {
            !state.isHourlyRateValid -> {
                if (state.hourlyRate.isBlank() || state.parsedHourlyRate == null) {
                    "Podaj prawidłową stawkę godzinową"
                } else {
                    "Stawka musi wynosić co najmniej 1 PLN"
                }
            }
            !state.hasLessonFormat ->
                "Wybierz co najmniej jedną formę zajęć (online lub stacjonarnie)"
            !state.hasCityIfInPerson -> "Podaj miasto zajęć stacjonarnych (2–50 znaków)"
            !state.hasSubjects -> "Dodaj co najmniej jeden prowadzony przedmiot"
            else -> "Uzupełnij wymagane pola profilu"
        }
}
