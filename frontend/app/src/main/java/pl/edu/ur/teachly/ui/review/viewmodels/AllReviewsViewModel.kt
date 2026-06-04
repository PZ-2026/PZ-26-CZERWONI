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

data class AllReviewsState(
    val reviews: List<ReviewResponse> = emptyList(),
    val currentStudentId: Int? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class AllReviewsViewModel(private val reviewRepository: ReviewRepository, private val tokenManager: TokenManager) :
    ViewModel() {

    private val _state = MutableStateFlow(AllReviewsState())
    val state: StateFlow<AllReviewsState> = _state.asStateFlow()

    fun loadReviews(tutorId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val currentStudentId = tokenManager.userIdFlow.first()
            reviewRepository.getTutorReviews(tutorId).fold(
                onSuccess = { reviews ->
                    _state.update {
                        it.copy(
                            reviews = reviews,
                            currentStudentId = currentStudentId,
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

    fun updateReview(reviewId: Int, tutorId: Int, rating: Double, comment: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            reviewRepository.updateReview(reviewId, ReviewRequest(tutorId, rating, comment)).fold(
                onSuccess = { updated ->
                    _state.update { s ->
                        s.copy(
                            reviews = s.reviews.map { if (it.id == reviewId) updated else it },
                            isSubmitting = false,
                            successMessage = "Opinia zaktualizowana"
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isSubmitting = false, error = e.message) }
                }
            )
        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            reviewRepository.deleteReview(reviewId).fold(
                onSuccess = {
                    _state.update { s ->
                        s.copy(
                            reviews = s.reviews.filter { it.id != reviewId },
                            isSubmitting = false,
                            successMessage = "Opinia usunięta"
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isSubmitting = false, error = e.message) }
                }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(successMessage = null, error = null) }
    }
}
