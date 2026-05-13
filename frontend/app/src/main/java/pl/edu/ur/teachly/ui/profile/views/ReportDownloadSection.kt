package pl.edu.ur.teachly.ui.profile.views

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val ISO = DateTimeFormatter.ISO_LOCAL_DATE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDownloadSection(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val modes = listOf("Dzień", "Tydzień", "Miesiąc", "Rok")
    var selectedMode by remember { mutableStateOf("Miesiąc") }
    var modeExpanded by remember { mutableStateOf(false) }

    // reference date (for DAY/WEEK), reference month, reference year
    var referenceDate by remember { mutableStateOf(LocalDate.now()) }
    var referenceYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var referenceYear by remember { mutableStateOf(LocalDate.now().year) }

    var showPicker by remember { mutableStateOf(false) }

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
                val label = referenceYearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("pl")) +
                        " ${referenceYearMonth.year}"
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
                tonalElevation = 6.dp,
            ) {
                when (selectedMode) {
                    "Dzień" -> DayPickerContent(
                        referenceDate = referenceDate,
                        selectedWeekStart = null,
                        onDaySelected = { referenceDate = it; showPicker = false },
                        onDismiss = { showPicker = false },
                    )
                    "Tydzień" -> DayPickerContent(
                        referenceDate = referenceDate,
                        selectedWeekStart = referenceDate.with(DayOfWeek.MONDAY),
                        onDaySelected = { referenceDate = it; showPicker = false },
                        onDismiss = { showPicker = false },
                    )
                    "Miesiąc" -> MonthPickerContent(
                        current = referenceYearMonth,
                        onSelected = { referenceYearMonth = it; showPicker = false },
                        onDismiss = { showPicker = false },
                    )
                    "Rok" -> YearPickerContent(
                        currentYear = referenceYear,
                        onSelected = { referenceYear = it; showPicker = false },
                        onDismiss = { showPicker = false },
                    )
                }
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Raporty i Statystyki",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Wybor trybu
            ExposedDropdownMenuBox(
                expanded = modeExpanded,
                onExpandedChange = { modeExpanded = !modeExpanded },
            ) {
                OutlinedTextField(
                    value = selectedMode,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Typ zakresu") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(expanded = modeExpanded, onDismissRequest = { modeExpanded = false }) {
                    modes.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode) },
                            onClick = { selectedMode = mode; modeExpanded = false },
                        )
                    }
                }
            }

            // Przycisk wyboru okresu
            OutlinedButton(
                onClick = { showPicker = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Wybierz okres")
            }

            // Podglad zakresu
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = rangeLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                )
            }

            Button(
                onClick = {
                    viewModel.downloadReport(ISO.format(startDate), ISO.format(endDate)) { result ->
                        result.onSuccess { file ->
                            Toast.makeText(context, "Zapisano: ${file.name}", Toast.LENGTH_LONG).show()
                            openPdfFile(context, file)
                        }
                        result.onFailure {
                            Toast.makeText(context, "Błąd: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Pobierz PDF")
            }
        }
    }
}

// ---- PICKER: DZIEŃ / TYDZIEŃ ----
@Composable
private fun DayPickerContent(
    referenceDate: LocalDate,
    selectedWeekStart: LocalDate?,         // null = tryb dnia, non-null = tryb tygodnia
    onDaySelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    var displayMonth by remember { mutableStateOf(YearMonth.from(referenceDate)) }
    val today = LocalDate.now()
    val weekStart = selectedWeekStart

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Nagłówek miesiąc / rok
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { displayMonth = displayMonth.minusMonths(1) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null)
            }
            Text(
                text = displayMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("pl"))
                        .replaceFirstChar { it.uppercase() } + " ${displayMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val day = displayMonth.atDay(dayNum)
                        val isToday = day == today
                        val isSelected = day == referenceDate
                        val isInWeek = weekStart != null && !day.isBefore(weekStart) && !day.isAfter(weekStart.plusDays(6))
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
                                    if (isToday && !isSelected) Modifier.border(1.dp, primary, RoundedCornerShape(4.dp))
                                    else Modifier
                                )
                                .clickable { onDaySelected(day) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$dayNum",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected || isInWeek) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    isInWeek -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
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
private fun MonthPickerContent(
    current: YearMonth,
    onSelected: (YearMonth) -> Unit,
    onDismiss: () -> Unit,
) {
    var year by remember { mutableStateOf(current.year) }
    val monthNames = (1..12).map {
        java.time.Month.of(it).getDisplayName(TextStyle.SHORT_STANDALONE, Locale("pl"))
            .replaceFirstChar { c -> c.uppercase() }
    }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
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
                    modifier = Modifier.padding(4.dp).clickable { onSelected(ym) },
                ) {
                    Text(
                        text = monthNames[idx],
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Anuluj") }
    }
}

// ---- PICKER: ROK ----
@Composable
private fun YearPickerContent(
    currentYear: Int,
    onSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
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
                    modifier = Modifier.padding(4.dp).clickable { onSelected(yr) },
                ) {
                    Text(
                        text = "$yr",
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
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
            file,
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
