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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.ReviewResponse
import pl.edu.ur.teachly.ui.components.other.formatDate
import pl.edu.ur.teachly.ui.review.views.StarRatingDisplay
import java.time.LocalDate

@Composable
fun ReviewAdminCard(review: ReviewResponse, onDelete: () -> Unit) {
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
                StarRatingDisplay(rating = review.rating)
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Usuń opinię",
                        tint = colorScheme.error,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.School,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary,
                    )
                },
                text = "Korepetytor: ${review.tutorFirstName} ${review.tutorLastName}",
            )

            Spacer(Modifier.height(4.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Person,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary,
                    )
                },
                text = "Uczeń: ${review.studentFirstName} ${review.studentLastName}",
            )

            if (!review.comment.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = review.comment,
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            val createdAtFormatted = "${formatDate(LocalDate.parse(review.createdAt.take(10)))} " +
                    review.createdAt.substring(11, 16)
            val updatedAtFormatted = "${formatDate(LocalDate.parse(review.updatedAt.take(10)))} " +
                    review.updatedAt.substring(11, 16)
            Text(
                text = "Dodana: $createdAtFormatted",
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Edytowana: $updatedAtFormatted",
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
    }
}
