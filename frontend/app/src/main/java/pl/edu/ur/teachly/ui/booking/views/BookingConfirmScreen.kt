package pl.edu.ur.teachly.ui.booking.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.OutlinedCard
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.theme.DeepGreen700

@Composable
fun BookingConfirmScreen(
    tutorName: String,
    subjectName: String,
    lessonDate: String,
    timeFrom: String,
    timeTo: String,
    amount: String,
    onGoHome: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SuccessIcon()
        Spacer(Modifier.height(24.dp))
        ConfirmHeadline(tutorName = tutorName)
        Spacer(Modifier.height(32.dp))
        BookingSummaryCard(
            tutorName = tutorName,
            subjectName = subjectName,
            lessonDate = lessonDate,
            timeFrom = timeFrom,
            timeTo = timeTo,
            amount = amount,
        )
        Spacer(Modifier.height(32.dp))
        PrimaryButton(
            text = stringResource(R.string.confirm_go_home),
            onClick = onGoHome,
        )
    }
}

@Composable
private fun SuccessIcon() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(DeepGreen700.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Icons.Default.Check
    }
}

@Composable
private fun ConfirmHeadline(tutorName: String) {
    Text(
        text = stringResource(R.string.confirm_title),
        style = typography.headlineMedium,
        color = colorScheme.onBackground,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.confirm_subtitle, tutorName),
        style = typography.bodyMedium,
        color = colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun BookingSummaryCard(
    tutorName: String,
    subjectName: String,
    lessonDate: String,
    timeFrom: String,
    timeTo: String,
    amount: String,
) {
    val rows = listOf(
        listOf(stringResource(R.string.summary_date), lessonDate),
        listOf(stringResource(R.string.summary_time), "$timeFrom – $timeTo"),
        listOf("Przedmiot", subjectName),
        listOf(stringResource(R.string.summary_tutor), tutorName),
        listOf(stringResource(R.string.summary_price), "$amount zł"),
    )

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        rows.forEachIndexed { index, (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        label,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    value,
                    style = typography.labelMedium,
                    color = colorScheme.onSurface,
                )
            }
            if (index < rows.lastIndex) {
                HorizontalDivider(color = colorScheme.outline)
            }
        }
    }
}
