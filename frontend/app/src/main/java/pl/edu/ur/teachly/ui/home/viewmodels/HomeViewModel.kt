package pl.edu.ur.teachly.ui.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.ReviewRequest
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.ReviewRepository
import pl.edu.ur.teachly.data.repository.UserRepository
import pl.edu.ur.teachly.ui.models.ScheduledClass
import pl.edu.ur.teachly.ui.models.toScheduledClass
import java.time.LocalDate

data class PendingReviewInfo(
    val tutorId: Int,
    val tutorFirstName: String,
    val tutorLastName: String,
    val subjectName: String,
)

data class HomeUiState(
    val userName: String = "",
    val userRole: UserRole = UserRole.STUDENT,
    val upcomingConfirmed: List<ScheduledClass> = emptyList(),
    val upcomingPending: List<ScheduledClass> = emptyList(),
    val confirmedExpanded: Boolean = true,
    val pendingExpanded: Boolean = true,
    val totalLessons: Int = 0,
    val pendingLessonsCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    // Pending reviews
    val pendingReviews: List<PendingReviewInfo> = emptyList(),
    val selectedPendingReview: PendingReviewInfo? = null,
    val isSubmittingPendingReview: Boolean = false,
    val pendingReviewError: String? = null,
    val pendingReviewSubmitted: Boolean = false,
)

class HomeViewModel(
    private val tokenManager: TokenManager,
    private val lessonRepository: LessonRepository,
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    // Keeps track of tutors explicitly dismissed in this session so the popup
    // doesn't reappear every time the user navigates back to the Home screen.
    private val dismissedTutorIds = mutableSetOf<Int>()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = HomeUiState(isLoading = true)

            val userId = tokenManager.userIdFlow.first() ?: run {
                _state.value = HomeUiState(isLoading = false)
                return@launch
            }
            val roleName = tokenManager.roleFlow.first() ?: "STUDENT"
            val role = try {
                UserRole.valueOf(roleName)
            } catch (e: Exception) {
                UserRole.STUDENT
            }

            userRepository.getUserById(userId).fold(
                onSuccess = { user -> _state.value = _state.value.copy(userName = user.firstName) },
                onFailure = {},
            )

            val lessonsResult = when (role) {
                UserRole.STUDENT -> lessonRepository.getStudentLessons(userId)
                UserRole.TUTOR -> lessonRepository.getTutorLessons(userId)
                UserRole.ADMIN -> lessonRepository.getStudentLessons(userId) // TODO: Handle admin
            }

            lessonsResult.fold(
                onSuccess = { lessons ->
                    try {
                        val today = LocalDate.now()
                        val upcoming = lessons
                            .filter { it.lessonStatus != LessonStatus.CANCELLED && it.lessonStatus != LessonStatus.COMPLETED }
                            .map { it.toScheduledClass() }
                            .filter { it.day >= today }
                            .sortedWith(compareBy({ it.day }, { it.time }))

                        val confirmed = upcoming.filter { it.status == LessonStatus.CONFIRMED }
                        val pending = upcoming.filter { it.status == LessonStatus.PENDING }

                        _state.value = _state.value.copy(
                            userRole = role,
                            upcomingConfirmed = confirmed,
                            upcomingPending = pending,
                            totalLessons = lessons.count { it.lessonStatus == LessonStatus.COMPLETED },
                            pendingLessonsCount = lessons.count { it.lessonStatus == LessonStatus.PENDING },
                            isLoading = false,
                            error = null,
                        )

                        // Load pending reviews only for students
                        if (role == UserRole.STUDENT) {
                            val completedLessons = lessons.filter { it.lessonStatus == LessonStatus.COMPLETED }
                            if (completedLessons.isNotEmpty()) {
                                reviewRepository.getStudentReviews(userId).fold(
                                    onSuccess = { reviews ->
                                        val reviewedTutorIds = reviews.map { it.tutorId }.toSet()
                                        val pendingReviews = completedLessons
                                            .filter { it.tutorId !in reviewedTutorIds }
                                            .filter { it.tutorId !in dismissedTutorIds }
                                            .distinctBy { it.tutorId }
                                            .map { lesson ->
                                                PendingReviewInfo(
                                                    tutorId = lesson.tutorId,
                                                    tutorFirstName = lesson.tutorFirstName,
                                                    tutorLastName = lesson.tutorLastName,
                                                    subjectName = lesson.subjectName,
                                                )
                                            }
                                        _state.update { it.copy(pendingReviews = pendingReviews) }
                                    },
                                    onFailure = { /* silently ignore — not a critical feature */ },
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(
                            userRole = role,
                            isLoading = false,
                            error = e.message,
                        )
                    }
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        userRole = role,
                        isLoading = false,
                        error = e.message,
                    )
                },
            )
        }
    }

    fun toggleConfirmed() = _state.update { it.copy(confirmedExpanded = !it.confirmedExpanded) }
    fun togglePending() = _state.update { it.copy(pendingExpanded = !it.pendingExpanded) }

    // ── Pending reviews ──────────────────────────────────────────────────────

    fun selectPendingReview(info: PendingReviewInfo) {
        _state.update { it.copy(selectedPendingReview = info, pendingReviewError = null) }
    }

    /** Back to the list without marking as permanently dismissed (multi-review flow). */
    fun dismissSelectedPendingReview() {
        _state.update { it.copy(selectedPendingReview = null, pendingReviewError = null) }
    }

    /** User explicitly skips — remember so the popup doesn't reappear this session. */
    fun dismissAllPendingReviews() {
        _state.value.pendingReviews.forEach { dismissedTutorIds.add(it.tutorId) }
        _state.value.selectedPendingReview?.let { dismissedTutorIds.add(it.tutorId) }
        _state.update {
            it.copy(
                pendingReviews = emptyList(),
                selectedPendingReview = null,
                pendingReviewError = null,
            )
        }
    }

    fun submitPendingReview(rating: Double, comment: String?) {
        val review = _state.value.selectedPendingReview ?: return
        viewModelScope.launch {
            val studentId = tokenManager.userIdFlow.first() ?: return@launch
            _state.update { it.copy(isSubmittingPendingReview = true, pendingReviewError = null) }
            reviewRepository.addReview(studentId, ReviewRequest(review.tutorId, rating, comment)).fold(
                onSuccess = {
                    val remaining = _state.value.pendingReviews.filter { it.tutorId != review.tutorId }
                    _state.update {
                        it.copy(
                            isSubmittingPendingReview = false,
                            selectedPendingReview = null,
                            pendingReviews = remaining,
                            pendingReviewError = null,
                            pendingReviewSubmitted = true,
                        )
                    }
                },
                onFailure = { e ->
                    _state.update { it.copy(isSubmittingPendingReview = false, pendingReviewError = e.message) }
                },
            )
        }
    }

    fun clearPendingReviewError() {
        _state.update { it.copy(pendingReviewError = null) }
    }

    fun clearPendingReviewSubmitted() {
        _state.update { it.copy(pendingReviewSubmitted = false) }
    }
}
