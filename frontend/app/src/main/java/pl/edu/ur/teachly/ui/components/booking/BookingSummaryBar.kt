package pl.edu.ur.teachly.ui.components.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.home.CALENDAR_DAYS
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.home.Tutor
import kotlin.math.roundToInt

@Composable
fun BookingSummaryBar(
    tutor: Tutor,
    selectedDayIndex: Int,
    selectedSlot: String?,
    selectedDuration: Int,
    onConfirm: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            AnimatedVisibility(
                visible = selectedSlot != null,
                enter = fadeIn(tween(200)) + expandVertically(),
                exit = fadeOut(tween(150)) + shrinkVertically(),
            ) {
                val day = CALENDAR_DAYS[selectedDayIndex]
                val price = (tutor.pricePerHour * selectedDuration / 60.0).roundToInt()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            "${day.shortName} ${day.dayNumber} marca · $selectedSlot · $selectedDuration min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            tutor.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        "$price zł",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            PrimaryButton(
                text = if (selectedSlot != null) stringResource(R.string.booking_cta_confirm)
                else stringResource(R.string.booking_cta_pick),
                onClick = onConfirm,
                enabled = selectedSlot != null,
            )
        }
    }
}
