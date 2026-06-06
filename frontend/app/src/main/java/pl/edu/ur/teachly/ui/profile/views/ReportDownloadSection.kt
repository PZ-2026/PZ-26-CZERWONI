package pl.edu.ur.teachly.ui.profile.views

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import pl.edu.ur.teachly.data.model.UserRole
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
    val reportTypes = remember(role) {
        when (role) {
            UserRole.STUDENT -> listOf(
                "LESSONS" to "Historia lekcji",
                "EXPENSES" to "Podsumowanie wydatków",
                "ANALYTICS" to "Czas nauki i analiza"
            )

            UserRole.TUTOR -> listOf(
                "LESSONS" to "Historia zajęć",
                "REVENUE" to "Podsumowanie przychodów",
                "STUDENTS" to "Analiza uczniów"
            )

            UserRole.ADMIN -> listOf(
                "LESSONS" to "Wszystkie lekcje platformy",
                "REVENUE" to "Obrót finansowy platformy",
                "USERS" to "Analiza zarejestrowanych kont"
            )
        }
    }

    var selectedReportKey by remember(role) {
        mutableStateOf(
            when (role) {
                UserRole.STUDENT -> "LESSONS"
                UserRole.TUTOR -> "LESSONS"
                UserRole.ADMIN -> "LESSONS"
            }
        )
    }
    var reportTypeExpanded by remember { mutableStateOf(false) }

    // Dynamically available fields for checkbox selections
    val availableFields = remember(role, selectedReportKey) {
        val list = mutableListOf<Pair<String, String>>()
        when (selectedReportKey) {
            "LESSONS" -> {
                list.add("Data" to "date")
                list.add("Czas" to "time")
                list.add("Przedmiot" to "subject")
                if (role == UserRole.STUDENT) {
                    list.add("Cena" to "price")
                } else {
                    list.add("Zarobki" to "price")
                }
                list.add("Statusy lekcji" to "status")
                if (role == UserRole.STUDENT || role == UserRole.ADMIN) {
                    list.add("Dane korepetytora" to "tutor")
                }
                if (role == UserRole.TUTOR || role == UserRole.ADMIN) {
                    list.add("Dane ucznia" to "student")
                }
            }

            "REVENUE" -> {
                list.add("Przedmiot" to "subject")
                list.add("Zarobki" to "price")
                list.add("Liczba lekcji" to "status")
                list.add("Wykresy i wizualizacje" to "charts")
            }

            "EXPENSES" -> {
                list.add("Przedmiot" to "subject")
                list.add("Kwota" to "price")
                list.add("Korepetytor" to "tutor")
                list.add("Data" to "date")
                list.add("Wykresy i wizualizacje" to "charts")
            }

            "ANALYTICS" -> {
                list.add("Korepetytor" to "tutor")
                list.add("Przedmiot" to "subject")
                list.add("Czas nauki" to "status")
                list.add("Wykresy i wizualizacje" to "charts")
            }

            "STUDENTS" -> {
                list.add("Dane ucznia" to "student")
                list.add("Przedmiot" to "subject")
                list.add("Przeprowadzone lekcje" to "status")
                list.add("Wykresy i wizualizacje" to "charts")
            }

            "USERS" -> {
                list.add("Tabela użytkowników" to "student")
                list.add("Wykresy i wizualizacje" to "charts")
            }
        }
        list
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reportTypeExpanded) },
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
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(expanded = modeExpanded, onDismissRequest = { modeExpanded = false }) {
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

// ---- PICKER: DZIEŃ / TYDZIEŃ ----
@Composable
private fun DayPickerContent(
    referenceDate: LocalDate,
    selectedWeekStart: LocalDate?, // null = tryb dnia, non-null = tryb tygodnia
    onDaySelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var displayMonth by remember { mutableStateOf(YearMonth.from(referenceDate)) }
    val today = LocalDate.now()
    val weekStart = selectedWeekStart

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Nagłówek miesiąc / rok
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { displayMonth = displayMonth.minusMonths(1) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null)
            }
            Text(
                text = displayMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("pl"))
                    .replaceFirstChar { it.uppercase() } + " ${displayMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { displayMonth = displayMonth.plusMonths(1) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }

        // Nagłówki dni tygodnia
        val dayNames = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "So", "Nd")
        Row(modifier = Modifier.fillMaxWidth()) {
            dayNames.forEach { name ->
                Text(
                    text = name,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Generowanie komórek
        val firstDayOfMonth = displayMonth.atDay(1)
        val startOffset = firstDayOfMonth.dayOfWeek.value - 1 // Mon=0
        val daysInMonth = displayMonth.lengthOfMonth()
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - startOffset + 1
                    if (dayNum < 1 || dayNum > daysInMonth) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    } else {
                        val day = displayMonth.atDay(dayNum)
                        val isToday = day == today
                        val isSelected = day == referenceDate
                        val isInWeek =
                            weekStart != null && !day.isBefore(weekStart) && !day.isAfter(weekStart.plusDays(6))
                        val primary = MaterialTheme.colorScheme.primary
                        val primaryContainer = MaterialTheme.colorScheme.primaryContainer

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(if (isSelected) CircleShape else RoundedCornerShape(4.dp))
                                .background(
                                    when {
                                        isSelected -> primary
                                        isInWeek -> primaryContainer
                                        else -> Color.Transparent
                                    }
                                )
                                .then(
                                    if (isToday && !isSelected) {
                                        Modifier.border(1.dp, primary, RoundedCornerShape(4.dp))
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { onDaySelected(day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$dayNum",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected || isInWeek) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    isInWeek -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
            Text("Anuluj")
        }
    }
}

// ---- PICKER: MIESIĄC ----
@Composable
private fun MonthPickerContent(current: YearMonth, onSelected: (YearMonth) -> Unit, onDismiss: () -> Unit) {
    var year by remember { mutableStateOf(current.year) }
    val monthNames = (1..12).map {
        java.time.Month.of(it).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.forLanguageTag("pl"))
            .replaceFirstChar { c -> c.uppercase() }
    }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { year-- }) { Icon(Icons.Default.ChevronLeft, contentDescription = null) }
            Text(text = "$year", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { year++ }) { Icon(Icons.Default.ChevronRight, contentDescription = null) }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.height(200.dp)) {
            items(12) { idx ->
                val ym = YearMonth.of(year, idx + 1)
                val isSelected = ym == current
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onSelected(ym) }
                ) {
                    Text(
                        text = monthNames[idx],
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Anuluj") }
    }
}

// ---- PICKER: ROK ----
@Composable
private fun YearPickerContent(currentYear: Int, onSelected: (Int) -> Unit, onDismiss: () -> Unit) {
    val thisYear = LocalDate.now().year
    val years = (thisYear - 5..thisYear + 2).toList()

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Wybierz rok", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.height(160.dp)) {
            items(years.size) { idx ->
                val yr = years[idx]
                val isSelected = yr == currentYear
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onSelected(yr) }
                ) {
                    Text(
                        text = "$yr",
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Anuluj") }
    }
}

private fun openPdfFile(context: android.content.Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Nie znaleziono aplikacji do obsługi PDF", Toast.LENGTH_SHORT).show()
    }
}
