package pl.edu.ur.teachly.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.PrimaryButton
import pl.edu.ur.teachly.ui.components.TutorDetailBody
import pl.edu.ur.teachly.ui.components.TutorDetailHeader

@Composable
fun TutorDetailScreen(
    tutorId     : String,
    onBack      : () -> Unit,
    onBookClick : () -> Unit,
) {
    // TODO: download tutors from ViewModel by tutorId
    val tutor       = MOCK_TUTORS.first { it.id.toString() == tutorId }
    val avatarIndex = remember(tutor.id) { (tutor.id - 1) % AVATAR_COLORS.size }
    val (avatarBg, avatarFg) = avatarColors(avatarIndex)

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TutorDetailHeader(tutor = tutor, avatarBg = avatarBg, avatarFg = avatarFg, onBack = onBack)
        
        Box(modifier = Modifier.weight(1f)) {
            TutorDetailBody(tutor = tutor)
        }
        
        PrimaryButton(
            text     = stringResource(R.string.detail_book_cta),
            onClick  = onBookClick,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )
    }
}
