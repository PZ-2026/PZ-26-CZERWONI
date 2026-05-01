package pl.edu.ur.teachly.ui.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.formatDate
import pl.edu.ur.teachly.ui.components.other.formatPhoneNumber
import pl.edu.ur.teachly.ui.components.profile.ProfileDataCard
import pl.edu.ur.teachly.ui.components.profile.ProfileDataDivider
import pl.edu.ur.teachly.ui.components.profile.ProfileHeader
import pl.edu.ur.teachly.ui.components.profile.ProfileInfoRow
import pl.edu.ur.teachly.ui.components.profile.TutorStatsSection
import pl.edu.ur.teachly.ui.components.tutor.TutorDetailBody
import pl.edu.ur.teachly.ui.profile.viewmodels.StudentProfile
import pl.edu.ur.teachly.ui.profile.viewmodels.TutorProfileViewModel
import pl.edu.ur.teachly.ui.review.views.AddReviewDialog
import pl.edu.ur.teachly.ui.theme.AvatarColors
import java.time.LocalDate

@Composable
fun TutorProfileScreen(
    tutorId: String,
    isMyProfile: Boolean = false,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onLogout: () -> Unit,
    onSeeAllReviews: () -> Unit = {},
    onAvailabilityClick: () -> Unit = {},
    viewModel: TutorProfileViewModel = koinViewModel(),
) {
    LaunchedEffect(tutorId) { viewModel.loadProfile(tutorId) }

    val state by viewModel.state.collectAsState()
    var showReviewDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.reviewSubmitSuccess) {
        if (state.reviewSubmitSuccess) {
            showReviewDialog = false
            viewModel.clearReviewSuccess()
        }
    }

    if (showReviewDialog) {
        AddReviewDialog(
            isLoading = state.isSubmittingReview,
            error = state.reviewError,
            onDismiss = {
                showReviewDialog = false
                viewModel.clearReviewError()
            },
            onSubmit = { rating, comment ->
                val id = tutorId.toIntOrNull() ?: return@AddReviewDialog
                viewModel.submitReview(id, rating, comment)
            },
        )
    }

    when {
        state.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        state.tutor != null -> {
            val t = state.tutor!!
            val avatarIndex = remember(t.id) { (t.id - 1) % AvatarColors.size }
            val profile = remember(t) {
                StudentProfile(
                    firstName = t.name.substringBefore(" "),
                    lastName = t.name.substringAfter(" "),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
            ) {
                ProfileHeader(
                    profile = profile,
                    avatarColor = AvatarColors[avatarIndex % AvatarColors.size],
                    role = UserRole.TUTOR,
                    onBack = onBack,
                    onEditClick = if (isMyProfile) onEditClick else null,
                    onCalendarClick = if (isMyProfile) onAvailabilityClick else null,
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    TutorStatsSection(stats = state.stats)
                    TutorDetailBody(
                        tutor = t,
                        reviews = state.reviews,
                        canReview = state.canReview && !isMyProfile,
                        onAddReview = { showReviewDialog = true },
                        onSeeAllReviews = onSeeAllReviews,
                    )

                    if (isMyProfile) {
                        ProfileDataCard(title = stringResource(R.string.account_data)) {
                            if (state.email.isNotBlank()) {
                                ProfileInfoRow(
                                    icon = Icons.Default.AlternateEmail,
                                    label = stringResource(R.string.email),
                                    value = state.email,
                                )
                                ProfileDataDivider()
                            }
                            val phone = state.phoneNumber?.let { formatPhoneNumber(it) }
                            if (!phone.isNullOrBlank()) {
                                ProfileInfoRow(
                                    icon = Icons.Default.Phone,
                                    label = stringResource(R.string.field_phone),
                                    value = phone,
                                )
                                ProfileDataDivider()
                            }
                            ProfileInfoRow(
                                icon = Icons.Default.AttachMoney,
                                label = stringResource(R.string.hourly_rate),
                                value = stringResource(R.string.hourly_rate_value, t.pricePerHour),
                            )
                            ProfileDataDivider()
                            ProfileInfoRow(
                                icon = Icons.Default.Wifi,
                                label = stringResource(R.string.lesson_format),
                                value = t.tags.joinToString(" / "),
                            )
                            if (profile.createdAt.isNotBlank()) {
                                ProfileDataDivider()
                                ProfileInfoRow(
                                    icon = Icons.Default.CalendarToday,
                                    label = stringResource(R.string.account_active_since),
                                    value = formatDate(LocalDate.parse(profile.createdAt.take(10))),
                                )
                            }
                            ProfileDataDivider()
                            ProfileInfoRow(
                                icon = Icons.Default.Person,
                                label = stringResource(R.string.role),
                                value = stringResource(R.string.tutor),
                            )
                        }
                    }

                    if (isMyProfile) {
                        PrimaryButton(
                            text = stringResource(R.string.profile_logout),
                            onClick = onLogout,
                            modifier = Modifier.padding(bottom = 24.dp),
                        )
                    }
                }
            }
        }

        else -> {
            if (isMyProfile) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Profil nieuzupełniony",
                        style = typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Musisz skonfigurować swój profil korepetytora, aby inni mogli Cię znaleźć.",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .padding(bottom = 24.dp),
                        textAlign = TextAlign.Center
                    )
                    PrimaryButton(
                        text = "Uzupełnij dane korepetytora",
                        onClick = { /* TODO: Handle tutor data change */ },
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(
                        text = stringResource(R.string.profile_logout),
                        onClick = onLogout,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.profile_not_found),
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
