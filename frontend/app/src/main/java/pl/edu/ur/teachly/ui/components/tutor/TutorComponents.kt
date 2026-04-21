package pl.edu.ur.teachly.ui.components.tutor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.ui.models.Review
import pl.edu.ur.teachly.ui.models.Tutor

@Composable
fun TutorBioSection(tutor: Tutor) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Text(
                text = tutor.bio,
                style = typography.bodyMedium,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        content()
    }
}

@Composable
fun ReviewList(reviews: List<Review>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        reviews.forEach { review ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, colorScheme.outline),
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
                            style = typography.labelMedium,
                            color = colorScheme.onSurface
                        )
                        Text(
                            "★".repeat(review.rating),
                            style = typography.labelSmall,
                            color = Color(0xFFD97706)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        review.text,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

