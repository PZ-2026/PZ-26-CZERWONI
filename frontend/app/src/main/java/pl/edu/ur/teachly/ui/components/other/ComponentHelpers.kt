package pl.edu.ur.teachly.ui.components.other

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = colorScheme.primary,
    unfocusedBorderColor = colorScheme.outline,
    focusedContainerColor = colorScheme.surface,
    unfocusedContainerColor = colorScheme.surfaceVariant,
    errorBorderColor = colorScheme.error,
    cursorColor = colorScheme.primary,
)