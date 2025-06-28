package com.sy.imagesaver.presentation.manager

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackBarManager @Inject constructor() {
    
    private var snackbarHostState: SnackbarHostState? = null
    private var coroutineScope: CoroutineScope? = null
    
    fun setSnackbarHostState(hostState: SnackbarHostState, scope: CoroutineScope) {
        this.snackbarHostState = hostState
        this.coroutineScope = scope
    }
    
    fun showSnackbar(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        coroutineScope?.launch {
            snackbarHostState?.showSnackbar(
                message = message,
                duration = duration.toSnackbarDuration()
            )
        }
    }
    
    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null
    ) {
        coroutineScope?.launch {
            val result = snackbarHostState?.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration.toSnackbarDuration()
            )
            
            if (result == SnackbarResult.ActionPerformed && onAction != null) {
                onAction()
            }
        }
    }
    
    enum class SnackbarDuration {
        Short, Long, Indefinite
    }
    
    private fun SnackbarDuration.toSnackbarDuration(): androidx.compose.material3.SnackbarDuration {
        return when (this) {
            SnackbarDuration.Short -> androidx.compose.material3.SnackbarDuration.Short
            SnackbarDuration.Long -> androidx.compose.material3.SnackbarDuration.Long
            SnackbarDuration.Indefinite -> androidx.compose.material3.SnackbarDuration.Indefinite
        }
    }
}

@Composable
fun rememberSnackBarManager(): SnackBarManager {
    return remember { SnackBarManager() }
} 