package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialog(message: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AppConfirmDialog(
        title = "Potwierdź usunięcie",
        message = message,
        confirmText = "Usuń",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        destructive = true
    )
}
