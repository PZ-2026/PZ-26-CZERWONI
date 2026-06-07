package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.model.SubjectResponse
import pl.edu.ur.teachly.data.model.TutorSubjectResponse
import pl.edu.ur.teachly.ui.models.TeachingLevel
import pl.edu.ur.teachly.ui.models.activeTeachingLevels
import pl.edu.ur.teachly.ui.models.displayLabel
import pl.edu.ur.teachly.ui.components.other.authTextFieldColors
import pl.edu.ur.teachly.ui.models.shortLabel

@Composable
fun TutorSubjectListRow(subject: TutorSubjectResponse, onRemove: () -> Unit) {
    val levels = subject.activeTeachingLevels().map { it.shortLabel() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject.subjectName,
                style = typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (levels.isNotEmpty()) {
                Text(
                    text = levels.joinToString(", "),
                    style = typography.labelSmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Usuń przedmiot",
                tint = colorScheme.error
            )
        }
    }
}

@Composable
fun AddTutorSubjectDialog(
    availableSubjects: List<SubjectResponse>,
    alreadyAddedSubjectIds: Set<Int>,
    onDismiss: () -> Unit,
    onConfirm: (Int, Boolean, Boolean, Boolean, Boolean, Boolean) -> Unit
) {
    val notAdded = availableSubjects.filter { it.id !in alreadyAddedSubjectIds }
    var selectedSubject by remember { mutableStateOf(notAdded.firstOrNull()) }
    var levelPrimary by remember { mutableStateOf(false) }
    var levelHighSchool by remember { mutableStateOf(false) }
    var levelUniversity by remember { mutableStateOf(false) }
    var levelExamPrep by remember { mutableStateOf(false) }
    var levelProfessional by remember { mutableStateOf(false) }

    val atLeastOneLevel =
        levelPrimary || levelHighSchool || levelUniversity || levelExamPrep || levelProfessional

    AppFormDialog(
        title = "Dodaj przedmiot",
        onDismiss = onDismiss,
        onConfirm = {
            selectedSubject?.let { subject ->
                onConfirm(
                    subject.id,
                    levelPrimary,
                    levelHighSchool,
                    levelUniversity,
                    levelExamPrep,
                    levelProfessional
                )
            }
        },
        confirmText = "Dodaj",
        confirmEnabled = selectedSubject != null && atLeastOneLevel
    ) {
        if (notAdded.isEmpty()) {
            Text(
                text = "Wszystkie dostępne przedmioty zostały już dodane.",
                style = typography.bodyMedium
            )
        } else {
            DialogSectionCard(title = "Przedmiot") {
                TutorSubjectDropdown(
                    subjects = notAdded,
                    selected = selectedSubject,
                    onSelect = { selectedSubject = it }
                )
            }
            DialogSectionCard(title = stringResource(R.string.teaching_levels_dialog_title)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    TutorSubjectLevelCheckRow(TeachingLevel.PRIMARY.displayLabel(), levelPrimary) {
                        levelPrimary = it
                    }
                    TutorSubjectLevelCheckRow(TeachingLevel.HIGH_SCHOOL.displayLabel(), levelHighSchool) {
                        levelHighSchool = it
                    }
                    TutorSubjectLevelCheckRow(TeachingLevel.UNIVERSITY.displayLabel(), levelUniversity) {
                        levelUniversity = it
                    }
                    TutorSubjectLevelCheckRow(TeachingLevel.EXAM.displayLabel(), levelExamPrep) {
                        levelExamPrep = it
                    }
                    TutorSubjectLevelCheckRow(TeachingLevel.PROFESSIONAL.displayLabel(), levelProfessional) {
                        levelProfessional = it
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorSubjectLevelCheckRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(
            text = label,
            style = typography.bodyMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TutorSubjectDropdown(
    subjects: List<SubjectResponse>,
    selected: SubjectResponse?,
    onSelect: (SubjectResponse) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected?.subjectName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Wybierz przedmiot") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
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
            subjects.forEach { subject ->
                DropdownMenuItem(
                    text = { Text("${subject.subjectName} (${subject.categoryName})") },
                    onClick = {
                        onSelect(subject)
                        expanded = false
                    }
                )
            }
        }
    }
}
