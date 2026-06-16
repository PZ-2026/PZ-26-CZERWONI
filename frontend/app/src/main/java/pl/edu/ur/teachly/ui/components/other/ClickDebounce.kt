package pl.edu.ur.teachly.ui.components.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

private const val DEFAULT_DEBOUNCE_MS = 500L

@Composable
fun rememberDebouncedCallback(debounceMs: Long = DEFAULT_DEBOUNCE_MS, onClick: () -> Unit): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    return remember(onClick, debounceMs) {
        {
            val now = System.currentTimeMillis()
            if (now - lastClickTime >= debounceMs) {
                lastClickTime = now
                onClick()
            }
        }
    }
}

@Composable
fun rememberDebouncedPopBackStack(
    navController: NavHostController,
    debounceMs: Long = DEFAULT_DEBOUNCE_MS
): () -> Unit = rememberDebouncedCallback(debounceMs) {
    navController.popBackStack()
}
