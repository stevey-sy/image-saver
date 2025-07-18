package com.sy.imagesaver.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sy.imagesaver.presentation.bookmark.BookMarkScreen
import com.sy.imagesaver.presentation.bookmark.BookmarkViewModel
import com.sy.imagesaver.presentation.search.SearchScreen
import com.sy.imagesaver.presentation.search.SearchViewModel
import com.sy.imagesaver.presentation.manager.SnackBarManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.imagesaver.data.cache.CachedQueryInfo

sealed class Screen(val route: String) {
    object Bookmark : Screen("bookmark")
    object Search : Screen("search")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    snackBarManager: SnackBarManager,
    bookMarkViewModel: BookmarkViewModel,
    onSearchViewModelReady: ((SearchViewModel) -> Unit)? = null,
    onCachedQueriesUpdate: ((List<CachedQueryInfo>) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Bookmark.route,
        modifier = modifier
    ) {
        composable(Screen.Bookmark.route) {
            BookMarkScreen(
                viewModel = bookMarkViewModel,
                snackBarManager = snackBarManager
            )
        }
        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = hiltViewModel()
            
            // SearchViewModel이 준비되면 MainActivity에 전달
            LaunchedEffect(searchViewModel) {
                onSearchViewModelReady?.invoke(searchViewModel)
            }
            
            // 캐시된 검색어 목록 상태 변화 감지
            val searchUiState = searchViewModel.uiState.collectAsState()
            LaunchedEffect(searchUiState.value.cachedQueries) {
                onCachedQueriesUpdate?.invoke(searchUiState.value.cachedQueries)
            }
            
            SearchScreen(viewModel = searchViewModel)
        }
    }
}

