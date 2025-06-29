package com.sy.imagesaver.presentation.search.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.paging.LoadState
import com.sy.imagesaver.presentation.search.SearchViewModel

@Composable
fun SearchErrorHandler(
    loadState: LoadState,
    viewModel: SearchViewModel
) {
    LaunchedEffect(loadState) {
        when (loadState) {
            is LoadState.Error -> {
                viewModel.handleLoadStateError(loadState.error)
            }
            is LoadState.NotLoading -> {
                viewModel.clearError()
            }
            else -> {}
        }
    }
} 