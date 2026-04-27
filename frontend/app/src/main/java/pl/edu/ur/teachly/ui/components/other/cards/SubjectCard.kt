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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import pl.edu.ur.teachly.data.model.SubjectResponse

@Composable
fun SubjectCard(subject: SubjectResponse, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                    text = subject.subjectName,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edytuj",
                            tint = colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Usuń",
                            tint = colorScheme.error
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            CardInfoRow(
                icon = {
                    Icon(
                        Icons.Default.Category,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.primary
                    )
                },
                text = "Kategoria: ${subject.categoryName}",
            )
        }
    }
}