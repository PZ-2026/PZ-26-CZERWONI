package pl.edu.ur.teachly.ui.profile.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.edu.ur.teachly.ui.components.MOCK_TUTORS
import pl.edu.ur.teachly.ui.components.Tutor

data class TutorStats(
    val totalEarnings: Double = 0.0,
    val lessonsCount: Int = 0,
    val reviewsCount: Int = 0,
)

data class TutorProfileState(
    val tutor: Tutor? = null,
    val stats: TutorStats = TutorStats(),
)

class TutorProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(TutorProfileState())
    val state: StateFlow<TutorProfileState> = _state.asStateFlow()

    // TODO: Load from DB
    fun loadProfile(tutorId: String) {
        val tutor = MOCK_TUTORS.first { it.id.toString() == tutorId }
        _state.value = TutorProfileState(
            tutor = tutor,
            stats = TutorStats(
                totalEarnings = 1200.0,
                lessonsCount = 12,
                reviewsCount = 8,
            )
        )
    }
}