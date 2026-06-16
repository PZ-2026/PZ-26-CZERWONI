package pl.edu.ur.teachly.ui.components.other.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pl.edu.ur.teachly.ui.components.other.PrimaryButton

/**
 * Dialog wyboru źródła zdjęcia profilowego (aparat / galeria / usunięcie) używany w panelu
 * administratora. Logika uprawnień i launcherów pozostaje po stronie wywołującego — komponent
 * odpowiada wyłącznie za prezentację i przekazanie zdarzeń.
 *
 * @param hasCustomAvatar czy pokazać opcję usunięcia bieżącego zdjęcia
 * @param onDismiss zamknięcie dialogu bez wyboru
 * @param onTakePhoto wybór opcji „Zrób zdjęcie"
 * @param onPickFromGallery wybór opcji „Wybierz z galerii"
 * @param onDeleteAvatar wybór opcji „Usuń zdjęcie"
 */
@Composable
internal fun AvatarSourcePickerDialog(
    hasCustomAvatar: Boolean,
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickFromGallery: () -> Unit,
    onDeleteAvatar: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .widthIn(max = 400.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Zdjęcie profilowe",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Wybierz źródło zdjęcia",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PrimaryButton(
                    text = "Zrób zdjęcie",
                    onClick = onTakePhoto
                )
                PrimaryButton(
                    text = "Wybierz z galerii",
                    onClick = onPickFromGallery
                )
                if (hasCustomAvatar) {
                    PrimaryButton(
                        text = "Usuń zdjęcie",
                        onClick = onDeleteAvatar
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text("Anuluj", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
