package pl.edu.ur.teachly.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.OutlinedCard
import pl.edu.ur.teachly.ui.components.PrimaryButton
import pl.edu.ur.teachly.ui.theme.DeepGreen700

@Composable
fun BookingConfirmScreen(
    bookingId   : String,
    scheduledAt : String,
    onGoHome    : () -> Unit,
) {
    // TODO: pobierz szczegóły rezerwacji z ViewModelu po bookingId
    //       Na razie używamy mock result dla podglądu
    val result = BookingResult(
        tutor           = MOCK_TUTORS[0],
        day             = CALENDAR_DAYS[1],
        timeSlot        = "11:00",
        durationMinutes = 60,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SuccessIcon()
        Spacer(Modifier.height(24.dp))
        ConfirmHeadline(tutorName = result.tutor.name)
        Spacer(Modifier.height(32.dp))
        BookingSummaryCard(result = result)
        Spacer(Modifier.height(32.dp))
        PrimaryButton(
            text    = stringResource(R.string.confirm_go_home),
            onClick = onGoHome,
        )
    }
}

// Ikona sukcesu

@Composable
private fun SuccessIcon() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(DeepGreen700.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Text("✓", style = MaterialTheme.typography.displaySmall, color = DeepGreen700)
    }
}

// Nagłówek potwierdzenia

@Composable
private fun ConfirmHeadline(tutorName: String) {
    Text(
        text  = stringResource(R.string.confirm_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text      = stringResource(R.string.confirm_subtitle, tutorName),
        style     = MaterialTheme.typography.bodyMedium,
        color     = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

// Karta z podsumowaniem

@Composable
private fun BookingSummaryCard(result: BookingResult) {
    val rows = listOf(
        Triple("📅", stringResource(R.string.summary_date),     "${result.day.shortName} ${result.day.dayNumber} marca 2026"),
        Triple("🕐", stringResource(R.string.summary_time),     result.timeSlot),
        Triple("⏱",  stringResource(R.string.summary_duration), "${result.durationMinutes} min"),
        Triple("👩‍🏫", stringResource(R.string.summary_tutor),    result.tutor.name),
        Triple("💰", stringResource(R.string.summary_price),    "${result.totalPrice} zł"),
    )

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        rows.forEachIndexed { index, (icon, label, value) ->
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(icon,  style = MaterialTheme.typography.bodySmall)
                    Text(label, style = MaterialTheme.typography.bodySmall,   color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(value, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            if (index < rows.lastIndex) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
