package pl.edu.ur.teachly.ui.profile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.ReviewRequest
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.ReviewRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.models.TutorStats
import pl.edu.ur.teachly.ui.models.toUiTutor

data class TutorProfileState(
    val tutor: Tutor? = null,
    val email: String = "",
    val phoneNumber: String? = "",
    val stats: TutorStats = TutorStats(),
    val reviews: List<ReviewResponse> = emptyList(),
    val canReview: Boolean = false,
    val isSubmittingReview: Boolean = false,
    val reviewError: String? = null,
    val reviewSubmitSuccess: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class TutorProfileViewModel(
    private val tutorRepository: TutorRepository,
    private val lessonRepository: LessonRepository,
    private val reviewRepository: ReviewRepository,
    private val tokenManager: TokenManager,
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

            val currentUserId = tokenManager.userIdFlow.first()
            val currentRole = tokenManager.roleFlow.first()

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

            var reviews = emptyList<ReviewResponse>()
            var reviewsCount = 0
            var avgRating = 0.0
            reviewsResult.fold(
                onSuccess = { reviewList ->
                    reviews = reviewList
                    reviewsCount = reviewList.size
                    if (reviewList.isNotEmpty()) {
                        avgRating = reviewList.sumOf { it.rating } / reviewList.size
                    }
                },
                onFailure = {},
            )

            var canReview = false
            if (currentRole == "STUDENT" && currentUserId != null) {
                val alreadyReviewed = reviews.any { it.studentId == currentUserId }
                if (!alreadyReviewed) {
                    lessonRepository.getStudentLessons(currentUserId).fold(
                        onSuccess = { studentLessons ->
                            canReview = studentLessons.any {
                                it.tutorId == id && it.lessonStatus == LessonStatus.COMPLETED
                            }
                        },
                        onFailure = {},
                    )
                }
            }

            _state.update {
                it.copy(
                    tutor = tutorResponse.toUiTutor(
                        subjects = subjects,
                        rating = avgRating,
                        reviewCount = reviewsCount,
                        lessonCount = completedLessons,
                    ),
                    email = tutorResponse.email,
                    phoneNumber = tutorResponse.phoneNumber,
                    stats = TutorStats(
                        completedLessons = completedLessons,
                        reviewsCount = reviewsCount,
                        avgRating = avgRating,
                        totalEarnings = totalEarnings,
                    ),
                    reviews = reviews,
                    canReview = canReview,
                    isLoading = false,
                )
            }
        }
    }

    fun submitReview(tutorId: Int, rating: Double, comment: String?) {
        viewModelScope.launch {
            val studentId = tokenManager.userIdFlow.first() ?: return@launch
            _state.update { it.copy(isSubmittingReview = true, reviewError = null) }
            reviewRepository.addReview(studentId, ReviewRequest(tutorId, rating, comment)).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isSubmittingReview = false,
                            reviewSubmitSuccess = true
                        )
                    }
                    loadProfile(tutorId.toString())
                },
                onFailure = { e ->
                    _state.update { it.copy(isSubmittingReview = false, reviewError = e.message) }
                },
            )
        }
    }

    fun clearReviewSuccess() {
        _state.update { it.copy(reviewSubmitSuccess = false) }
    }

    fun clearReviewError() {
        _state.update { it.copy(reviewError = null) }
    }
}
