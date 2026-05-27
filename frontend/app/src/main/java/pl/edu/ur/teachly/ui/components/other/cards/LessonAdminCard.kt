package pl.edu.ur.teachly.ui.components.other.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.LessonResponse
import pl.edu.ur.teachly.ui.components.other.badges.LessonStatusBadge
import pl.edu.ur.teachly.ui.components.other.badges.PaymentStatusBadge
import pl.edu.ur.teachly.ui.components.other.formatDate
import java.time.LocalDate

@Composable
fun LessonAdminCard(lesson: LessonResponse, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = lesson.subjectName,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edytuj",
                        tint = colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Person,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "Korepetytor: ${lesson.tutorFirstName} ${lesson.tutorLastName}",
            )

            Spacer(Modifier.height(6.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.School,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "Uczeń: ${lesson.studentFirstName} ${lesson.studentLastName}",
            )

            Spacer(Modifier.height(6.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = formatDate(LocalDate.parse(lesson.lessonDate)),
            )

            Spacer(Modifier.height(6.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Schedule,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "${lesson.timeFrom.take(5)}–${lesson.timeTo.take(5)}",
            )

            Spacer(Modifier.height(6.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Payments,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "%.2f".format(lesson.amount) + " PLN",
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LessonStatusBadge(lesson.lessonStatus)
                PaymentStatusBadge(lesson.paymentStatus)
            }
        }
    }
}