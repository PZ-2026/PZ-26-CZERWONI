package pl.edu.ur.teachly.ui.components.other.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import pl.edu.ur.teachly.ui.models.MINUTE_OPTIONS
import kotlin.math.absoluteValue

@Composable
fun SlottedTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
) {
    var hour by remember { mutableIntStateOf(initialHour) }
    var minute by remember { mutableIntStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            SlottedTimePicker(
                hour = hour,
                minute = minute,
                onHourChange = { hour = it },
                onMinuteChange = { minute = it },
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(hour, minute) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}

@Composable
fun SlottedTimePicker(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
) {
    // Huge number of pages for infinite scroll effect (typical real world hack)
    val countMultiplier = 500
    val hourTotalPages = 24 * countMultiplier
    val minuteTotalPages = MINUTE_OPTIONS.size

    // Starting page remembers chosen hour
    val hourState = rememberPagerState(
        initialPage = (hourTotalPages / 2) + hour,
        pageCount = { hourTotalPages }
    )

    val minuteIndex = if (minute == 30) 1 else 0
    val minuteState = rememberPagerState(
        initialPage = (minuteTotalPages / 2) + minuteIndex,
        pageCount = { minuteTotalPages }
    )

    // Synchronization needed because of infinite scroll effect
    LaunchedEffect(hourState.currentPage) {
        onHourChange(hourState.currentPage % 24)
    }

    LaunchedEffect(minuteState.currentPage) {
        onMinuteChange(MINUTE_OPTIONS[minuteState.currentPage % MINUTE_OPTIONS.size])
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WheelPickerColumn(
            state = hourState,
            label = { page -> "%02d".format(page % 24) }
        )

        Text(":", style = typography.displaySmall, modifier = Modifier.padding(horizontal = 12.dp))

        WheelPickerColumn(
            state = minuteState,
            label = { page -> "%02d".format(MINUTE_OPTIONS[page]) }
        )
    }
}

@SuppressLint("FrequentlyChangingValue")
@Composable
private fun WheelPickerColumn(
    state: PagerState,
    label: (Int) -> String
) {
    VerticalPager(
        state = state,
        modifier = Modifier.width(70.dp),
        flingBehavior = PagerDefaults.flingBehavior(state = state),
        contentPadding = PaddingValues(vertical = 70.dp)
    ) { page ->
        val isSelected = state.currentPage == page

        // Distance from center to animate scale and opacity
        val pageOffset = (
                (state.currentPage - page) + state.currentPageOffsetFraction
                ).absoluteValue

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .graphicsLayer {
                    // Interpolation: 1.0 (center) -> 0.3 (far)
                    alpha = lerp(
                        start = 0.3f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                    // Interpolation: 1.2 (center) -> 0.7 (far)
                    val scale = lerp(
                        start = 0.7f,
                        stop = 1.2f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                    scaleX = scale
                    scaleY = scale
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label(page),
                style = typography.displaySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) colorScheme.primary else colorScheme.onSurface
            )
        }
    }
}
