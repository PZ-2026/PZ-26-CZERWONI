package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.TutorRequest
import pl.edu.ur.teachly.data.model.TutorResponse

@Composable
fun TutorEditDialog(
    tutor: TutorResponse,
    onDismiss: () -> Unit,
    onSave: (TutorRequest) -> Unit,
) {
    var bio by remember { mutableStateOf(tutor.bio ?: "") }
    var hourlyRate by remember { mutableStateOf(tutor.hourlyRate.toString()) }
    var offersOnline by remember { mutableStateOf(tutor.offersOnline) }
    var offersInPerson by remember { mutableStateOf(tutor.offersInPerson) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edytuj korepetytora") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = hourlyRate,
                    onValueChange = { hourlyRate = it },
                    label = { Text("Stawka godzinowa (PLN)") },
                    leadingIcon = { Icon(Icons.Default.Payments, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    leadingIcon = { Icon(Icons.Default.Info, null) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
                DialogSectionLabel("Forma zajęć")
                DialogSwitchRow("Zajęcia online", offersOnline) { offersOnline = it }
                DialogSwitchRow("Zajęcia stacjonarne", offersInPerson) { offersInPerson = it }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        TutorRequest(
                            bio = bio.ifBlank { null },
                            hourlyRate = hourlyRate.toDoubleOrNull() ?: tutor.hourlyRate,
                            offersOnline = offersOnline,
                            offersInPerson = offersInPerson,
                        )
                    )
                },
                enabled = hourlyRate.toDoubleOrNull() != null,
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}
