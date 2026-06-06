package pl.edu.ur.teachly.ui.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.SubjectResponse
import pl.edu.ur.teachly.data.model.TutorRequest
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.data.model.TutorSubjectRequest
import pl.edu.ur.teachly.data.model.TutorSubjectResponse
import pl.edu.ur.teachly.data.repository.SubjectRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.util.Debouncer

data class AdminTutorsState(
    val tutors: List<TutorResponse> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null,
    val editingTutor: TutorResponse? = null,
    val editSubjects: List<TutorSubjectResponse> = emptyList(),
    val availableSubjects: List<SubjectResponse> = emptyList(),
    val isEditLoading: Boolean = false
)

class AdminTutorsViewModel(
    private val tutorRepository: TutorRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminTutorsState())
    val state: StateFlow<AdminTutorsState> = _state.asStateFlow()
    private val searchDebouncer = Debouncer(viewModelScope)

    init {
        loadTutors()
    }

    fun loadTutors() {
        viewModelScope.launch {
            val current = _state.value
            _state.update { it.copy(isLoading = true, error = null) }
            val query = current.searchQuery.trim().takeIf { it.isNotBlank() }
            tutorRepository.getAllTutors(query).fold(
                onSuccess = { tutors ->
                    _state.update { it.copy(tutors = tutors, isLoading = false) }
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchDebouncer.submit { loadTutors() }
    }

    fun openEdit(tutor: TutorResponse) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    editingTutor = tutor,
                    editSubjects = emptyList(),
                    availableSubjects = emptyList(),
                    isEditLoading = true,
                    error = null
                )
            }
            val subjectsDeferred = async { tutorRepository.getTutorSubjects(tutor.id) }
            val availableDeferred = async { subjectRepository.getAllSubjects() }
            val subjectsResult = subjectsDeferred.await()
            val availableResult = availableDeferred.await()
            _state.update {
                it.copy(
                    editSubjects = subjectsResult.getOrDefault(emptyList()),
                    availableSubjects = availableResult.getOrDefault(emptyList()),
                    isEditLoading = false,
                    error = subjectsResult.exceptionOrNull()?.message
                        ?: availableResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun closeEdit() {
        _state.update {
            it.copy(
                editingTutor = null,
                editSubjects = emptyList(),
                availableSubjects = emptyList(),
                isEditLoading = false
            )
        }
    }

    fun updateTutor(tutorId: Int, request: TutorRequest) {
        viewModelScope.launch {
            tutorRepository.adminUpdateTutor(tutorId, request).fold(
                onSuccess = {
                    _state.update { it.copy(successMessage = "Dane korepetytora zostały zaktualizowane") }
                    closeEdit()
                    loadTutors()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
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
        val tutorId = _state.value.editingTutor?.id ?: return
        viewModelScope.launch {
            val request =
                TutorSubjectRequest(
                    subjectId = subjectId,
                    levelPrimary = levelPrimary,
                    levelHighSchool = levelHighSchool,
                    levelUniversity = levelUniversity,
                    levelExamPrep = levelExamPrep,
                    levelProfessional = levelProfessional
                )
            tutorRepository.adminAddTutorSubject(tutorId, request).fold(
                onSuccess = { added ->
                    _state.update { it.copy(editSubjects = it.editSubjects + added) }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun removeSubject(tutorSubjectId: Int) {
        val tutorId = _state.value.editingTutor?.id ?: return
        if (_state.value.editSubjects.size <= 1) {
            _state.update {
                it.copy(error = "Musi pozostać co najmniej jeden prowadzony przedmiot")
            }
            return
        }
        viewModelScope.launch {
            tutorRepository.adminRemoveTutorSubject(tutorId, tutorSubjectId).fold(
                onSuccess = {
                    _state.update {
                        it.copy(editSubjects = it.editSubjects.filter { s -> s.id != tutorSubjectId })
                    }
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
