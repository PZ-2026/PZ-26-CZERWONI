package pl.edu.ur.teachly.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import pl.edu.ur.teachly.ui.auth.RegisterUiState
import pl.edu.ur.teachly.ui.auth.RegisterViewModel

@Composable
fun StepTwoContent(
    uiState   : RegisterUiState,
    viewModel : RegisterViewModel,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 28.dp, bottom = 48.dp),
    ) {
        AuthTextField(
            value           = uiState.first_name,
            onValueChange   = viewModel::onFirstNameChange,
            label           = stringResource(R.string.field_first_name),
            placeholder     = "Jan",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
        AuthTextField(
            value           = uiState.last_name,
            onValueChange   = viewModel::onLastNameChange,
            label           = stringResource(R.string.field_last_name),
            placeholder     = "Kowalski",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
        AuthTextField(
            value           = uiState.email,
            onValueChange   = viewModel::onEmailChange,
            label           = stringResource(R.string.field_email),
            placeholder     = "jan@example.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
        PasswordTextField(
            value           = uiState.password,
            onValueChange   = viewModel::onPasswordChange,
            label           = stringResource(R.string.field_password),
            placeholder     = stringResource(R.string.field_password_hint),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); viewModel.register() }),
        )

        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter   = fadeIn(tween(200)) + expandVertically(),
            exit    = fadeOut(tween(150)) + shrinkVertically(),
        ) {
            ErrorBanner(message = uiState.errorMessage.orEmpty())
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Text(
                text      = stringResource(R.string.register_terms),
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            text      = stringResource(R.string.register_cta),
            onClick   = { focusManager.clearFocus(); viewModel.register() },
            isLoading = uiState.isLoading,
        )
    }
}
