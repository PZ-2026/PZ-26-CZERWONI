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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.StatCard
import pl.edu.ur.teachly.ui.components.other.formatDate
import pl.edu.ur.teachly.ui.components.profile.ProfileDataCard
import pl.edu.ur.teachly.ui.components.profile.ProfileDataDivider
import pl.edu.ur.teachly.ui.components.profile.ProfileHeader
import pl.edu.ur.teachly.ui.components.profile.ProfileInfoRow
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel
import pl.edu.ur.teachly.ui.theme.AvatarColors
import java.time.LocalDate

@Composable
fun StudentProfileScreen(
    onBack: () -> Unit,
    onEditClick: () -> Unit,
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
                role = profile.role,
                onBack = onBack,
                onEditClick = onEditClick,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.activity),
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatCard(
                            value = "${profile.lessonsCount}",
                            label = stringResource(R.string.completed_lessons),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                // Dane konta
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
                            label = stringResource(R.string.field_phone),
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
                        icon = Icons.Default.Person,
                        label = stringResource(R.string.role),
                        value = stringResource(R.string.student),
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
