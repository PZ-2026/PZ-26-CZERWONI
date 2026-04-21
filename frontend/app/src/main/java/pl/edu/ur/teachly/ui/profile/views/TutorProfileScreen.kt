package pl.edu.ur.teachly.ui.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.formatDate
import pl.edu.ur.teachly.ui.components.profile.ProfileDataCard
import pl.edu.ur.teachly.ui.components.profile.ProfileDataDivider
import pl.edu.ur.teachly.ui.components.profile.ProfileHeader
import pl.edu.ur.teachly.ui.components.profile.ProfileInfoRow
import pl.edu.ur.teachly.ui.components.tutor.TutorDetailBody
import pl.edu.ur.teachly.ui.profile.viewmodels.StudentProfile
import pl.edu.ur.teachly.ui.profile.viewmodels.TutorProfileViewModel
import pl.edu.ur.teachly.ui.profile.viewmodels.TutorStats
import pl.edu.ur.teachly.ui.theme.AvatarColors
import java.time.LocalDate

@Composable
fun TutorProfileScreen(
    tutorId: String,
    isMyProfile: Boolean = false,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: TutorProfileViewModel = koinViewModel(),
) {
    LaunchedEffect(tutorId) { viewModel.loadProfile(tutorId) }

    val state by viewModel.state.collectAsState()

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
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    TutorStatsSection(stats = state.stats)
                    TutorDetailBody(tutor = t)

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
                            if (profile.createdAt.isNotBlank()) { // TODO: Can add this to profile / not necessary
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
                            modifier = Modifier.padding(bottom = 24.dp, top = 8.dp),
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
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    PrimaryButton(
                        text = "Uzupełnij dane korepetytora",
                        onClick = { /* TODO: Handle tutor data change */ },
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
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

@Composable
fun TutorStatsSection(stats: TutorStats) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.profile_stats_title),
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground,
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TutorStatCard(
                    value = "${stats.completedLessons}",
                    label = stringResource(R.string.tutor_lessons_count),
                    modifier = Modifier.weight(1f),
                )
                TutorStatCard(
                    value = if (stats.avgRating > 0.0) "%.1f".format(stats.avgRating) else "–",
                    label = stringResource(R.string.avg_rating),
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TutorStatCard(
                    value = "${stats.reviewsCount}",
                    label = stringResource(R.string.tutor_reviews_count),
                    modifier = Modifier.weight(1f),
                )
                TutorStatCard(
                    value = stringResource(R.string.total_earnings).format(stats.totalEarnings),
                    label = stringResource(R.string.tutor_earnings),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun TutorStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
            )
            Text(
                text = label,
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
            )
        }
    }
}
