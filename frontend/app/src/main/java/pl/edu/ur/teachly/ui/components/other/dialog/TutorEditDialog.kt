package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.TutorRequest
import pl.edu.ur.teachly.data.model.TutorResponse
import pl.edu.ur.teachly.data.model.TutorSubjectResponse
import pl.edu.ur.teachly.ui.components.other.authTextFieldColors

@Composable
fun TutorEditDialog(
    tutor: TutorResponse,
    subjects: List<TutorSubjectResponse>,
    isSubjectsLoading: Boolean,
    onDismiss: () -> Unit,
    onSave: (TutorRequest) -> Unit,
    onAddSubject: () -> Unit,
    onRemoveSubject: (Int) -> Unit
) {
    var bio by remember { mutableStateOf(tutor.bio ?: "") }
    var hourlyRate by remember { mutableStateOf(DialogValidation.formatEditableDecimal(tutor.hourlyRate)) }
    var offersOnline by remember { mutableStateOf(tutor.offersOnline) }
    var offersInPerson by remember { mutableStateOf(tutor.offersInPerson) }
    var city by remember { mutableStateOf(tutor.city ?: "") }

    val hourlyRateError = DialogValidation.hourlyRateError(hourlyRate)
    val lessonFormatError = DialogValidation.lessonFormatError(offersOnline, offersInPerson)
    val hasCityIfInPerson = !offersInPerson || city.trim().length in 2..50
    val isValid = DialogValidation.isHourlyRateValid(hourlyRate) &&
        (offersOnline || offersInPerson) &&
        hasCityIfInPerson &&
        subjects.isNotEmpty()

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
                    offersInPerson = offersInPerson,
                    city = if (offersInPerson) city.trim().ifBlank { null } else null
                )
            )
        },
        confirmEnabled = isValid && !isSubjectsLoading
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
            if (offersInPerson) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { if (it.length <= 50) city = it },
                    label = { Text("Miasto zajęć stacjonarnych") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = DialogFieldShape,
                    colors = authTextFieldColors(),
                    isError = !hasCityIfInPerson,
                    supportingText = if (!hasCityIfInPerson) {
                        { Text("Podaj miasto (2–50 znaków)") }
                    } else {
                        null
                    }
                )
            }
        }

        DialogSectionCard(title = "Prowadzone przedmioty") {
            when {
                isSubjectsLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                subjects.isEmpty() -> {
                    Text(
                        text = "Brak przypisanych przedmiotów. Dodaj co najmniej jeden.",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = colorScheme.error
                    )
                }
                else -> {
                    Column {
                        subjects.forEachIndexed { index, subject ->
                            TutorSubjectListRow(
                                subject = subject,
                                onRemove = { onRemoveSubject(subject.id) }
                            )
                            if (index < subjects.lastIndex) {
                                HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.35f))
                            }
                        }
                    }
                }
            }
            TextButton(
                onClick = onAddSubject,
                modifier = Modifier.align(Alignment.Start),
                enabled = !isSubjectsLoading
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Dodaj przedmiot", modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}
