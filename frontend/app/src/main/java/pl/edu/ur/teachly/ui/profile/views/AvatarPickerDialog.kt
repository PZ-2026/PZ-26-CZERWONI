package pl.edu.ur.teachly.ui.profile.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import pl.edu.ur.teachly.ui.components.other.PrimaryButton

/**
 * Dialog wyboru źródła zdjęcia profilowego: aparat, galeria oraz (opcjonalnie) usunięcie
 * bieżącego avatara. Logika uprawnień i uruchamiania launcherów pozostaje po stronie wywołującego —
 * ten composable odpowiada wyłącznie za prezentację i przekazanie zdarzeń.
 *
 * @param hasCustomAvatar czy użytkownik ma ustawione własne zdjęcie (decyduje o pokazaniu opcji usunięcia)
 * @param onDismiss zamknięcie dialogu bez wyboru
 * @param onTakePhoto wybór opcji „Zrób zdjęcie"
 * @param onPickFromGallery wybór opcji „Wybierz z galerii"
 * @param onDeleteAvatar wybór opcji „Usuń zdjęcie"
 */
@Composable
internal fun AvatarPickerDialog(
    hasCustomAvatar: Boolean,
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickFromGallery: () -> Unit,
    onDeleteAvatar: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Wybierz opcję",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface
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
                    Text("Anuluj", color = colorScheme.primary)
                }
            }
        }
    }
}
