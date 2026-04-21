package pl.edu.ur.teachly.ui.components.lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.LessonFormat
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.ui.components.other.LessonStatusBadge
import java.time.LocalTime

@Composable
fun InfoCard(lesson: LessonResponse, isStudent: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.outline),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = lesson.subjectName,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                LessonStatusBadge(lesson.lessonStatus)
            }

            // Person
            val personLabel = if (isStudent) "Korepetytor" else "Uczeń"
            val personName = if (isStudent)
                "${lesson.tutorFirstName} ${lesson.tutorLastName}".trim()
            else
                "${lesson.studentFirstName} ${lesson.studentLastName}".trim()
            DetailRow(Icons.Default.Person, "$personLabel: $personName")

            // Date
            DetailRow(Icons.Default.CalendarMonth, lesson.lessonDate)

            // Time
            val endTime = try {
                LocalTime.parse(lesson.timeFrom).plusMinutes(
                    java.time.Duration.between(
                        LocalTime.parse(lesson.timeFrom),
                        LocalTime.parse(lesson.timeTo),
                    ).toMinutes()
                ).toString().take(5)
            } catch (e: Exception) {
                lesson.timeTo.take(5)
            }
            val durationMin = try {
                java.time.Duration.between(
                    LocalTime.parse(lesson.timeFrom),
                    LocalTime.parse(lesson.timeTo)
                ).toMinutes()
            } catch (e: Exception) {
                0L
            }
            DetailRow(
                Icons.Default.Schedule,
                "${lesson.timeFrom.take(5)} – $endTime | ($durationMin min)"
            )

            // Format
            val formatLabel = when (lesson.format) {
                LessonFormat.ONLINE -> "Online"
                LessonFormat.IN_PERSON -> "Stacjonarnie"
            }
            DetailRow(Icons.Default.School, formatLabel)

            // Amount + payment status
            val payLabel = when (lesson.paymentStatus) {
                PaymentStatus.PAID -> "Opłacone"
                PaymentStatus.PENDING -> "Nieopłacone"
                PaymentStatus.CANCELLED -> "Anulowane"
            }
            DetailRow(Icons.Default.CreditCard, "${lesson.amount} zł | $payLabel")
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = colorScheme.primary)
        Text(text = text, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
    }
}
