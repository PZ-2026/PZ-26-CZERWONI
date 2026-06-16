package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.edu.ur.teachly.R

@Composable
fun ConfirmDeleteDialog(message: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AppConfirmDialog(
        title = stringResource(R.string.dialog_confirm_delete_title),
        message = message,
        confirmText = stringResource(R.string.delete),
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        destructive = true
    )
}
