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
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.components.other.LessonStatusBadge
import pl.edu.ur.teachly.ui.components.other.formatDate
import pl.edu.ur.teachly.ui.models.LessonDetail

@Composable
fun InfoCard(lesson: LessonDetail, userRole: UserRole) {
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
            val personLabel = when (userRole) {
                UserRole.STUDENT -> "Korepetytor"
                UserRole.TUTOR -> "Uczeń"
                UserRole.ADMIN -> "Korepetytor / Uczeń"
            }
            val personName = when (userRole) {
                UserRole.STUDENT -> "${lesson.tutorFirstName} ${lesson.tutorLastName}".trim()
                UserRole.TUTOR -> "${lesson.studentFirstName} ${lesson.studentLastName}".trim()
                UserRole.ADMIN -> "${lesson.tutorFirstName} ${lesson.tutorLastName} / ${lesson.studentFirstName} ${lesson.studentLastName}"
            }
            DetailRow(Icons.Default.Person, "$personLabel: $personName")

            // Date
            DetailRow(Icons.Default.CalendarMonth, formatDate(lesson.lessonDate))

            // Time
            val endTime = lesson.timeFrom.plusMinutes(
                java.time.Duration.between(lesson.timeFrom, lesson.timeTo).toMinutes()
            )
            val durationMin = java.time.Duration.between(lesson.timeFrom, lesson.timeTo).toMinutes()
            DetailRow(
                Icons.Default.Schedule,
                "${lesson.timeFrom} - $endTime | ($durationMin min)"
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
