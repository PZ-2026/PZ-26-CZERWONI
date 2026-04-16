package pl.edu.ur.teachly.ui.profile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.ReviewRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.components.Tutor
import pl.edu.ur.teachly.ui.components.toUiTutor

data class TutorStats(
    val totalLessons: Int = 0,
    val completedLessons: Int = 0,
    val reviewsCount: Int = 0,
    val totalEarnings: Double = 0.0,
)

data class TutorProfileState(
    val tutor: Tutor? = null,
    val stats: TutorStats = TutorStats(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class TutorProfileViewModel(
    private val tutorRepository: TutorRepository,
    private val lessonRepository: LessonRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TutorProfileState())
    val state: StateFlow<TutorProfileState> = _state.asStateFlow()

    fun loadProfile(tutorId: String) {
        val id = tutorId.toIntOrNull() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val tutorResponse = tutorRepository.getTutorById(id).getOrElse { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
                return@launch
            }

            val subjects = tutorRepository.getTutorSubjects(id)
                .getOrDefault(emptyList())
                .map { it.subjectName }

            _state.update { it.copy(tutor = tutorResponse.toUiTutor(subjects)) }

            var totalLessons = 0
            var completedLessons = 0
            var totalEarnings = 0.0
            lessonRepository.getTutorLessons(id).fold(
                onSuccess = { lessons ->
                    totalLessons = lessons.size
                    completedLessons = lessons.count { it.lessonStatus == LessonStatus.COMPLETED }
                    totalEarnings = lessons
                        .filter { it.lessonStatus == LessonStatus.COMPLETED }
                        .sumOf { it.amount }
                },
                onFailure = {},
            )

            var reviewsCount = 0
            reviewRepository.getTutorReviews(id).fold(
                onSuccess = { reviews -> reviewsCount = reviews.size },
                onFailure = {},
            )

            _state.update {
                it.copy(
                    stats = TutorStats(
                        totalLessons = totalLessons,
                        completedLessons = completedLessons,
                        reviewsCount = reviewsCount,
                        totalEarnings = totalEarnings,
                    ),
                    isLoading = false,
                )
            }
        }
    }
}
