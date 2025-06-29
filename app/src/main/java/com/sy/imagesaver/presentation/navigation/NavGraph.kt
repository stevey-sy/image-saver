package com.sy.imagesaver.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sy.imagesaver.presentation.bookmark.BookMarkScreen
import com.sy.imagesaver.presentation.bookmark.BookMarkViewModel
import com.sy.imagesaver.presentation.search.SearchScreen
import com.sy.imagesaver.presentation.manager.SnackBarManager

sealed class Screen(val route: String) {
    object Bookmark : Screen("bookmark")
    object Search : Screen("search")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    snackBarManager: SnackBarManager,
    bookMarkViewModel: BookMarkViewModel,
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
            SearchScreen(snackBarManager = snackBarManager)
        }
    }
}

