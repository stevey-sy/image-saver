package com.sy.imagesaver.presentation.manager

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackBarManager @Inject constructor() {
    
    private var snackbarHostState: SnackbarHostState? = null
    private var coroutineScope: CoroutineScope? = null
    private var showSnackbarWithColor: ((String, Color) -> Unit)? = null
    
    fun setSnackbarHostState(hostState: SnackbarHostState, scope: CoroutineScope) {
        this.snackbarHostState = hostState
        this.coroutineScope = scope
    }
    
    fun setShowSnackbarWithColor(callback: (String, Color) -> Unit) {
        this.showSnackbarWithColor = callback
    }
    
    fun showSnackbar(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        println("SnackBarManager: showSnackbar 호출됨 - message: $message, duration: $duration")
        println("SnackBarManager: snackbarHostState: $snackbarHostState, coroutineScope: $coroutineScope")
        coroutineScope?.launch {
            println("SnackBarManager: SnackBar 표시 시작")
            snackbarHostState?.showSnackbar(
                message = message,
                duration = duration.toSnackbarDuration()
            )
            println("SnackBarManager: SnackBar 표시 완료")
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
    
    fun showSuccessSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        showSnackbarWithColor?.invoke(message, Color(0xFF4CAF50)) // 초록색
    }
    
    fun showErrorSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Long) {
        showSnackbarWithColor?.invoke(message, Color(0xFFF44336)) // 붉은색
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