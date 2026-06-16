package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.authTextFieldColors

@Composable
fun CategoryDialog(title: String, initialName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(initialName) }
    val nameError = DialogValidation.labelNameError(name, "Nazwa kategorii")
    val isValid = DialogValidation.isLabelNameValid(name)

    AppFormDialog(
        title = title,
        onDismiss = onDismiss,
        onConfirm = { onSave(name.trim()) },
        confirmEnabled = isValid
    ) {
        DialogSectionCard {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= DialogValidation.MAX_LABEL_LENGTH) name = it },
                label = { Text(stringResource(R.string.field_name)) },
                leadingIcon = { Icon(Icons.Default.Edit, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = DialogFieldShape,
                colors = authTextFieldColors(),
                isError = nameError != null,
                supportingText = nameError?.let { error -> { Text(error) } }
            )
        }
    }
}
