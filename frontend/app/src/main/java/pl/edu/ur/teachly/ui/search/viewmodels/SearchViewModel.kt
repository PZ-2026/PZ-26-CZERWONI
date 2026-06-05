package pl.edu.ur.teachly.ui.search.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.repository.ReviewRepository
import pl.edu.ur.teachly.data.repository.SubjectRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.models.toUiTutor

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
    private val subjectRepository: SubjectRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val allTutorsFlow = MutableStateFlow<List<Tutor>>(emptyList())
    private val queryFlow = MutableStateFlow("")
    private val activeSubjectFlow = MutableStateFlow("Wszystkie")
    private val subjectsFlow = MutableStateFlow<List<String>>(listOf("Wszystkie"))
    private val isLoadingFlow = MutableStateFlow(true)
    private val errorFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SearchUiState> = combine(
        combine(queryFlow, activeSubjectFlow) { q, s -> q to s },
        combine(allTutorsFlow, subjectsFlow) { t, s -> t to s },
        combine(isLoadingFlow, errorFlow) { l, e -> l to e }
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
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            isLoadingFlow.value = true
            errorFlow.value = null

            tutorRepository.getAllTutors().fold(
                onSuccess = { tutors ->
                    try {
                        allTutorsFlow.value = coroutineScope {
                            tutors.map { tutor ->
                                async {
                                    val subjectsDeferred = async {
                                        tutorRepository.getTutorSubjects(tutor.id)
                                            .getOrDefault(emptyList())
                                    }
                                    val reviewsDeferred = async {
                                        reviewRepository.getTutorReviews(tutor.id)
                                            .getOrDefault(emptyList())
                                    }
                                    val tutorSubjects = subjectsDeferred.await()
                                    val reviews = reviewsDeferred.await()
                                    val avgRating = if (reviews.isEmpty()) {
                                        0.0
                                    } else {
                                        reviews.sumOf { it.rating } / reviews.size
                                    }
                                    tutor.toUiTutor(
                                        tutorSubjects = tutorSubjects,
                                        rating = (avgRating * 10).toLong() / 10.0,
                                        reviewCount = reviews.size
                                    )
                                }
                            }.awaitAll()
                        }
                    } catch (e: Exception) {
                        errorFlow.value = e.message
                    }
                },
                onFailure = { e -> errorFlow.value = e.message }
            )

            subjectRepository.getAllSubjects().fold(
                onSuccess = { subjects ->
                    subjectsFlow.value = listOf("Wszystkie") + subjects.map { it.subjectName }
                },
                onFailure = { }
            )

            isLoadingFlow.value = false
        }
    }

    fun onQueryChange(newQuery: String) {
        queryFlow.value = newQuery
    }

    fun onSubjectSelect(newSubject: String) {
        activeSubjectFlow.value = newSubject
    }

    fun clearQuery() {
        queryFlow.value = ""
    }

    fun refresh() {
        loadData()
    }
}
