package pl.edu.ur.teachly.ui.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.edu.ur.teachly.data.local.ReviewPreferencesManager
import pl.edu.ur.teachly.data.local.TokenManager
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.ReviewRequest
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.data.repository.LessonRepository
import pl.edu.ur.teachly.data.repository.ReviewRepository
import pl.edu.ur.teachly.data.repository.UserRepository
import pl.edu.ur.teachly.ui.models.ScheduledClass
import pl.edu.ur.teachly.ui.models.toScheduledClass

data class PendingReviewInfo(
    val tutorId: Int,
    val tutorFirstName: String,
    val tutorLastName: String,
    val subjectName: String,
    val tutorAvatarUrl: String?
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
    val pendingReviewSubmitted: Boolean = false
)

class HomeViewModel(
    private val tokenManager: TokenManager,
    private val lessonRepository: LessonRepository,
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val reviewPreferencesManager: ReviewPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)

            val userId = tokenManager.userIdFlow.first() ?: run {
                _uiState.value = HomeUiState(isLoading = false)
                return@launch
            }
            val roleName = tokenManager.roleFlow.first() ?: "STUDENT"
            val role = try {
                UserRole.valueOf(roleName)
            } catch (e: Exception) {
                UserRole.STUDENT
            }

            userRepository.getUserById(userId).fold(
                onSuccess = { user -> _uiState.value = _uiState.value.copy(userName = user.firstName) },
                onFailure = {}
            )

            val lessonsResult = when (role) {
                UserRole.STUDENT -> lessonRepository.getStudentLessons(userId)
                UserRole.TUTOR -> lessonRepository.getTutorLessons(userId)
                UserRole.ADMIN -> Result.success(emptyList())
            }

            lessonsResult.fold(
                onSuccess = { lessons ->
                    try {
                        val today = LocalDate.now()
                        val upcoming = lessons
                            .filter {
                                it.lessonStatus != LessonStatus.CANCELLED &&
                                    it.lessonStatus != LessonStatus.COMPLETED
                            }
                            .map { it.toScheduledClass() }
                            .filter { it.day >= today }
                            .sortedWith(compareBy({ it.day }, { it.time }))

                        val confirmed = upcoming.filter { it.status == LessonStatus.CONFIRMED }
                        val pending = upcoming.filter { it.status == LessonStatus.PENDING }

                        _uiState.value = _uiState.value.copy(
                            userRole = role,
                            upcomingConfirmed = confirmed,
                            upcomingPending = pending,
                            totalLessons = lessons.count { it.lessonStatus == LessonStatus.COMPLETED },
                            pendingLessonsCount = lessons.count { it.lessonStatus == LessonStatus.PENDING },
                            isLoading = false,
                            error = null
                        )

                        // Load pending reviews only for students
                        if (role == UserRole.STUDENT) {
                            val completedLessons =
                                lessons.filter { it.lessonStatus == LessonStatus.COMPLETED }
                            if (completedLessons.isNotEmpty()) {
                                val permanentlyDismissed =
                                    reviewPreferencesManager.dismissedTutorIdsFlow.first()
                                reviewRepository.getStudentReviews(userId).fold(
                                    onSuccess = { reviews ->
                                        val reviewedTutorIds = reviews.map { it.tutorId }.toSet()
                                        val pendingReviews = completedLessons
                                            .filter { it.tutorId !in reviewedTutorIds }
                                            .filter { it.tutorId !in permanentlyDismissed }
                                            .distinctBy { it.tutorId }
                                            .map { lesson ->
                                                PendingReviewInfo(
                                                    tutorId = lesson.tutorId,
                                                    tutorFirstName = lesson.tutorFirstName,
                                                    tutorLastName = lesson.tutorLastName,
                                                    subjectName = lesson.subjectName,
                                                    tutorAvatarUrl = lesson.tutorAvatarUrl?.takeIf { it != "null" }
                                                )
                                            }
                                        _uiState.update { it.copy(pendingReviews = pendingReviews) }
                                    },
                                    onFailure = { /* silently ignore — not a critical feature */ }
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(
                            userRole = role,
                            isLoading = false,
                            error = e.message
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        userRole = role,
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun toggleConfirmed() = _uiState.update { it.copy(confirmedExpanded = !it.confirmedExpanded) }
    fun togglePending() = _uiState.update { it.copy(pendingExpanded = !it.pendingExpanded) }

    // Pending reviews

    fun selectPendingReview(info: PendingReviewInfo) {
        _uiState.update { it.copy(selectedPendingReview = info, pendingReviewError = null) }
    }

    /** Back to the list without marking as permanently dismissed (multi-review flow). */
    fun dismissSelectedPendingReview() {
        _uiState.update { it.copy(selectedPendingReview = null, pendingReviewError = null) }
    }

    /** User explicitly skips — permanently remember in DataStore so the popup never returns. */
    fun dismissAllPendingReviews() {
        val toDismiss = buildSet {
            addAll(_uiState.value.pendingReviews.map { it.tutorId })
            _uiState.value.selectedPendingReview?.let { add(it.tutorId) }
        }
        viewModelScope.launch {
            reviewPreferencesManager.dismissTutors(toDismiss)
        }
        _uiState.update {
            it.copy(
                pendingReviews = emptyList(),
                selectedPendingReview = null,
                pendingReviewError = null
            )
        }
    }

    fun submitPendingReview(rating: Double, comment: String?) {
        val review = _uiState.value.selectedPendingReview ?: return
        viewModelScope.launch {
            val studentId = tokenManager.userIdFlow.first() ?: return@launch
            _uiState.update { it.copy(isSubmittingPendingReview = true, pendingReviewError = null) }
            reviewRepository.addReview(studentId, ReviewRequest(review.tutorId, rating, comment))
                .fold(
                    onSuccess = {
                        val remaining =
                            _uiState.value.pendingReviews.filter { it.tutorId != review.tutorId }
                        _uiState.update {
                            it.copy(
                                isSubmittingPendingReview = false,
                                selectedPendingReview = null,
                                pendingReviews = remaining,
                                pendingReviewError = null,
                                pendingReviewSubmitted = true
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                isSubmittingPendingReview = false,
                                pendingReviewError = e.message
                            )
                        }
                    }
                )
        }
    }

    fun clearPendingReviewError() {
        _uiState.update { it.copy(pendingReviewError = null) }
    }

    fun clearPendingReviewSubmitted() {
        _uiState.update { it.copy(pendingReviewSubmitted = false) }
    }
}
