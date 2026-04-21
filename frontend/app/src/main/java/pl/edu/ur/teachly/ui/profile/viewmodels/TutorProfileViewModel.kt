package pl.edu.ur.teachly.ui.profile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.ReviewRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.models.toUiTutor

data class TutorStats(
    val totalLessons: Int = 0,
    val completedLessons: Int = 0,
    val reviewsCount: Int = 0,
    val avgRating: Double = 0.0,
    val totalEarnings: Double = 0.0,
)

data class TutorProfileState(
    val tutor: Tutor? = null,
    val email: String = "",
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

            val subjectsDeferred = async {
                tutorRepository.getTutorSubjects(id)
                    .getOrDefault(emptyList())
                    .map { it.subjectName }
            }
            val lessonsDeferred = async { lessonRepository.getTutorLessons(id) }
            val reviewsDeferred = async { reviewRepository.getTutorReviews(id) }

            val subjects = subjectsDeferred.await()
            val lessonsResult = lessonsDeferred.await()
            val reviewsResult = reviewsDeferred.await()

            var completedLessons = 0
            var totalEarnings = 0.0
            lessonsResult.fold(
                onSuccess = { lessons ->
                    completedLessons = lessons.count { it.lessonStatus == LessonStatus.COMPLETED }
                    totalEarnings = lessons
                        .filter { it.lessonStatus == LessonStatus.COMPLETED }
                        .sumOf { it.amount }
                },
                onFailure = {},
            )

            var reviewsCount = 0
            var avgRating = 0.0
            reviewsResult.fold(
                onSuccess = { reviews ->
                    reviewsCount = reviews.size
                    if (reviews.isNotEmpty()) {
                        avgRating = reviews.sumOf { it.rating } / reviews.size
                    }
                },
                onFailure = {},
            )

            _state.update {
                it.copy(
                    tutor = tutorResponse.toUiTutor(
                        subjects = subjects,
                        rating = avgRating,
                        reviewCount = reviewsCount,
                        lessonCount = completedLessons,
                    ),
                    email = tutorResponse.email,
                    stats = TutorStats(
                        completedLessons = completedLessons,
                        reviewsCount = reviewsCount,
                        avgRating = avgRating,
                        totalEarnings = totalEarnings,
                    ),
                    isLoading = false,
                )
            }
        }
    }
}
