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

data class TutorSetupState(
    val bio: String = "",
    val hourlyRate: String = "",
    val offersOnline: Boolean = false,
    val offersInPerson: Boolean = false,
    val currentSubjects: List<TutorSubjectResponse> = emptyList(),
    val availableSubjects: List<SubjectResponse> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

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
                    currentSubjects = currentSubjects,
                    availableSubjects = availableSubjects,
                    isLoading = false,
                    error = tutorResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun onBioChange(value: String) = _state.update { it.copy(bio = value) }
    fun onHourlyRateChange(value: String) = _state.update { it.copy(hourlyRate = value) }
    fun onOffersOnlineChange(value: Boolean) = _state.update { it.copy(offersOnline = value) }
    fun onOffersInPersonChange(value: Boolean) = _state.update { it.copy(offersInPerson = value) }

    fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val request = TutorSelfProfileRequest(
                bio = _state.value.bio.ifBlank { null },
                hourlyRate = _state.value.hourlyRate.toDoubleOrNull() ?: 0.0,
                offersOnline = _state.value.offersOnline,
                offersInPerson = _state.value.offersInPerson
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
            val request = TutorSubjectRequest(
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
}
