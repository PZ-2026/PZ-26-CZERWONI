package pl.edu.ur.teachly.ui.tutor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.repository.ReviewRepository
import pl.edu.ur.teachly.data.repository.TutorRepository
import pl.edu.ur.teachly.ui.components.Review
import pl.edu.ur.teachly.ui.components.Tutor
import pl.edu.ur.teachly.ui.components.toUiReview
import pl.edu.ur.teachly.ui.components.toUiTutor

data class TutorDetailUiState(
    val tutor: Tutor? = null,
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class TutorDetailViewModel(
    private val tutorRepository: TutorRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TutorDetailUiState())
    val state: StateFlow<TutorDetailUiState> = _state.asStateFlow()

    fun loadTutor(tutorId: String) {
        val id = tutorId.toIntOrNull() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Load tutor basic info
            val tutorResponse = tutorRepository.getTutorById(id).getOrElse { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
                return@launch
            }

            // Load subjects for this tutor
            val subjects = tutorRepository.getTutorSubjects(id)
                .getOrDefault(emptyList())
                .map { it.subjectName }

            try {
                _state.update { it.copy(tutor = tutorResponse.toUiTutor(subjects)) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                return@launch
            }

            // Load reviews and compute rating
            reviewRepository.getTutorReviews(id).fold(
                onSuccess = { reviews ->
                    try {
                        val avgRating = if (reviews.isEmpty()) 0.0
                        else reviews.sumOf { it.rating } / reviews.size
                        val uiReviews = reviews.map { r -> r.toUiReview() }
                        _state.update { s ->
                            s.copy(
                                tutor = s.tutor?.copy(
                                    rating = avgRating,
                                    reviewCount = reviews.size,
                                ),
                                reviews = uiReviews,
                            )
                        }
                    } catch (_: Exception) {
                    }
                },
                onFailure = { },
            )

            _state.update { it.copy(isLoading = false) }
        }
    }
}


