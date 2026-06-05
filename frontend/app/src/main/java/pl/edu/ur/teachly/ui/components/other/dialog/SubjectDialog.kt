package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import pl.edu.ur.teachly.data.model.SubjectCategoryResponse
import pl.edu.ur.teachly.ui.components.other.authTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDialog(
    title: String,
    initialName: String,
    initialCategoryId: Int,
    categories: List<SubjectCategoryResponse>,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var categoryId by remember { mutableIntStateOf(initialCategoryId) }
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == categoryId }
    val nameError = DialogValidation.labelNameError(name, "Nazwa przedmiotu")
    val categoryError = if (categoryId <= 0) "Wybierz kategorię" else null
    val isValid = DialogValidation.isLabelNameValid(name) && categoryId > 0

    AppFormDialog(
        title = title,
        onDismiss = onDismiss,
        onConfirm = { onSave(name.trim(), categoryId) },
        confirmEnabled = isValid
    ) {
        DialogSectionCard {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= DialogValidation.MAX_LABEL_LENGTH) name = it },
                label = { Text("Nazwa przedmiotu") },
                leadingIcon = { Icon(Icons.Default.School, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = DialogFieldShape,
                colors = authTextFieldColors(),
                isError = nameError != null,
                supportingText = nameError?.let { error -> { Text(error) } }
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory?.categoryName ?: "Wybierz kategorię",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategoria") },
                    leadingIcon = { Icon(Icons.Default.Category, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    shape = DialogFieldShape,
                    colors = authTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.categoryName) },
                            onClick = {
                                categoryId = cat.id
                                expanded = false
                            }
                        )
                    }
                }
            }
            categoryError?.let { DialogErrorText(it) }
        }
    }
}
