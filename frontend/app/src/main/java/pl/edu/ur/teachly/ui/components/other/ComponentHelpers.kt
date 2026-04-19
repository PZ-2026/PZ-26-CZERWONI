package pl.edu.ur.teachly.ui.components.other

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = colorScheme.primary,
    unfocusedBorderColor = colorScheme.outline,
    focusedContainerColor = colorScheme.surface,
    unfocusedContainerColor = colorScheme.surfaceVariant,
    errorBorderColor = colorScheme.error,
    cursorColor = colorScheme.primary,
)

fun formatDate(date: LocalDate, locale: Locale = Locale("pl")): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
    return date.format(formatter)
}
