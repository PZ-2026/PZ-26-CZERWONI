package pl.edu.ur.teachly.ui.components.other

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.data.repository.AuthRepository
import pl.edu.ur.teachly.navigation.navigateToSplash

@Composable
fun FullScreenError(message: String, modifier: Modifier = Modifier, onLogout: (() -> Unit)? = null) {
    val authRepository = koinInject<AuthRepository>()
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val navController = remember(view) {
        runCatching { view.findNavController() }.getOrNull()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = colorScheme.error,
                modifier = Modifier.size(52.dp)
            )
            Text(
                text = message,
                style = typography.bodyMedium,
                color = colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            LogoutButton(
                text = stringResource(R.string.logout),
                onClick = {
                    if (onLogout != null) {
                        onLogout()
                    } else {
                        (navController as? NavHostController)?.navigateToSplash()
                    }
                    scope.launch {
                        authRepository.logout()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ErrorBanner(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.errorContainer,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Error,
                contentDescription = null,
                tint = colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = message,
                style = typography.bodySmall,
                color = colorScheme.onErrorContainer
            )
        }
    }
    Spacer(Modifier.height(12.dp))
}
