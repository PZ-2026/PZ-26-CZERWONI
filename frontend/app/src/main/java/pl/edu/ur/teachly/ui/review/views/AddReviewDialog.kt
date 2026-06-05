package pl.edu.ur.teachly.ui.review.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.other.authTextFieldColors
import pl.edu.ur.teachly.ui.components.other.dialog.AppFormDialog
import pl.edu.ur.teachly.ui.components.other.dialog.DialogErrorText
import pl.edu.ur.teachly.ui.components.other.dialog.DialogFieldShape
import pl.edu.ur.teachly.ui.components.other.dialog.DialogSectionCard
import pl.edu.ur.teachly.ui.components.other.dialog.DialogSectionLabel

@Composable
fun AddReviewDialog(
    isLoading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSubmit: (rating: Double, comment: String?) -> Unit,
    initialRating: Double = 0.0,
    initialComment: String = ""
) {
    var selectedRating by remember { mutableDoubleStateOf(initialRating) }
    var comment by remember { mutableStateOf(initialComment) }

    AppFormDialog(
        title = stringResource(R.string.review_dialog_title),
        onDismiss = onDismiss,
        onConfirm = {
            if (selectedRating > 0.0) {
                onSubmit(selectedRating, comment.trim().ifBlank { null })
            }
        },
        confirmText = stringResource(R.string.review_dialog_submit),
        confirmEnabled = selectedRating > 0.0 && !isLoading
    ) {
        DialogSectionCard {
            DialogSectionLabel(stringResource(R.string.review_dialog_rating_label))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { selectedRating = i.toDouble() },
                        tint = if (i <= selectedRating) {
                            colorScheme.primary
                        } else {
                            colorScheme.onSurface.copy(alpha = 0.2f)
                        }
                    )
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = { Text(stringResource(R.string.review_dialog_comment_hint)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = DialogFieldShape,
                colors = authTextFieldColors()
            )

            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            }

            error?.let { DialogErrorText(it) }
        }
    }
}
