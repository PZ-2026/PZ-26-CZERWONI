package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.runtime.Composable

data class ConfirmConfig(
    val title: String,
    val message: String,
    val confirmLabel: String,
    val destructive: Boolean = false,
    val action: () -> Unit
)

@Composable
fun ConfirmDialog(config: ConfirmConfig, onDismiss: () -> Unit) {
    AppConfirmDialog(
        title = config.title,
        message = config.message,
        confirmText = config.confirmLabel,
        onDismiss = onDismiss,
        onConfirm = {
            config.action()
            onDismiss()
        },
        destructive = config.destructive
    )
}
