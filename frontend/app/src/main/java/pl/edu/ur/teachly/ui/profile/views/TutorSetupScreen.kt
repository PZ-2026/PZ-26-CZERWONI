package pl.edu.ur.teachly.ui.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.ErrorBanner
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.dialog.AddTutorSubjectDialog
import pl.edu.ur.teachly.ui.components.other.dialog.DialogSwitchRow
import pl.edu.ur.teachly.ui.components.other.dialog.TutorSubjectListRow
import pl.edu.ur.teachly.ui.profile.viewmodels.TutorSetupViewModel
import pl.edu.ur.teachly.ui.theme.headerGradientColors

@Composable
fun TutorSetupScreen(
    tutorId: Int,
    onBack: (() -> Unit)?,
    onDone: () -> Unit,
    viewModel: TutorSetupViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddSubjectDialog by rememberSaveable { mutableStateOf(false) }
    val isFormValid = state.isFormValid
    val hourlyRateError =
        when {
            state.hourlyRate.isBlank() -> null
            state.parsedHourlyRate == null -> stringResource(R.string.tutor_setup_rate_invalid)
            !state.isHourlyRateValid -> stringResource(R.string.tutor_setup_rate_min)
            else -> null
        }

    LaunchedEffect(tutorId) { viewModel.load(tutorId) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            viewModel.clearSaved()
            onDone()
        }
    }

    if (showAddSubjectDialog) {
        AddTutorSubjectDialog(
            availableSubjects = state.availableSubjects,
            alreadyAddedSubjectIds = state.currentSubjects.map { it.subjectId }.toSet(),
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { subjectId, lPrimary, lHighSchool, lUniversity, lExamPrep, lProfessional ->
                viewModel.addSubject(subjectId, lPrimary, lHighSchool, lUniversity, lExamPrep, lProfessional)
                showAddSubjectDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        AppHeader(
            title = "Profil korepetytora",
            subtitle = "Uzupełnij swoje dane",
            background = HeaderBackground.Diagonal(headerGradientColors()),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        textAlign = TextAlign.End
                    )
                }
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
                isError = hourlyRateError != null,
                supportingText = hourlyRateError?.let { error -> { Text(error) } }
            )

            // --- Forma zajęć ---
            SectionHeader("Forma zajęć")

            DialogSwitchRow(
                label = "Zajęcia online",
                checked = state.offersOnline,
                onCheckedChange = viewModel::onOffersOnlineChange
            )
            DialogSwitchRow(
                label = "Zajęcia stacjonarnie",
                checked = state.offersInPerson,
                onCheckedChange = viewModel::onOffersInPersonChange
            )
            if (!state.hasLessonFormat) {
                Text(
                    text = stringResource(R.string.tutor_setup_lesson_format_required),
                    style = typography.bodySmall,
                    color = colorScheme.error,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            if (state.offersInPerson) {
                OutlinedTextField(
                    value = state.city,
                    onValueChange = { if (it.length <= 50) viewModel.onCityChange(it) },
                    label = { Text("Miasto zajęć stacjonarnych") },
                    placeholder = { Text("np. Kraków") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !state.hasCityIfInPerson,
                    supportingText =
                        if (!state.hasCityIfInPerson) {
                            { Text("Podaj miasto (2–50 znaków)") }
                        } else {
                            null
                        }
                )
            }

            // --- Przedmioty ---
            SectionHeader("Prowadzone przedmioty")

            if (state.currentSubjects.isEmpty()) {
                Text(
                    text = stringResource(R.string.tutor_setup_subjects_required),
                    style = typography.bodyMedium,
                    color = colorScheme.error,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Surface(
                    shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        state.currentSubjects.forEachIndexed { index, subject ->
                            TutorSubjectListRow(
                                subject = subject,
                                onRemove = { viewModel.removeSubject(subject.id) }
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
                enabled = state.availableSubjects.isNotEmpty()
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
                modifier = Modifier.padding(bottom = 32.dp, top = 8.dp)
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
        modifier = Modifier.padding(top = 8.dp)
    )
}
