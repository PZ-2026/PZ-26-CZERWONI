package pl.edu.ur.teachly.ui.search.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.repository.SubjectRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.models.toUiTutor
import pl.edu.ur.teachly.ui.util.Debouncer

data class SearchUiState(
    val query: String = "",
    val activeSubject: String = "Wszystkie",
    val tutors: List<Tutor> = emptyList(),
    val subjects: List<String> = listOf("Wszystkie"),
    val isLoading: Boolean = true,
    val error: String? = null
)

class SearchViewModel(
    private val tutorRepository: TutorRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _state.asStateFlow()
    private val searchDebouncer = Debouncer(viewModelScope)

    init {
        loadSubjects()
        searchTutors()
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            subjectRepository.getAllSubjects().fold(
                onSuccess = { subjects ->
                    _state.update {
                        it.copy(subjects = listOf("Wszystkie") + subjects.map { subject -> subject.subjectName })
                    }
                },
                onFailure = { }
            )
        }
    }

    private fun searchTutors() {
        viewModelScope.launch {
            val current = _state.value
            _state.update { it.copy(isLoading = true, error = null) }

            val subjectFilter =
                current.activeSubject.takeIf { it.isNotBlank() && it != "Wszystkie" }
            val queryFilter = current.query.trim().takeIf { it.isNotBlank() }

            tutorRepository.searchTutors(queryFilter, subjectFilter).fold(
                onSuccess = { results ->
                    _state.update {
                        it.copy(
                            tutors = results.map { result -> result.toUiTutor() },
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        searchDebouncer.submit { searchTutors() }
    }

    fun onSubjectSelect(newSubject: String) {
        _state.update { it.copy(activeSubject = newSubject) }
        searchDebouncer.cancel()
        searchTutors()
    }

    fun clearQuery() {
        _state.update { it.copy(query = "") }
        searchDebouncer.cancel()
        searchTutors()
    }

    fun refresh() {
        loadSubjects()
        searchDebouncer.cancel()
        searchTutors()
    }
}
