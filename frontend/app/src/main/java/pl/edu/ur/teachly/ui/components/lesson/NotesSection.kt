package pl.edu.ur.teachly.ui.components.lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.LessonResponse

@Composable
fun NotesSection(
    lesson: LessonResponse,
    isStudent: Boolean,
    isTutor: Boolean,
    isSaving: Boolean,
    onSaveStudentNotes: (String) -> Unit,
    onSaveTutorNotes: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outline),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Notatki",
                style = typography.titleSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = colorScheme.onSurface,
            )

            // Student notes
            NoteField(
                label = "Notatki ucznia",
                initialValue = lesson.studentNotes ?: "",
                editable = isStudent,
                isSaving = isSaving,
                onSave = onSaveStudentNotes,
            )

            // Tutor notes
            NoteField(
                label = "Notatki korepetytora",
                initialValue = lesson.tutorNotes ?: "",
                editable = isTutor,
                isSaving = isSaving,
                onSave = onSaveTutorNotes,
            )
        }
    }
}

@Composable
fun NoteField(
    label: String,
    initialValue: String,
    editable: Boolean,
    isSaving: Boolean,
    onSave: (String) -> Unit,
) {
    var text by rememberSaveable(initialValue) { mutableStateOf(initialValue.take(500)) }
    val isDirty = text != initialValue

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = typography.labelMedium,
            color = colorScheme.onSurfaceVariant,
        )
        if (editable) {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 500) text = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 10,
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Wpisz notatki…") },
                supportingText = {
                    Text(
                        text = "${text.length}/500",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )
                },
            )
            if (isDirty) {
                Button(
                    onClick = { onSave(text) },
                    enabled = !isSaving,
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(if (isSaving) "Zapisywanie…" else "Zapisz")
                }
            }
        } else {
            if (initialValue.isBlank()) {
                Text(
                    text = "Brak notatek",
                    style = typography.bodyMedium,
                    color = colorScheme.onBackground.copy(alpha = 0.4f),
                )
            } else {
                Text(
                    text = initialValue,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
