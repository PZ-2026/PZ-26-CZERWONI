package pl.edu.ur.teachly.ui.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.auth.AuthTextField
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.ErrorBanner
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.InitialsAvatar
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel
import pl.edu.ur.teachly.ui.theme.AvatarColors

@Composable
fun ProfileEditScreen(
    onBack: () -> Unit,
    onSave: (Boolean) -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val editState by viewModel.editState.collectAsState()

    LaunchedEffect(Unit) { viewModel.startEditing() }

    LaunchedEffect(editState.isSaved) {
        if (editState.isSaved) {
            onSave(editState.requiresRelogin)
            viewModel.resetEditState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        AppHeader(
            title = "Edytuj profil",
            subtitle = "Zmień swoje dane",
            background = HeaderBackground.Diagonal(
                listOf(colorScheme.onPrimaryContainer, colorScheme.primary)
            ),
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            val initials =
                stringResource(
                    R.string.initials,
                    editState.firstName.firstOrNull() ?: "",
                    editState.lastName.firstOrNull() ?: ""
                )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                InitialsAvatar(initials = initials, avatarColor = AvatarColors[0], size = 96.dp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            AuthTextField(
                label = stringResource(R.string.field_first_name),
                value = editState.firstName,
                onValueChange = viewModel::onFirstNameChange,
                placeholder = stringResource(R.string.first_name_placeholder),
                capitalize = true,
            )
            AuthTextField(
                label = stringResource(R.string.field_last_name),
                value = editState.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = stringResource(R.string.last_name_placeholder),
                capitalize = true,
            )
            AuthTextField(
                label = stringResource(R.string.email),
                value = editState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "twój@email.com"
            )
            AuthTextField(
                label = stringResource(R.string.phone),
                value = editState.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChange,
                placeholder = "123 456 789",
                visualTransformation = pl.edu.ur.teachly.ui.components.other.PhoneVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )
            pl.edu.ur.teachly.ui.components.other.PasswordTextField(
                label = "Nowe hasło (opcjonalnie)",
                value = editState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Pozostaw puste by nie zmieniać",
            )
            if (editState.error != null) {
                ErrorBanner(message = editState.error!!)
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.profile_edit_save),
                onClick = { viewModel.saveProfile() },
                isLoading = editState.isLoading,
                modifier = Modifier.padding(bottom = 32.dp, top = 24.dp),
            )
        }
    }
}
