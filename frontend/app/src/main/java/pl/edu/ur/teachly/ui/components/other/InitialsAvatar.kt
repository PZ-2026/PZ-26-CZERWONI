package pl.edu.ur.teachly.ui.components.other

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pl.edu.ur.teachly.BuildConfig
import pl.edu.ur.teachly.ui.theme.AvatarColor

@Composable
fun InitialsAvatar(
    initials: String,
    avatarColor: AvatarColor,
    avatarUrl: String? = null,
    size: Dp = 72.dp,
    cornerRadius: Dp = 24.dp,
    isEditable: Boolean = false,
    onEditClick: () -> Unit = {}
) {
    val fontSize = (size.value * 0.38f).sp

    Box(
        modifier = Modifier
            .size(size)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(avatarColor.background, RoundedCornerShape(cornerRadius))
                .clip(RoundedCornerShape(cornerRadius))
                .clickable(enabled = isEditable) { onEditClick() },
            contentAlignment = Alignment.Center
        ) {
            // Zawsze renderujemy inicjały z tyłu jako fallback
            Text(
                text = initials,
                style = TextStyle(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = avatarColor.foreground,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )

            // Jeżeli URL istnieje i nie jest dosłownym ciągiem "null" lub pustym stubem, rysujemy AsyncImage
            if (!avatarUrl.isNullOrBlank() &&
                !avatarUrl.equals("null", ignoreCase = true) &&
                !avatarUrl.contains("/null", ignoreCase = true) &&
                !avatarUrl.endsWith("/uploads/avatars/", ignoreCase = true)
            ) {
                val fullUrl = if (avatarUrl.startsWith("http")) {
                    avatarUrl
                } else if (avatarUrl.startsWith("/uploads/")) {
                    val baseUrl = BuildConfig.BASE_URL.trimEnd('/')
                    "$baseUrl$avatarUrl"
                } else if (avatarUrl.contains("cache") ||
                    avatarUrl.contains("avatar_upload") ||
                    avatarUrl.startsWith("/data/")
                ) {
                    avatarUrl
                } else {
                    val baseUrl = BuildConfig.BASE_URL.trimEnd('/')
                    if (avatarUrl.startsWith("/")) {
                        "$baseUrl$avatarUrl"
                    } else {
                        "$baseUrl/$avatarUrl"
                    }
                }
                AsyncImage(
                    model = fullUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(size),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Kółeczko z ołówkiem w prawym dolnym rogu
        if (isEditable) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .clip(CircleShape)
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Avatar",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
