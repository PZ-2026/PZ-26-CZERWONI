package pl.edu.ur.teachly.ui.tutor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.ReviewRequest
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.ReviewRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.models.Review
import pl.edu.ur.teachly.ui.models.Tutor
import pl.edu.ur.teachly.ui.models.toUiReview
import pl.edu.ur.teachly.ui.models.toUiTutor

data class TutorDetailUiState(
    val tutor: Tutor? = null,
    val reviews: List<Review> = emptyList(),
    val currentStudentId: Int? = null,
    val canReview: Boolean = false,
    val isSubmittingReview: Boolean = false,
    val reviewError: String? = null,
    val reviewSubmitSuccess: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class TutorDetailViewModel(
    private val tutorRepository: TutorRepository,
    private val reviewRepository: ReviewRepository,
    private val lessonRepository: LessonRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state = MutableStateFlow(TutorDetailUiState())
    val state: StateFlow<TutorDetailUiState> = _state.asStateFlow()

    fun loadTutor(tutorId: String) {
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

            try {
                _state.update { it.copy(tutor = tutorResponse.toUiTutor(subjects)) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                return@launch
            }

            val currentUserId = tokenManager.userIdFlow.first()
            val currentRole = tokenManager.roleFlow.first()

            var uiReviews = emptyList<Review>()
            reviewRepository.getTutorReviews(id).fold(
                onSuccess = { reviews ->
                    try {
                        val avgRating = if (reviews.isEmpty()) 0.0
                        else reviews.sumOf { it.rating } / reviews.size
                        uiReviews = reviews.map { r -> r.toUiReview() }
                        _state.update { s ->
                            s.copy(
                                tutor = s.tutor?.copy(
                                    rating = avgRating,
                                    reviewCount = reviews.size,
                                ),
                                reviews = uiReviews,
                            )
                        }

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
                        _state.update { it.copy(canReview = canReview, currentStudentId = currentUserId) }
                    } catch (_: Exception) {
                    }
                },
                onFailure = { },
            )

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun updateReview(reviewId: Int, tutorId: Int, rating: Double, comment: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingReview = true, reviewError = null) }
            reviewRepository.updateReview(reviewId, ReviewRequest(tutorId, rating, comment)).fold(
                onSuccess = {
                    _state.update { it.copy(isSubmittingReview = false, reviewSubmitSuccess = true) }
                    loadTutor(tutorId.toString())
                },
                onFailure = { e ->
                    _state.update { it.copy(isSubmittingReview = false, reviewError = e.message) }
                },
            )
        }
    }

    fun submitReview(tutorId: Int, rating: Double, comment: String?) {
        viewModelScope.launch {
            val studentId = tokenManager.userIdFlow.first() ?: return@launch
            _state.update { it.copy(isSubmittingReview = true, reviewError = null) }
            reviewRepository.addReview(studentId, ReviewRequest(tutorId, rating, comment)).fold(
                onSuccess = {
                    _state.update { it.copy(isSubmittingReview = false, reviewSubmitSuccess = true) }
                    loadTutor(tutorId.toString())
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
