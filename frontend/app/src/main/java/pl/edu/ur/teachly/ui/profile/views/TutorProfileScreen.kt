package pl.edu.ur.teachly.ui.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.profile.ProfileHeader
import pl.edu.ur.teachly.ui.components.tutor.TutorDetailBody
import pl.edu.ur.teachly.ui.profile.viewmodels.StudentProfile
import pl.edu.ur.teachly.ui.profile.viewmodels.TutorProfileViewModel
import pl.edu.ur.teachly.ui.profile.viewmodels.TutorStats
import pl.edu.ur.teachly.ui.theme.AvatarColors

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
                    student = false,
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
                        PrimaryButton(
                            text = stringResource(R.string.profile_logout),
                            onClick = onLogout,
                            modifier = Modifier.padding(bottom = 24.dp),
                        )
                    }
                }
            }
        }

        else -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Nie znaleziono profilu",
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
            )
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                value = "${stats.completedLessons}",
                label = stringResource(R.string.tutor_lessons_count),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = "${stats.reviewsCount}",
                label = stringResource(R.string.tutor_reviews_count),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = "%.0f zł".format(stats.totalEarnings),
                label = stringResource(R.string.tutor_earnings),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
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
