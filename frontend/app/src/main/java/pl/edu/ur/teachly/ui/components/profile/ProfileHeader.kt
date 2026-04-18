package pl.edu.ur.teachly.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.InitialsAvatar
import pl.edu.ur.teachly.ui.profile.viewmodels.StudentProfile
import pl.edu.ur.teachly.ui.theme.AvatarColor

@Composable
fun ProfileHeader(
    profile: StudentProfile,            // student profile on default
    avatarColor: AvatarColor,
    student: Boolean = true,
    onBack: () -> Unit,
    onEditClick: (() -> Unit)? = null,  // null = guest view
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(colorScheme.onPrimaryContainer, colorScheme.primary),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                )
            )
            .padding(horizontal = 24.dp)
            .padding(top = 28.dp, bottom = 28.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            colorScheme.onPrimary.copy(alpha = 0.15f),
                            RoundedCornerShape(10.dp),
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (onEditClick != null) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                colorScheme.onPrimary.copy(alpha = 0.15f),
                                RoundedCornerShape(10.dp),
                            )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.cd_edit_profile),
                            tint = colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InitialsAvatar(
                    initials = profile.initials,
                    avatarColor = avatarColor,
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.fullName,
                        style = typography.titleLarge,
                        color = colorScheme.onPrimary,
                    )
                    Text(
                        text = stringResource(
                            if (student) R.string.profile_student_role
                            else R.string.profile_tutor_role
                        ),
                        style = typography.bodySmall,
                        color = colorScheme.onPrimary.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}