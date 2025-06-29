package com.sy.imagesaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sy.imagesaver.presentation.manager.rememberSnackBarManager
import com.sy.imagesaver.presentation.navigation.NavGraph
import com.sy.imagesaver.presentation.navigation.Screen
import com.sy.imagesaver.presentation.theme.AppIcons
import com.sy.imagesaver.presentation.theme.ImageSaverTheme
import com.sy.imagesaver.presentation.theme.Orange
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material3.Snackbar
import kotlinx.coroutines.launch
import com.sy.imagesaver.presentation.bookmark.BookMarkViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageSaverTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val snackBarManager = rememberSnackBarManager()
    val bookMarkViewModel: BookMarkViewModel = hiltViewModel()
    
    // SnackBar 색상 상태
    var snackbarColor by remember { mutableStateOf(Color(0xFF4CAF50)) } // 기본 초록색
    
    // 액션 버튼 색상 계산
    val actionColor = when {
        snackbarColor == Color(0xFF4CAF50) -> Color(0xFFE8F5E8) // 초록색 배경일 때 연한 초록색
        snackbarColor == Color(0xFFF44336) -> Color(0xFFFFEBEE) // 붉은색 배경일 때 연한 붉은색
        else -> Color(0xFFE8F5E8) // 기본값
    }
    
    // SnackBarManager에 SnackbarHostState 설정
    LaunchedEffect(snackbarHostState, coroutineScope) {
        snackBarManager.setSnackbarHostState(snackbarHostState, coroutineScope)
    }
    
    // SnackBarManager에 색상 변경 콜백 설정
    LaunchedEffect(snackBarManager) {
        snackBarManager.setShowSnackbarWithColor { message, color ->
            snackbarColor = color
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
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
                        IconButton(onClick = { /* Handle filter icon click */ }) {
                            Icon(AppIcons.Filter, contentDescription = "Filter")
                        }
                        IconButton(onClick = { bookMarkViewModel.toggleDeleteMode() }) {
                            Icon(AppIcons.Trash, contentDescription = "Trash")
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                listOf(
                    Screen.Bookmark to Icons.Default.Check,
                    Screen.Search to Icons.Default.Search
                ).forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen.route) },
                        label = { 
                            Text(
                                when (screen) {
                                    Screen.Bookmark -> "나의 보관함"
                                    Screen.Search -> "검색"
                                }
                            ) 
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Orange,
                            selectedTextColor = Orange,
                            indicatorColor = Orange.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        snackbarHost = { 
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
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            snackBarManager = snackBarManager,
            bookMarkViewModel = bookMarkViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ImageSaverTheme {
        MainScreen()
    }
}