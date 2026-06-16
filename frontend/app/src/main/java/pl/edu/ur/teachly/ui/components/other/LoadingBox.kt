package pl.edu.ur.teachly.ui.components.other

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Pełnoekranowy wskaźnik ładowania: wyśrodkowany [CircularProgressIndicator] wypełniający dostępną
 * przestrzeń. Wydzielony, aby ujednolicić powtarzający się w wielu ekranach stan ładowania.
 */
@Composable
fun LoadingBox(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}
