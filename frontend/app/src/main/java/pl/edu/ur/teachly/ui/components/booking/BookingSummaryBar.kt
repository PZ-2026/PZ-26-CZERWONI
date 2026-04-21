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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.models.CalendarDay
import kotlin.math.roundToInt

@Composable
fun BookingSummaryBar(
    tutorName: String,
    pricePerHour: Int,
    selectedDay: CalendarDay?,
    selectedSlot: String?,
    selectedDuration: Int,
    isSubmitting: Boolean = false,
    onConfirm: () -> Unit,
) {
    Surface(color = colorScheme.surface, shadowElevation = 8.dp) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            AnimatedVisibility(
                visible = selectedSlot != null && selectedDay != null,
                enter = fadeIn(tween(200)) + expandVertically(),
                exit = fadeOut(tween(150)) + shrinkVertically(),
            ) {
                val day = selectedDay
                val price = (pricePerHour * selectedDuration / 60.0).roundToInt()
                if (day != null && selectedSlot != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                stringResource(
                                    R.string.booking_summary_time,
                                    day.shortName,
                                    day.dayNumber,
                                    selectedSlot,
                                    selectedDuration
                                ),
                                style = typography.bodySmall,
                                color = colorScheme.onSurfaceVariant,
                            )
                            Text(
                                tutorName,
                                style = typography.labelSmall,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                        Text(
                            stringResource(R.string.price_value, price),
                            style = typography.headlineSmall,
                            color = colorScheme.primary,
                        )
                    }
                }
            }

            PrimaryButton(
                text = if (selectedSlot != null) stringResource(R.string.booking_cta_confirm)
                else stringResource(R.string.booking_cta_pick),
                onClick = onConfirm,
                enabled = selectedSlot != null && !isSubmitting,
                isLoading = isSubmitting,
            )
        }
    }
}

