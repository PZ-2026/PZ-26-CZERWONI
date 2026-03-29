package pl.edu.ur.teachly.ui.tutor.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.AVATAR_COLORS
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
    viewModel: TutorDetailViewModel = viewModel()
) {
    LaunchedEffect(tutorId) { viewModel.loadTutor(tutorId) }

    val tutor by viewModel.tutor.collectAsState()

    tutor?.let { t ->
        val avatarIndex = remember(t.id) { (t.id - 1) % AVATAR_COLORS.size }
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
                student = false,
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TutorDetailBody(tutor = t)
            }

            PrimaryButton(
                text = stringResource(R.string.tutordetail_book_cta),
                onClick = onBookClick,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
    }
}