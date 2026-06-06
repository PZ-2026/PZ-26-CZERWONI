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
import pl.edu.ur.teachly.ui.util.Debouncer

data class AdminTutorsState(
    val tutors: List<TutorResponse> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminTutorsViewModel(private val tutorRepository: TutorRepository) : ViewModel() {

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

    fun updateTutor(tutorId: Int, request: TutorRequest) {
        viewModelScope.launch {
            tutorRepository.adminUpdateTutor(tutorId, request).fold(
                onSuccess = {
                    _state.update { it.copy(successMessage = "Dane korepetytora zostały zaktualizowane") }
                    loadTutors()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
