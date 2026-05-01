package pl.edu.ur.teachly.ui.components.lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.teachly.data.model.LessonStatus
import pl.edu.ur.teachly.data.model.PaymentStatus
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.components.other.dialog.ConfirmConfig
import pl.edu.ur.teachly.ui.components.other.dialog.ConfirmDialog
import pl.edu.ur.teachly.ui.models.LessonDetail

@Composable
fun ActionsSection(
    lesson: LessonDetail,
    userRole: UserRole,
    isSaving: Boolean,
    thirtyMinPast: Boolean,
    onChangeStatus: (LessonStatus) -> Unit,
    onMarkPaid: () -> Unit,
    onGoToTutor: () -> Unit,
    onRebook: () -> Unit,
) {
    val status = lesson.lessonStatus
    var pendingConfirm by remember { mutableStateOf<ConfirmConfig?>(null) }

    pendingConfirm?.let { config ->
        ConfirmDialog(config = config, onDismiss = { pendingConfirm = null })
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        if (userRole == UserRole.STUDENT || userRole == UserRole.ADMIN) {
            // Go to tutor profile (always)
            OutlinedButton(
                onClick = onGoToTutor,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Profil korepetytora")
            }

            // Rebook (if cancelled or completed)
            if (status == LessonStatus.CANCELLED || status == LessonStatus.COMPLETED) {
                Button(
                    onClick = onRebook,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Zarezerwuj ponownie")
                }
            }

            // Cancel (if pending or confirmed)
            if (status == LessonStatus.PENDING || status == LessonStatus.CONFIRMED) {
                OutlinedButton(
                    onClick = {
                        pendingConfirm = ConfirmConfig(
                            title = "Anuluj lekcję",
                            message = "Czy na pewno chcesz anulować tę lekcję? Tej operacji nie można cofnąć.",
                            confirmLabel = "Anuluj lekcję",
                            destructive = true,
                            action = { onChangeStatus(LessonStatus.CANCELLED) },
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
                    border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.5f)),
                ) {
                    Text("Anuluj lekcję")
                }
            }
        }

        if (userRole == UserRole.TUTOR || userRole == UserRole.ADMIN) {
            // Accept/reject (if pending)
            if (status == LessonStatus.PENDING) {
                Button(
                    onClick = {
                        pendingConfirm = ConfirmConfig(
                            title = "Zaakceptuj lekcję",
                            message = "Czy na pewno chcesz zaakceptować tę lekcję?",
                            confirmLabel = "Zaakceptuj",
                            action = { onChangeStatus(LessonStatus.CONFIRMED) },
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Zaakceptuj")
                }
                Spacer(Modifier.height(2.dp))
                OutlinedButton(
                    onClick = {
                        pendingConfirm = ConfirmConfig(
                            title = "Odrzuć lekcję",
                            message = "Czy na pewno chcesz odrzucić tę lekcję?",
                            confirmLabel = "Odrzuć",
                            destructive = true,
                            action = { onChangeStatus(LessonStatus.CANCELLED) },
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.error,
                    ),
                    border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.5f)),
                ) {
                    Text("Odrzuć")
                }
            }

            // Cancel (if confirmed)
            if (status == LessonStatus.CONFIRMED) {
                OutlinedButton(
                    onClick = {
                        pendingConfirm = ConfirmConfig(
                            title = "Anuluj lekcję",
                            message = "Czy na pewno chcesz anulować potwierdzoną lekcję?",
                            confirmLabel = "Anuluj lekcję",
                            destructive = true,
                            action = { onChangeStatus(LessonStatus.CANCELLED) },
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
                    border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.5f)),
                ) {
                    Text("Anuluj lekcję")
                }
            }

            // Mark completed. Must be confirmed + 30min past start
            if (status == LessonStatus.CONFIRMED && thirtyMinPast) {
                Button(
                    onClick = { onChangeStatus(LessonStatus.COMPLETED) },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Oznacz jako zakończoną")
                }
            }

            // Mark paid. Must be completed or confirmed + 30min past AND not already paid
            val canMarkPaid = lesson.paymentStatus == PaymentStatus.PENDING &&
                    (status == LessonStatus.COMPLETED || (status == LessonStatus.CONFIRMED && thirtyMinPast))
            if (canMarkPaid) {
                Button(
                    onClick = onMarkPaid,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.tertiary,
                        contentColor = colorScheme.onTertiary,
                    ),
                ) {
                    Text("Oznacz jako opłaconą")
                }
            }
        }
    }
}
