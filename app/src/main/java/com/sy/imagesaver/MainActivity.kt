package com.sy.imagesaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.sy.imagesaver.presentation.manager.rememberSnackBarManager
import com.sy.imagesaver.presentation.navigation.NavGraph
import com.sy.imagesaver.presentation.theme.ImageSaverTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import com.sy.imagesaver.presentation.bookmark.BookMarkViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.sy.imagesaver.data.cache.CachedQueryInfo
import com.sy.imagesaver.presentation.common.MainBottomBar
import com.sy.imagesaver.presentation.common.MainSnackBar
import com.sy.imagesaver.presentation.common.MainTopBar
import com.sy.imagesaver.presentation.manager.SnackBarManager
import com.sy.imagesaver.presentation.theme.Orange
import com.sy.imagesaver.presentation.theme.Purple

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
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val snackBarManager = rememberSnackBarManager()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val bookMarkViewModel: BookMarkViewModel = hiltViewModel()
    
    // 필터 드롭다운 상태
    var showFilterDropdown by remember { mutableStateOf(false) }
    val bookmarkUiState by bookMarkViewModel.uiState.collectAsState()
    val selectedFilter = bookmarkUiState.selectedFilter
    
    // History dropdown 상태
    var showHistoryDropdown by remember { mutableStateOf(false) }
    var cachedQueries by remember { mutableStateOf<List<CachedQueryInfo>>(emptyList()) }
    var onHistoryItemClick: (String) -> Unit = {}
    
    // SnackBar 색상 상태
    var snackbarColor by remember { mutableStateOf(Color.Transparent) }
    var actionColor by remember { mutableStateOf(Color.White) }
    
    // SnackBar 이벤트 처리
    LaunchedEffect(snackBarManager) {
        snackBarManager.snackBarFlow.collect { event ->
            when (event) {
                is SnackBarManager.SnackBarEvent.Success -> {
                    snackbarColor = Orange  // Green
                    actionColor = Color.White
                }
                is SnackBarManager.SnackBarEvent.Error -> {
                    snackbarColor = Purple  // Red
                    actionColor = Color.White
                }
            }
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainTopBar(
                navController = navController,
                bookMarkViewModel = bookMarkViewModel,
                showFilterDropdown = showFilterDropdown,
                onFilterDropdownChange = { showFilterDropdown = it },
                selectedFilter = selectedFilter,
                showHistoryDropdown = showHistoryDropdown,
                onHistoryDropdownChange = { showHistoryDropdown = it },
                cachedQueryList = cachedQueries,
                onHistoryItemClick = onHistoryItemClick
            )
        },
        bottomBar = {
            MainBottomBar(navController = navController)
        },
        snackbarHost = { 
            MainSnackBar(
                snackbarHostState = snackbarHostState,
                snackbarColor = snackbarColor,
                actionColor = actionColor
            )
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            snackBarManager = snackBarManager,
            bookMarkViewModel = bookMarkViewModel,
            onSearchViewModelReady = { searchViewModel ->
                onHistoryItemClick = { query ->
                    searchViewModel.selectCachedQuery(query)
                }
            },
            onCachedQueriesUpdate = { queries ->
                cachedQueries = queries
            },
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