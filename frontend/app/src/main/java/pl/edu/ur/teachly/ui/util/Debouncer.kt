package pl.edu.ur.teachly.ui.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debouncer(
    private val scope: CoroutineScope,
    private val delayMs: Long = 300L
) {
    private var job: Job? = null

    fun submit(block: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch {
            delay(delayMs)
            block()
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}
