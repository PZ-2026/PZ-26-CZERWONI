package pl.edu.ur.teachly.ui.review.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.ReviewRequest
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.data.repository.ReviewRepository

data class MyReviewsState(
    val reviews: List<ReviewResponse> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val editSuccess: Boolean = false,
)

class MyReviewsViewModel(
    private val reviewRepository: ReviewRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state = MutableStateFlow(MyReviewsState())
    val state: StateFlow<MyReviewsState> = _state.asStateFlow()

    fun loadMyReviews() {
        viewModelScope.launch {
            val studentId = tokenManager.userIdFlow.first() ?: return@launch
            _state.update { it.copy(isLoading = true, error = null) }
            reviewRepository.getStudentReviews(studentId).fold(
                onSuccess = { reviews ->
                    _state.update { it.copy(reviews = reviews, isLoading = false) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }

    fun updateReview(reviewId: Int, tutorId: Int, rating: Double, comment: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            reviewRepository.updateReview(reviewId, ReviewRequest(tutorId, rating, comment)).fold(
                onSuccess = {
                    _state.update { it.copy(isSubmitting = false, editSuccess = true) }
                    loadMyReviews()
                },
                onFailure = { e ->
                    _state.update { it.copy(isSubmitting = false, error = e.message) }
                },
            )
        }
    }

    fun clearEditSuccess() {
        _state.update { it.copy(editSuccess = false) }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, error = null) }
            reviewRepository.deleteReview(reviewId).fold(
                onSuccess = {
                    _state.update { it.copy(isDeleting = false) }
                    loadMyReviews()
                },
                onFailure = { e ->
                    _state.update { it.copy(isDeleting = false, error = e.message) }
                },
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
