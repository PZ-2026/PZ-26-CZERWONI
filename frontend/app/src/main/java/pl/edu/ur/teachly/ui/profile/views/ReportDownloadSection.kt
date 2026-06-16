package pl.edu.ur.teachly.ui.profile.views

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel

private val ISO = DateTimeFormatter.ISO_LOCAL_DATE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDownloadSection(viewModel: ProfileViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val profileState by viewModel.profile.collectAsState()
    val role = profileState.role

    val modes = listOf("Dzień", "Tydzień", "Miesiąc", "Rok")
    var selectedMode by remember { mutableStateOf("Miesiąc") }
    var modeExpanded by remember { mutableStateOf(false) }

    // Reference dates
    var referenceDate by remember { mutableStateOf(LocalDate.now()) }
    var referenceYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var referenceYear by remember { mutableStateOf(LocalDate.now().year) }

    var showPicker by remember { mutableStateOf(false) }

    // Report Type choices per user role
    val reportTypes = remember(role) { reportTypesFor(role) }

    var selectedReportKey by remember(role) { mutableStateOf("LESSONS") }
    var reportTypeExpanded by remember { mutableStateOf(false) }

    // Dynamically available fields for checkbox selections
    val availableFields = remember(role, selectedReportKey) {
        availableFieldsFor(role, selectedReportKey)
    }

    var selectedFields by remember(availableFields) {
        mutableStateOf(availableFields.map { it.second }.toSet())
    }

    val (startDate, endDate, rangeLabel) = remember(selectedMode, referenceDate, referenceYearMonth, referenceYear) {
        when (selectedMode) {
            "Dzień" -> {
                Triple(referenceDate, referenceDate, "Dzień: ${ISO.format(referenceDate)}")
            }

            "Tydzień" -> {
                val start = referenceDate.with(DayOfWeek.MONDAY)
                val end = start.plusDays(6)
                Triple(start, end, "Zakres: ${ISO.format(start)} – ${ISO.format(end)}")
            }

            "Miesiąc" -> {
                val start = referenceYearMonth.atDay(1)
                val end = referenceYearMonth.atEndOfMonth()
                val label =
                    referenceYearMonth.month.getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.forLanguageTag("pl")
                    ) + " ${referenceYearMonth.year}"
                Triple(start, end, "Zakres: $label")
            }

            "Rok" -> {
                val start = LocalDate.of(referenceYear, 1, 1)
                val end = LocalDate.of(referenceYear, 12, 31)
                Triple(start, end, "Zakres: $referenceYear")
            }

            else -> Triple(referenceDate, referenceDate, "Dzień: ${ISO.format(referenceDate)}")
        }
    }

    // ---- DIALOGI WYBORU ----
    if (showPicker) {
        Dialog(onDismissRequest = { showPicker = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 6.dp
            ) {
                when (selectedMode) {
                    "Dzień" -> DayPickerContent(
                        referenceDate = referenceDate,
                        selectedWeekStart = null,
                        onDaySelected = {
                            referenceDate = it
                            showPicker = false
                        },
                        onDismiss = { showPicker = false }
                    )

                    "Tydzień" -> DayPickerContent(
                        referenceDate = referenceDate,
                        selectedWeekStart = referenceDate.with(DayOfWeek.MONDAY),
                        onDaySelected = {
                            referenceDate = it
                            showPicker = false
                        },
                        onDismiss = { showPicker = false }
                    )

                    "Miesiąc" -> MonthPickerContent(
                        current = referenceYearMonth,
                        onSelected = {
                            referenceYearMonth = it
                            showPicker = false
                        },
                        onDismiss = { showPicker = false }
                    )

                    "Rok" -> YearPickerContent(
                        currentYear = referenceYear,
                        onSelected = {
                            referenceYear = it
                            showPicker = false
                        },
                        onDismiss = { showPicker = false }
                    )
                }
            }
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val currentReportTypeName = reportTypes.firstOrNull { it.first == selectedReportKey }?.second ?: ""
    val collapsedSummary = remember(currentReportTypeName, rangeLabel) {
        val period = rangeLabel.removePrefix("Zakres: ").removePrefix("Dzień: ")
        "$currentReportTypeName · $period"
    }

    var sectionExpanded by rememberSaveable { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (sectionExpanded) 180f else 0f,
        animationSpec = tween(200),
        label = "reportSectionChevron"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .clickable(role = Role.Button) { sectionExpanded = !sectionExpanded }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Raporty i statystyki",
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )
                        if (!sectionExpanded) {
                            Text(
                                text = collapsedSummary,
                                style = typography.bodySmall,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (sectionExpanded) "Zwiń" else "Rozwiń",
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(chevronRotation)
                    )
                }

                AnimatedVisibility(
                    visible = sectionExpanded,
                    enter = expandVertically(animationSpec = tween(200)) + fadeIn(animationSpec = tween(200)),
                    exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(animationSpec = tween(150))
                ) {
                    Column {
                        HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.25f))
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = reportTypeExpanded,
                                onExpandedChange = { reportTypeExpanded = !reportTypeExpanded }
                            ) {
                                OutlinedTextField(
                                    value = currentReportTypeName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Typ raportu") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = reportTypeExpanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = reportTypeExpanded,
                                    onDismissRequest = { reportTypeExpanded = false }
                                ) {
                                    reportTypes.forEach { (key, name) ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                selectedReportKey = key
                                                reportTypeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Zawartość raportu",
                                    style = typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurface
                                )
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = colorScheme.background,
                                    border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(0.dp)
                                    ) {
                                        availableFields.forEach { (label, key) ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        selectedFields = if (selectedFields.contains(key)) {
                                                            selectedFields - key
                                                        } else {
                                                            selectedFields + key
                                                        }
                                                    }
                                                    .padding(vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = selectedFields.contains(key),
                                                    onCheckedChange = { checked ->
                                                        selectedFields = if (checked) {
                                                            selectedFields + key
                                                        } else {
                                                            selectedFields - key
                                                        }
                                                    }
                                                )
                                                Text(
                                                    text = label,
                                                    style = typography.bodyMedium,
                                                    color = colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.35f))

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Okres raportu",
                                    style = typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurface
                                )

                                ExposedDropdownMenuBox(
                                    expanded = modeExpanded,
                                    onExpandedChange = { modeExpanded = !modeExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedMode,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Typ zakresu dat") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded)
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                                    )
                                    ExposedDropdownMenu(expanded = modeExpanded, onDismissRequest = {
                                        modeExpanded =
                                            false
                                    }) {
                                        modes.forEach { mode ->
                                            DropdownMenuItem(
                                                text = { Text(mode) },
                                                onClick = {
                                                    selectedMode = mode
                                                    modeExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    onClick = { showPicker = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    color = colorScheme.surface,
                                    border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.45f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = colorScheme.primary.copy(alpha = 0.12f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday,
                                                contentDescription = null,
                                                tint = colorScheme.primary,
                                                modifier = Modifier
                                                    .padding(10.dp)
                                                    .size(20.dp)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Wybrany okres",
                                                style = typography.labelSmall,
                                                color = colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = rangeLabel.removePrefix("Zakres: ").removePrefix("Dzień: "),
                                                style = typography.bodyLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = colorScheme.onSurface
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = null,
                                            tint = colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            PrimaryButton(
                                text = "Pobierz PDF",
                                onClick = {
                                    viewModel.downloadReport(
                                        startDate = ISO.format(startDate),
                                        endDate = ISO.format(endDate),
                                        type = selectedReportKey,
                                        includeFields = selectedFields.toList()
                                    ) { result ->
                                        result.onSuccess { file ->
                                            Toast.makeText(context, "Zapisano: ${file.name}", Toast.LENGTH_LONG).show()
                                            openPdfFile(context, file)
                                        }
                                        result.onFailure {
                                            Toast.makeText(context, "Błąd: ${it.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
