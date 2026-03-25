package pl.edu.ur.teachly.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.*

@Composable
fun LoginScreen(
    onBack    : () -> Unit,
    onSuccess : () -> Unit,
    viewModel : LoginViewModel = viewModel(),
) {
    val uiState      by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onSuccess()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AuthHeader(
            title    = stringResource(R.string.login_title),
            subtitle = stringResource(R.string.login_subtitle),
            onBack   = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 48.dp)
        ) {
            AuthTextField(
                value           = uiState.email,
                onValueChange   = viewModel::onEmailChange,
                label           = stringResource(R.string.field_email),
                placeholder     = "jan@example.com",
                leadingIcon     = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction    = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
            )

            PasswordTextField(
                value           = uiState.password,
                onValueChange   = viewModel::onPasswordChange,
                label           = stringResource(R.string.field_password),
                placeholder     = stringResource(R.string.field_password),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(); viewModel.login() }
                ),
            )

            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter   = fadeIn(tween(200)) + expandVertically(),
                exit    = fadeOut(tween(150)) + shrinkVertically(),
            ) {
                ErrorBanner(message = uiState.errorMessage?.let { stringResource(it) }.orEmpty())
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { /* TODO: reset hasła */ }) {
                    Text(
                        text  = stringResource(R.string.login_forgot_password),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            PrimaryButton(
                text      = stringResource(R.string.login_cta),
                onClick   = { focusManager.clearFocus(); viewModel.login() },
                isLoading = uiState.isLoading,
            )

            DividerOr()

            GoogleButton()
        }
    }
}

@Composable
private fun GoogleButton() {
    OutlinedButton(
        onClick  = { /* TODO: Google Sign-In */ },
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape    = RoundedCornerShape(18.dp),
        border   = androidx.compose.foundation.BorderStroke(
            1.5.dp, MaterialTheme.colorScheme.outline,
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor   = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text("G", style = MaterialTheme.typography.titleMedium, color = pl.edu.ur.teachly.ui.theme.GoogleBlue)
            Text(
                text  = stringResource(R.string.login_google),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}