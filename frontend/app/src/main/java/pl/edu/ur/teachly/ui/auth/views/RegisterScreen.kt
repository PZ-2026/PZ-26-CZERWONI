package pl.edu.ur.teachly.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.AuthHeader
import pl.edu.ur.teachly.ui.components.StepOneContent
import pl.edu.ur.teachly.ui.components.StepProgressBar
import pl.edu.ur.teachly.ui.components.StepTwoContent

@Composable
fun RegisterScreen(
    onBack    : () -> Unit,
    onSuccess : () -> Unit,
    viewModel : RegisterViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onSuccess()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AuthHeader(
            title    = if (uiState.step == 1) stringResource(R.string.register_step1_title)
            else                   stringResource(R.string.register_step2_title),
            subtitle = if (uiState.step == 1) stringResource(R.string.register_step1_subtitle)
            else                   stringResource(R.string.register_step2_subtitle),
            onBack   = if (uiState.step == 1) onBack else viewModel::onPreviousStep,
            extra    = { StepProgressBar(step = uiState.step) },
        )

        AnimatedContent(
            targetState  = uiState.step,
            transitionSpec = {
                if (targetState > initialState)
                    (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                else
                    (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
            },
            label = "step_transition",
        ) { step ->
            if (step == 1) {
                StepOneContent(
                    selectedRole = uiState.selectedRole,
                    onRoleSelected = viewModel::onRoleSelected,
                    onNext = viewModel::onNextStep,
                )
            } else {
                StepTwoContent(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}