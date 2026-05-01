package pl.edu.ur.teachly.ui.review.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.data.repository.ReviewRepository

data class AllReviewsState(
    val reviews: List<ReviewResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class AllReviewsViewModel(
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AllReviewsState())
    val state: StateFlow<AllReviewsState> = _state.asStateFlow()

    fun loadReviews(tutorId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            reviewRepository.getTutorReviews(tutorId).fold(
                onSuccess = { reviews ->
                    _state.update { it.copy(reviews = reviews, isLoading = false) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }
}
