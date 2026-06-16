package pl.edu.ur.teachly.ui.profile.views

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// ---- PICKER: DZIEŃ / TYDZIEŃ ----
@Composable
internal fun DayPickerContent(
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
internal fun MonthPickerContent(current: YearMonth, onSelected: (YearMonth) -> Unit, onDismiss: () -> Unit) {
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
internal fun YearPickerContent(currentYear: Int, onSelected: (Int) -> Unit, onDismiss: () -> Unit) {
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

/** Otwiera wygenerowany plik PDF w zewnętrznej aplikacji przez [FileProvider]. */
internal fun openPdfFile(context: Context, file: File) {
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
