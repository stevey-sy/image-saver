package com.sy.imagesaver.presentation.common

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MainSnackBar(
    snackbarHostState: SnackbarHostState,
    snackbarColor: Color,
    actionColor: Color
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = snackbarColor, // 동적 색상 적용
                contentColor = Color.White, // 흰색 텍스트
                actionContentColor = actionColor, // 동적 액션 버튼 색상
                shape = SnackbarDefaults.shape,
            )
        }
    )
} 