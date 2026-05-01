package pl.edu.ur.teachly.ui.components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.auth.viewmodels.RegisterUiState
import pl.edu.ur.teachly.ui.auth.viewmodels.RegisterViewModel
import pl.edu.ur.teachly.ui.components.other.ErrorBanner
import pl.edu.ur.teachly.ui.components.other.PasswordTextField
import pl.edu.ur.teachly.ui.components.other.PhoneVisualTransformation
import pl.edu.ur.teachly.ui.components.other.PrimaryButton

@Composable
fun StepTwoContent(
    uiState: RegisterUiState,
    viewModel: RegisterViewModel,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 28.dp, bottom = 48.dp),
    ) {
        AuthTextField(
            value = uiState.firstName,
            onValueChange = viewModel::onFirstNameChange,
            label = stringResource(R.string.field_first_name),
            placeholder = stringResource(R.string.first_name_placeholder),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            capitalize = true,
        )
        AuthTextField(
            value = uiState.lastName,
            onValueChange = viewModel::onLastNameChange,
            label = stringResource(R.string.field_last_name),
            placeholder = stringResource(R.string.last_name_placeholder),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            capitalize = true,
        )
        AuthTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = stringResource(R.string.field_email),
            placeholder = stringResource(R.string.email_placeholder),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
        AuthTextField(
            value = uiState.phoneNumber,
            onValueChange = viewModel::onPhoneChange,
            label = stringResource(R.string.field_phone),
            placeholder = stringResource(R.string.phone_number_placeholder),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            visualTransformation = PhoneVisualTransformation(),
        )
        PasswordTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = stringResource(R.string.field_password),
            placeholder = stringResource(R.string.field_password_hint),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); viewModel.register() }),
        )

        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn(tween(200)) + expandVertically(),
            exit = fadeOut(tween(150)) + shrinkVertically(),
        ) {
            ErrorBanner(message = uiState.errorMessage.orEmpty())
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.register_terms),
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            text = stringResource(R.string.register_cta),
            onClick = { focusManager.clearFocus(); viewModel.register() },
            isLoading = uiState.isLoading,
        )
    }
}
