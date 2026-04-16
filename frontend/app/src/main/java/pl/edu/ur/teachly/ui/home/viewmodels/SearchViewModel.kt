package pl.edu.ur.teachly.ui.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.repository.SubjectRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.components.Tutor
import pl.edu.ur.teachly.ui.components.toUiTutor

data class SearchUiState(
    val query: String = "",
    val activeSubject: String = "Wszystkie",
    val tutors: List<Tutor> = emptyList(),
    val subjects: List<String> = listOf("Wszystkie"),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class SearchViewModel(
    private val tutorRepository: TutorRepository,
    private val subjectRepository: SubjectRepository,
) : ViewModel() {

    private val _allTutors = MutableStateFlow<List<Tutor>>(emptyList())
    private val _query = MutableStateFlow("")
    private val _activeSubject = MutableStateFlow("Wszystkie")
    private val _subjects = MutableStateFlow<List<String>>(listOf("Wszystkie"))
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SearchUiState> = combine(
        combine(_query, _activeSubject) { q, s -> q to s },
        combine(_allTutors, _subjects) { t, s -> t to s },
        combine(_isLoading, _error) { l, e -> l to e },
    ) { (query, subject), (tutors, subjects), (isLoading, error) ->
        val filtered = tutors.filter { tutor ->
            (subject == "Wszystkie" || tutor.subjects.contains(subject)) &&
                    (query.isBlank() || tutor.name.contains(query, ignoreCase = true))
        }
        SearchUiState(
            query = query,
            activeSubject = subject,
            tutors = filtered,
            subjects = subjects,
            isLoading = isLoading,
            error = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState(),
    )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            tutorRepository.getAllTutors().fold(
                onSuccess = { tutors ->
                    try {
                        _allTutors.value = tutors.map { it.toUiTutor() }
                    } catch (e: Exception) {
                        _error.value = e.message
                    }
                },
                onFailure = { e -> _error.value = e.message },
            )

            subjectRepository.getAllSubjects().fold(
                onSuccess = { subjects ->
                    _subjects.value = listOf("Wszystkie") + subjects.map { it.subjectName }
                },
                onFailure = { },
            )

            _isLoading.value = false
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onSubjectSelect(newSubject: String) {
        _activeSubject.value = newSubject
    }

    fun clearQuery() {
        _query.value = ""
    }

    fun refresh() {
        loadData()
    }
}

