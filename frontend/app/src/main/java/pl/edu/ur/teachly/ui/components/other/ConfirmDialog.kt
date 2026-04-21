package pl.edu.ur.teachly.ui.components.other

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class ConfirmConfig(
    val title: String,
    val message: String,
    val confirmLabel: String,
    val destructive: Boolean = false,
    val action: () -> Unit,
)

@Composable
fun ConfirmDialog(config: ConfirmConfig, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(config.title) },
        text = { Text(config.message) },
        confirmButton = {
            Button(
                onClick = { config.action(); onDismiss() },
                colors = if (config.destructive)
                    ButtonDefaults.buttonColors(
                        containerColor = colorScheme.error,
                        contentColor = colorScheme.onError,
                    )
                else ButtonDefaults.buttonColors(),
            ) {
                Text(config.confirmLabel)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}
