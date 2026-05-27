package pl.edu.ur.teachly.ui.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.TutorRequest
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.data.repository.TutorRepository

data class AdminTutorsState(
    val tutors: List<TutorResponse> = emptyList(),
    val filteredTutors: List<TutorResponse> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminTutorsViewModel(
    private val tutorRepository: TutorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminTutorsState())
    val state: StateFlow<AdminTutorsState> = _state.asStateFlow()

    init {
        loadTutors()
    }

    fun loadTutors() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            tutorRepository.getAllTutors().fold(
                onSuccess = { tutors ->
                    _state.update { it.copy(tutors = tutors, filteredTutors = tutors, isLoading = false) }
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        val q = query.lowercase()
        val filtered = _state.value.tutors.filter { tutor ->
            q.isEmpty() ||
                    tutor.firstName.lowercase().contains(q) ||
                    tutor.lastName.lowercase().contains(q) ||
                    tutor.email.lowercase().contains(q)
        }
        _state.update { it.copy(filteredTutors = filtered) }
    }

    fun updateTutor(tutorId: Int, request: TutorRequest) {
        viewModelScope.launch {
            tutorRepository.adminUpdateTutor(tutorId, request).fold(
                onSuccess = { updated ->
                    _state.update { s ->
                        s.copy(
                            tutors = s.tutors.map { if (it.id == tutorId) updated else it },
                            successMessage = "Dane korepetytora zostały zaktualizowane"
                        )
                    }
                    onSearchChange(_state.value.searchQuery)
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
