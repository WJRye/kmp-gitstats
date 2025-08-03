package io.github.kmp.gitstats

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object SnackbarManager {
    private var snackbarHostState: SnackbarHostState? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun init(hostState: SnackbarHostState) {
        snackbarHostState = hostState
    }

    fun show(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        scope.launch {
            snackbarHostState?.showSnackbar(message, null, false, duration)
        }
    }
}