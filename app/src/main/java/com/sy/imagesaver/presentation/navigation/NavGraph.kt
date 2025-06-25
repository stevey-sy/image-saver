package com.sy.imagesaver.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sy.imagesaver.presentation.bookmark.BookmarkScreen
import com.sy.imagesaver.presentation.search.SearchScreen

sealed class Screen(val route: String) {
    object Bookmark : Screen("bookmark")
    object Search : Screen("search")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Bookmark.route
    ) {
        composable(Screen.Bookmark.route) {
            BookmarkScreen()
        }
        composable(Screen.Search.route) {
            SearchScreen()
        }
    }
}

