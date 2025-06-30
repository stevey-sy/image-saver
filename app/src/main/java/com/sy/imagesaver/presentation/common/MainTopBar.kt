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
import com.sy.imagesaver.presentation.bookmark.BookMarkViewModel
import com.sy.imagesaver.presentation.navigation.Screen
import com.sy.imagesaver.presentation.theme.AppIcons
import com.sy.imagesaver.presentation.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    navController: NavHostController,
    bookMarkViewModel: BookMarkViewModel,
    showFilterDropdown: Boolean,
    onFilterDropdownChange: (Boolean) -> Unit,
    selectedFilter: MediaType?
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        actions = {
            // 현재 화면이 BookmarkScreen일 때만 아이콘들 표시
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val isBookmarkScreen = currentDestination?.hierarchy?.any { it.route == Screen.Bookmark.route } == true
            
            if (isBookmarkScreen) {
                Box {
                    IconButton(onClick = { onFilterDropdownChange(true) }) {
                        Icon(AppIcons.Filter, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = showFilterDropdown,
                        onDismissRequest = { onFilterDropdownChange(false) }
                    ) {
                        listOf("전체", "이미지", "영상").forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter) },
                                onClick = {
                                    bookMarkViewModel.updateFilter(filter)
                                    onFilterDropdownChange(false)
                                },
                                leadingIcon = {
                                    val isSelected = when (filter) {
                                        "전체" -> selectedFilter == null
                                        "이미지" -> selectedFilter == MediaType.IMAGE
                                        "영상" -> selectedFilter == MediaType.VIDEO
                                        else -> false
                                    }
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "선택됨",
                                            tint = Orange
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                IconButton(onClick = { bookMarkViewModel.toggleDeleteMode() }) {
                    Icon(AppIcons.Trash, contentDescription = "Trash")
                }
            }
        }
    )
} 