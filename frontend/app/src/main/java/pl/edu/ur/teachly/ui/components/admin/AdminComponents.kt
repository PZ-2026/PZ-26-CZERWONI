package pl.edu.ur.teachly.ui.components.admin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminMessageSnackbars(
    successMessage: String?,
    errorMessage: String?,
) {
    successMessage?.let {
        Snackbar(
            modifier = Modifier.padding(8.dp),
            containerColor = colorScheme.primaryContainer,
        ) {
            Text(it, color = colorScheme.onPrimaryContainer)
        }
    }
    errorMessage?.let {
        Snackbar(
            modifier = Modifier.padding(8.dp),
            containerColor = colorScheme.errorContainer,
        ) {
            Text(it, color = colorScheme.onErrorContainer)
        }
    }
}

@Composable
fun AdminSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Szukaj...",
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
        ),
        singleLine = true,
    )
}

