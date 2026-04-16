package pl.edu.ur.teachly.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.ui.theme.AvatarColor

@Composable
fun InitialsAvatar(
    initials: String,
    avatarColor: AvatarColor,
    size: Dp = 72.dp,
    cornerRadius: Dp = 24.dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(avatarColor.background, RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = avatarColor.foreground
        )
    }
}