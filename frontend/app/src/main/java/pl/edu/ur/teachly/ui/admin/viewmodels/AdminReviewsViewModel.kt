package pl.edu.ur.teachly.ui.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.data.repository.AdminRepository

data class AdminReviewsState(
    val reviews: List<ReviewResponse> = emptyList(),
    val filteredReviews: List<ReviewResponse> = emptyList(),
    val searchQuery: String = "",
    val ratingFilter: Int? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminReviewsViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminReviewsState())
    val state: StateFlow<AdminReviewsState> = _state.asStateFlow()

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            adminRepository.getAllReviews().fold(
                onSuccess = { reviews ->
                    _state.update { it.copy(reviews = reviews, isLoading = false) }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onRatingFilterChange(rating: Int?) {
        _state.update { it.copy(ratingFilter = rating) }
        applyFilters()
    }

    private fun applyFilters() {
        val q = _state.value.searchQuery.lowercase()
        val ratingFilter = _state.value.ratingFilter
        val filtered = _state.value.reviews.filter { review ->
            val matchesSearch = q.isEmpty() ||
                    review.tutorFirstName.lowercase().contains(q) ||
                    review.tutorLastName.lowercase().contains(q) ||
                    review.studentFirstName.lowercase().contains(q) ||
                    review.studentLastName.lowercase().contains(q) ||
                    review.comment?.lowercase()?.contains(q) == true
            val matchesRating = ratingFilter == null || review.rating.toInt() == ratingFilter
            matchesSearch && matchesRating
        }
        _state.update { it.copy(filteredReviews = filtered) }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {
            adminRepository.deleteReview(reviewId).fold(
                onSuccess = {
                    _state.update { s ->
                        s.copy(
                            reviews = s.reviews.filter { it.id != reviewId },
                            successMessage = "Opinia została usunięta"
                        )
                    }
                    applyFilters()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
