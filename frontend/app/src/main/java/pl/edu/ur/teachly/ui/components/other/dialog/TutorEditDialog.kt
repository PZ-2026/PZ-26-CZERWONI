package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import pl.edu.ur.teachly.data.model.TutorRequest
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.ui.components.other.authTextFieldColors

@Composable
fun TutorEditDialog(tutor: TutorResponse, onDismiss: () -> Unit, onSave: (TutorRequest) -> Unit) {
    var bio by remember { mutableStateOf(tutor.bio ?: "") }
    var hourlyRate by remember { mutableStateOf(DialogValidation.formatEditableDecimal(tutor.hourlyRate)) }
    var offersOnline by remember { mutableStateOf(tutor.offersOnline) }
    var offersInPerson by remember { mutableStateOf(tutor.offersInPerson) }

    val hourlyRateError = DialogValidation.hourlyRateError(hourlyRate)
    val lessonFormatError = DialogValidation.lessonFormatError(offersOnline, offersInPerson)
    val isValid = DialogValidation.isHourlyRateValid(hourlyRate) &&
        (offersOnline || offersInPerson)

    AppFormDialog(
        title = "Edytuj korepetytora",
        subtitle = "${tutor.firstName} ${tutor.lastName}",
        onDismiss = onDismiss,
        onConfirm = {
            onSave(
                TutorRequest(
                    bio = bio.trim().ifBlank { null },
                    hourlyRate = DialogValidation.parseDecimal(hourlyRate)!!,
                    offersOnline = offersOnline,
                    offersInPerson = offersInPerson
                )
            )
        },
        confirmEnabled = isValid
    ) {
        DialogSectionCard {
            OutlinedTextField(
                value = hourlyRate,
                onValueChange = { value ->
                    DialogValidation.filterDecimalInput(value)?.let { hourlyRate = it }
                },
                label = { Text("Stawka godzinowa (PLN)") },
                leadingIcon = { Icon(Icons.Default.Payments, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = DialogFieldShape,
                colors = authTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = hourlyRateError != null,
                supportingText = hourlyRateError?.let { error -> { Text(error) } }
            )
            OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= DialogValidation.MAX_BIO_LENGTH) bio = it },
                label = { Text("Bio") },
                leadingIcon = { Icon(Icons.Default.Info, null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = DialogFieldShape,
                colors = authTextFieldColors(),
                supportingText = {
                    Text(
                        text = "${bio.length}/${DialogValidation.MAX_BIO_LENGTH}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        DialogSectionCard(title = "Forma zajęć") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DialogSwitchRow("Zajęcia online", offersOnline) { offersOnline = it }
                DialogSwitchRow("Zajęcia stacjonarne", offersInPerson) { offersInPerson = it }
            }
            lessonFormatError?.let { DialogErrorText(it) }
        }
    }
}
