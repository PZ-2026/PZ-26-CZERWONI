package pl.edu.ur.teachly.ui.components.tutorDetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.ui.components.other.SectionLabel
import pl.edu.ur.teachly.ui.components.home.Tutor

data class Review(
    val authorName: String,
    val text: String,
    val rating: Int,
)

val MOCK_REVIEWS = listOf(
    Review("Karolina M.", "Świetne wytłumaczenie całkowania, w końcu rozumiem!", 5),
    Review("Bartek W.", "Polecam! Dostałem 5 na maturze dzięki tym lekcjom.", 5),
    Review("Zuzia K.", "Bardzo cierpliwa i pomocna. Widać że lubi uczyć.", 5),
)

@Composable
fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        SectionLabel(text = title, modifier = Modifier.padding(bottom = 12.dp))
        content()
    }
}

@Composable
fun StatsGrid(tutor: Tutor) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        listOf(
            Triple("🎓", "${tutor.lessonCount}", "Lekcji"),
            Triple("⭐", "${tutor.rating}", "Ocena"),
            Triple("🕐", "${tutor.yearsExp}+", "Lat doś."),
            Triple("💬", tutor.responseTime, "Odpowiedź"),
        ).forEach { (icon, value, label) ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)) {
                    Text(icon, style = MaterialTheme.typography.titleSmall)
                    Text(
                        value,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewList(reviews: List<Review>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        reviews.forEach { review ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            review.authorName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "★".repeat(review.rating),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFD97706)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        review.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
