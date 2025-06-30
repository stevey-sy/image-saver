package com.sy.imagesaver.presentation.manager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackBarManager @Inject constructor() {
    private val _snackBarFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarFlow: SharedFlow<SnackBarEvent> = _snackBarFlow.asSharedFlow()
    
    suspend fun showSuccessSnackbar(message: String) {
        _snackBarFlow.emit(SnackBarEvent.Success(message))
    }
    
    suspend fun showErrorSnackbar(message: String) {
        _snackBarFlow.emit(SnackBarEvent.Error(message))
    }
    
    sealed class SnackBarEvent {
        abstract val message: String
        
        data class Success(override val message: String) : SnackBarEvent()
        data class Error(override val message: String) : SnackBarEvent()
    }
}

@Composable
fun rememberSnackBarManager(): SnackBarManager {
    return remember { SnackBarManager() }
} 