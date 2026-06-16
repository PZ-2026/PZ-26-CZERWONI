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
import pl.edu.ur.teachly.ui.util.Debouncer

data class AdminReviewsState(
    val reviews: List<ReviewResponse> = emptyList(),
    val searchQuery: String = "",
    val ratingFilter: Int? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminReviewsViewModel(private val adminRepository: AdminRepository) : ViewModel() {

    private val _state = MutableStateFlow(AdminReviewsState())
    val state: StateFlow<AdminReviewsState> = _state.asStateFlow()
    private val searchDebouncer = Debouncer(viewModelScope)

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            val current = _state.value
            _state.update { it.copy(isLoading = true, error = null) }
            val query = current.searchQuery.trim().takeIf { it.isNotBlank() }
            adminRepository.getAllReviews(query, current.ratingFilter).fold(
                onSuccess = { reviews ->
                    _state.update { it.copy(reviews = reviews, isLoading = false) }
                },
                onFailure = { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchDebouncer.submit { loadReviews() }
    }

    fun onRatingFilterChange(rating: Int?) {
        _state.update { it.copy(ratingFilter = rating) }
        searchDebouncer.cancel()
        loadReviews()
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch {
            adminRepository.deleteReview(reviewId).fold(
                onSuccess = {
                    _state.update { it.copy(successMessage = "Opinia została usunięta") }
                    loadReviews()
                },
                onFailure = { e -> _state.update { it.copy(error = e.message) } }
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
