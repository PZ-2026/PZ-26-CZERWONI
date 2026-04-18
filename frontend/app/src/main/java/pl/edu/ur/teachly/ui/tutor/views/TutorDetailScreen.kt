package pl.edu.ur.teachly.ui.tutor.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.profile.ProfileHeader
import pl.edu.ur.teachly.ui.components.tutor.TutorDetailBody
import pl.edu.ur.teachly.ui.profile.viewmodels.StudentProfile
import pl.edu.ur.teachly.ui.theme.AvatarColors
import pl.edu.ur.teachly.ui.tutor.viewmodels.TutorDetailViewModel

@Composable
fun TutorDetailScreen(
    tutorId: String,
    onBack: () -> Unit,
    onBookClick: () -> Unit,
    viewModel: TutorDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(tutorId) { viewModel.loadTutor(tutorId) }

    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        state.error != null -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = state.error!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(24.dp),
            )
        }

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
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ProfileHeader(
                    profile = profile,
                    avatarColor = AvatarColors[avatarIndex % AvatarColors.size],
                    onBack = onBack,
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    TutorDetailBody(tutor = t, reviews = state.reviews)
                }

                PrimaryButton(
                    text = stringResource(R.string.tutordetail_book_cta),
                    onClick = onBookClick,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                )
            }
        }
    }
}

