package pl.edu.ur.teachly.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import pl.edu.ur.teachly.ui.components.MOCK_TUTORS
import pl.edu.ur.teachly.ui.components.Tutor

data class HomeUiState(
    val query: String = "",
    val activeSubject: String = "Wszystkie",
    val tutors: List<Tutor> = emptyList()
)

class HomeViewModel : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _activeSubject = MutableStateFlow("Wszystkie")

    val uiState: StateFlow<HomeUiState> = combine(
        _query, _activeSubject
    ) { query, subject ->
        val filtered = MOCK_TUTORS.filter { tutor ->
            (subject == "Wszystkie" || tutor.subject == subject) &&
                    (query.isBlank() || tutor.name.contains(query, ignoreCase = true) ||
                            tutor.subject.contains(query, ignoreCase = true))
        }
        HomeUiState(query = query, activeSubject = subject, tutors = filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(tutors = MOCK_TUTORS)
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onSubjectSelect(newSubject: String) {
        _activeSubject.value = newSubject
    }

    fun clearQuery() {
        _query.value = ""
    }
}
