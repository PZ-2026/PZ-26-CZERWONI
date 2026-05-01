package pl.edu.ur.teachly.ui.components.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MessageSnackbars(
    successMessage: String?,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    val enter = slideInVertically(tween(300)) { it } + fadeIn(tween(300))
    val exit = slideOutVertically(tween(200)) { it } + fadeOut(tween(200))

    AnimatedVisibility(
        visible = errorMessage != null,
        enter = enter,
        exit = exit,
        modifier = modifier
    ) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            containerColor = colorScheme.errorContainer,
        ) { Text(errorMessage.orEmpty(), color = colorScheme.onErrorContainer) }
    }

    AnimatedVisibility(
        visible = successMessage != null && errorMessage == null,
        enter = enter,
        exit = exit,
        modifier = modifier
    ) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            containerColor = colorScheme.primaryContainer,
        ) { Text(successMessage.orEmpty(), color = colorScheme.onPrimaryContainer) }
    }
}
