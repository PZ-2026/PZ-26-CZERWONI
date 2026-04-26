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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.formatDate
import pl.edu.ur.teachly.ui.components.profile.AdminBadge
import pl.edu.ur.teachly.ui.components.profile.ProfileDataCard
import pl.edu.ur.teachly.ui.components.profile.ProfileDataDivider
import pl.edu.ur.teachly.ui.components.profile.ProfileHeader
import pl.edu.ur.teachly.ui.components.profile.ProfileInfoRow
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel
import pl.edu.ur.teachly.ui.theme.AvatarColors
import java.time.LocalDate

@Composable
fun AdminProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val profile by viewModel.profile.collectAsState()

    when {
        profile.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            ProfileHeader(
                profile = profile,
                avatarColor = AvatarColors[0],
                role = UserRole.ADMIN,
                onBack = onBack,
                onEditClick = null,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Spacer(Modifier.height(4.dp))

                AdminBadge()

                ProfileDataCard(title = stringResource(R.string.account_data)) {
                    if (profile.email.isNotBlank()) {
                        ProfileInfoRow(
                            icon = Icons.Default.AlternateEmail,
                            label = stringResource(R.string.email),
                            value = profile.email,
                        )
                    }
                    val phone = profile.phoneNumber
                    if (!phone.isNullOrBlank()) {
                        ProfileDataDivider()
                        ProfileInfoRow(
                            icon = Icons.Default.Phone,
                            label = stringResource(R.string.phone),
                            value = phone,
                        )
                    }
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
                        icon = Icons.Default.AdminPanelSettings,
                        label = stringResource(R.string.role),
                        value = stringResource(R.string.admin),
                    )
                }

                PrimaryButton(
                    text = stringResource(R.string.logout),
                    onClick = onLogout,
                    modifier = Modifier.padding(bottom = 32.dp, top = 8.dp),
                )
            }
        }
    }
}

