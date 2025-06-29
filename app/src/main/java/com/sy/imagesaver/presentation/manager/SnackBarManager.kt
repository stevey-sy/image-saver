package com.sy.imagesaver.presentation.manager

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.sy.imagesaver.presentation.theme.Orange
import com.sy.imagesaver.presentation.theme.Purple
import kotlinx.coroutines.CoroutineScope
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
    
    fun showSuccessSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        showSnackbarWithColor?.invoke(message, Orange) // 초록색
    }
    
    fun showErrorSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Long) {
        showSnackbarWithColor?.invoke(message, Purple)
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