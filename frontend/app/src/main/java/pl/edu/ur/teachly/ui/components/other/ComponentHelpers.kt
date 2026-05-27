package pl.edu.ur.teachly.ui.components.other

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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

fun formatDate(date: LocalDate, locale: Locale = Locale.forLanguageTag("pl")): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
    return date.format(formatter)
}

fun formatPhoneNumber(phone: String): String = phone.chunked(3).joinToString(" ")

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = text.text.chunked(3).joinToString(" ")

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = offset + when {
                offset <= 3 -> 0
                offset <= 6 -> 1
                else -> 2
            }

            override fun transformedToOriginal(offset: Int) = offset - when {
                offset <= 4 -> 0
                offset <= 8 -> 1
                else -> 2
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}