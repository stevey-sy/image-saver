package com.sy.imagesaver.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sy.imagesaver.R
import com.sy.imagesaver.domain.data.MediaType
import com.sy.imagesaver.presentation.bookmark.BookmarkViewModel
import com.sy.imagesaver.presentation.navigation.Screen
import com.sy.imagesaver.presentation.theme.AppIcons
import com.sy.imagesaver.presentation.theme.Orange
import com.sy.imagesaver.data.cache.CachedQueryInfo
import com.sy.imagesaver.presentation.model.BookmarkFilterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    navController: NavHostController,
    bookMarkViewModel: BookmarkViewModel,
    showFilterDropdown: Boolean,
    onFilterDropdownChange: (Boolean) -> Unit,
    selectedFilter: BookmarkFilterType,
    showHistoryDropdown: Boolean = false,
    onHistoryDropdownChange: (Boolean) -> Unit = {},
    cachedQueryList: List<CachedQueryInfo> = emptyList(),
    onHistoryItemClick: (String) -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        actions = {
            // 현재 화면 확인
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val isBookmarkScreen = currentDestination?.hierarchy?.any { it.route == Screen.Bookmark.route } == true
            val isSearchScreen = currentDestination?.hierarchy?.any { it.route == Screen.Search.route } == true
            
            if (isBookmarkScreen) {
                Box {
                    IconButton(onClick = { onFilterDropdownChange(true) }) {
                        Icon(AppIcons.Filter, contentDescription = stringResource(R.string.filter_icon_description))
                    }
                    DropdownMenu(
                        expanded = showFilterDropdown,
                        onDismissRequest = { onFilterDropdownChange(false) }
                    ) {
                        listOf(
                            stringResource(R.string.filter_all),
                            stringResource(R.string.filter_image),
                            stringResource(R.string.filter_video)
                        ).forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter) },
                                onClick = {
                                    bookMarkViewModel.updateFilter(filter)
                                    onFilterDropdownChange(false)
                                },
                                leadingIcon = {
                                    val isSelected = when (filter) {
                                        stringResource(R.string.filter_all) -> selectedFilter == BookmarkFilterType.ALL
                                        stringResource(R.string.filter_image) -> selectedFilter == BookmarkFilterType.IMAGE
                                        stringResource(R.string.filter_video) -> selectedFilter == BookmarkFilterType.VIDEO
                                        else -> false
                                    }
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = stringResource(R.string.selected_description),
                                            tint = Orange
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                IconButton(onClick = { bookMarkViewModel.toggleDeleteMode() }) {
                    Icon(AppIcons.Trash, contentDescription = stringResource(R.string.trash_icon_description))
                }
            } else if (isSearchScreen) {
                // SearchScreen에서 History 아이콘 표시
                Box {
                    IconButton(onClick = { onHistoryDropdownChange(true) }) {
                        Icon(
                            imageVector = AppIcons.History,
                            contentDescription = stringResource(R.string.history_icon_description)
                        )
                    }
                    DropdownMenu(
                        expanded = showHistoryDropdown,
                        onDismissRequest = { onHistoryDropdownChange(false) }
                    ) {
                        if (cachedQueryList.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.no_search_history)) },
                                onClick = { onHistoryDropdownChange(false) }
                            )
                        } else {
                            cachedQueryList.forEach { queryInfo ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(stringResource(R.string.search_history_format, queryInfo.query, queryInfo.cachedTime))
                                    },
                                    onClick = {
                                        onHistoryItemClick(queryInfo.query)
                                        onHistoryDropdownChange(false)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
} 