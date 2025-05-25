package com.example.taskmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.taskmanager.Views.AppBarView
import com.example.taskmanager.util.UiEventDispatcher
import kotlinx.coroutines.launch

@Composable
fun AppScaffold(
    title: String,
    navController: NavController,
    showBackButton: Boolean = true,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    drawerContent: (@Composable ColumnScope.() -> Unit)? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AppBarView(
                title = title,
                showBackButton = showBackButton,
                onBackNavClicked = { navController.navigateUp() },
                onMenuClick = {
                    scope.launch { scaffoldState.drawerState.open() }
                }
            )
        },
        drawerContent = drawerContent,
        floatingActionButton = floatingActionButton ?: {}
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            content(padding)
        }
    }
}



