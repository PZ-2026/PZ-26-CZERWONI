package pl.edu.ur.teachly.ui.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.data.model.SubjectResponse
import pl.edu.ur.teachly.data.model.TutorSubjectResponse
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.ErrorBanner
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.dialog.DialogSectionLabel
import pl.edu.ur.teachly.ui.components.other.dialog.DialogSwitchRow
import pl.edu.ur.teachly.ui.profile.viewmodels.TutorSetupViewModel

@Composable
fun TutorSetupScreen(
    tutorId: Int,
    onBack: (() -> Unit)?,
    onDone: () -> Unit,
    viewModel: TutorSetupViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showAddSubjectDialog by rememberSaveable { mutableStateOf(false) }
    val isFormValid = state.hourlyRate.toDoubleOrNull()?.let { it > 0 } == true

    LaunchedEffect(tutorId) { viewModel.load(tutorId) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            viewModel.clearSaved()
            onDone()
        }
    }

    if (showAddSubjectDialog) {
        AddSubjectDialog(
            availableSubjects = state.availableSubjects,
            alreadyAddedSubjectIds = state.currentSubjects.map { it.subjectId }.toSet(),
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { subjectId, lPrimary, lHighSchool, lUniversity, lExamPrep, lProfessional ->
                viewModel.addSubject(subjectId, lPrimary, lHighSchool, lUniversity, lExamPrep, lProfessional)
                showAddSubjectDialog = false
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
    ) {
        AppHeader(
            title = "Profil korepetytora",
            subtitle = "Uzupełnij swoje dane",
            background = HeaderBackground.Diagonal(
                listOf(colorScheme.onPrimaryContainer, colorScheme.primary)
            ),
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- Podstawowe dane ---
            SectionHeader("Informacje podstawowe")

            OutlinedTextField(
                value = state.bio,
                onValueChange = { if (it.length <= 2000) viewModel.onBioChange(it) },
                label = { Text("Opis (bio)") },
                placeholder = { Text("Napisz coś o sobie, swoim doświadczeniu...") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                supportingText = {
                    Text(
                        text = "${state.bio.length}/2000",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )
                },
            )

            OutlinedTextField(
                value = state.hourlyRate,
                onValueChange = viewModel::onHourlyRateChange,
                label = { Text("Stawka godzinowa (PLN)") },
                placeholder = { Text("np. 80") },
                leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.hourlyRate.isNotBlank() && (state.hourlyRate.toDoubleOrNull() == null || state.hourlyRate.toDoubleOrNull()!! <= 0),
                supportingText = if (state.hourlyRate.isNotBlank() && !isFormValid) {
                    { Text("Podaj prawidłową stawkę większą niż 0") }
                } else null,
            )

            // --- Forma zajęć ---
            SectionHeader("Forma zajęć")

            DialogSwitchRow(
                label = "Zajęcia online",
                checked = state.offersOnline,
                onCheckedChange = viewModel::onOffersOnlineChange,
            )
            DialogSwitchRow(
                label = "Zajęcia stacjonarne",
                checked = state.offersInPerson,
                onCheckedChange = viewModel::onOffersInPersonChange,
            )

            // --- Przedmioty ---
            SectionHeader("Prowadzone przedmioty")

            if (state.currentSubjects.isEmpty()) {
                Text(
                    text = "Nie dodano jeszcze żadnych przedmiotów.",
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            } else {
                Surface(
                    shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        state.currentSubjects.forEachIndexed { index, subject ->
                            SubjectRow(
                                subject = subject,
                                onRemove = { viewModel.removeSubject(subject.id) },
                            )
                            if (index < state.currentSubjects.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = { showAddSubjectDialog = true },
                modifier = Modifier.align(Alignment.Start),
                enabled = state.availableSubjects.isNotEmpty(),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Dodaj przedmiot", modifier = Modifier.padding(start = 4.dp))
            }

            if (state.error != null) {
                ErrorBanner(message = state.error!!)
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Zapisz profil",
                onClick = { viewModel.saveProfile() },
                isLoading = state.isSaving,
                enabled = isFormValid,
                modifier = Modifier.padding(bottom = 32.dp, top = 8.dp),
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun SubjectRow(
    subject: TutorSubjectResponse,
    onRemove: () -> Unit,
) {
    val levels = buildList {
        if (subject.levelPrimary == true) add("Podstawówka")
        if (subject.levelHighSchool == true) add("Liceum")
        if (subject.levelUniversity == true) add("Studia")
        if (subject.levelExamPrep == true) add("Egzaminy")
        if (subject.levelProfessional == true) add("Zawodowe")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject.subjectName,
                style = typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            if (levels.isNotEmpty()) {
                Text(
                    text = levels.joinToString(", "),
                    style = typography.labelSmall,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Usuń przedmiot",
                tint = colorScheme.error,
            )
        }
    }
}

@Composable
private fun AddSubjectDialog(
    availableSubjects: List<SubjectResponse>,
    alreadyAddedSubjectIds: Set<Int>,
    onDismiss: () -> Unit,
    onConfirm: (Int, Boolean, Boolean, Boolean, Boolean, Boolean) -> Unit,
) {
    val notAdded = availableSubjects.filter { it.id !in alreadyAddedSubjectIds }
    var selectedSubject by remember { mutableStateOf(notAdded.firstOrNull()) }
    var levelPrimary by remember { mutableStateOf(false) }
    var levelHighSchool by remember { mutableStateOf(false) }
    var levelUniversity by remember { mutableStateOf(false) }
    var levelExamPrep by remember { mutableStateOf(false) }
    var levelProfessional by remember { mutableStateOf(false) }

    val atLeastOneLevel = levelPrimary || levelHighSchool || levelUniversity || levelExamPrep || levelProfessional

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj przedmiot") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (notAdded.isEmpty()) {
                    Text(
                        text = "Wszystkie dostępne przedmioty zostały już dodane.",
                        style = typography.bodyMedium,
                    )
                    return@Column
                }

                DialogSectionLabel("Przedmiot")
                SubjectDropdown(
                    subjects = notAdded,
                    selected = selectedSubject,
                    onSelect = { selectedSubject = it },
                )

                Spacer(modifier = Modifier.height(4.dp))
                DialogSectionLabel("Poziomy nauczania (wybierz co najmniej jeden)")

                LevelCheckRow("Szkoła podstawowa", levelPrimary) { levelPrimary = it }
                LevelCheckRow("Liceum / technikum", levelHighSchool) { levelHighSchool = it }
                LevelCheckRow("Studia", levelUniversity) { levelUniversity = it }
                LevelCheckRow("Przygotowanie do egzaminów", levelExamPrep) { levelExamPrep = it }
                LevelCheckRow("Szkolenie zawodowe", levelProfessional) { levelProfessional = it }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val s = selectedSubject ?: return@TextButton
                    onConfirm(s.id, levelPrimary, levelHighSchool, levelUniversity, levelExamPrep, levelProfessional)
                },
                enabled = selectedSubject != null && atLeastOneLevel,
            ) { Text("Dodaj") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } },
    )
}

@Composable
private fun LevelCheckRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(
            text = label,
            style = typography.bodyMedium,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectDropdown(
    subjects: List<SubjectResponse>,
    selected: SubjectResponse?,
    onSelect: (SubjectResponse) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
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
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            subjects.forEach { subject ->
                DropdownMenuItem(
                    text = { Text("${subject.subjectName} (${subject.categoryName})") },
                    onClick = {
                        onSelect(subject)
                        expanded = false
                    },
                )
            }
        }
    }
}
