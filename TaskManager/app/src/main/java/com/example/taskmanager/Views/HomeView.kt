package com.example.taskmanager.Views

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.taskmanager.AppScaffold
import com.example.taskmanager.Data.Task
import com.example.taskmanager.ProvideSnackbarHost
import com.example.taskmanager.R
import com.example.taskmanager.Screen
import com.example.taskmanager.ViewModels.SessionViewModel
import com.example.taskmanager.ViewModels.TaskViewModel
import com.example.taskmanager.drawerScreens
import com.example.taskmanager.ui.theme.Purple40
import com.example.taskmanager.util.UiEventDispatcher
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeView(
    viewModel: TaskViewModel,
    sessionViewModel: SessionViewModel,
    navController: NavController,
    googleSignInClient: GoogleSignInClient,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
) {
    val currentUser by sessionViewModel.currentUser.collectAsState()
    val showLoginDialog = remember { mutableStateOf(currentUser == null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    val currentBackStackEntry = navController.currentBackStackEntry
    val snackbarMessage =
        currentBackStackEntry?.savedStateHandle?.remove<String>("snackbar_message")

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            UiEventDispatcher.send(it)
        }
    }

    // Watch for auth changes to control login dialog
    LaunchedEffect(currentUser) {
        showLoginDialog.value = currentUser == null
    }

    val tasks by if (!showLoginDialog.value)
        viewModel.getAllTasks.collectAsState(initial = emptyList())
    else
        remember { mutableStateOf(emptyList()) }

    if (showLoginDialog.value) {
        LoginDialog(
            onLoginSuccess = { showLoginDialog.value = false },
            onDismiss = { },
            sessionViewModel = sessionViewModel,
            googleSignInClient = googleSignInClient,
            launcher = googleSignInLauncher
        )
    }

    ProvideSnackbarHost { snackbarHostState ->
        AppScaffold(
            title = "TaskManager",
            navController = navController,
            showBackButton = false,
            snackbarHostState = snackbarHostState,
            drawerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.secondary)
                ) {
                    DrawerContent(
                        navController = navController,
                        sessionViewModel = sessionViewModel,
                        googleSignInClient = googleSignInClient,
                    ) {
                        navController.navigate(Screen.HomeScreen.route)
                    }
                }
            },
            floatingActionButton = {
                if (!showLoginDialog.value) {
                    FloatingActionButton(
                        modifier = Modifier.padding(20.dp),
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = Color.White,
                        onClick = {
                            navController.navigate(Screen.AddScreen.route + "/new")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = colorResource(id = R.color.white)
                        )
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                Text(
                    text = if (currentUser != null)
                        "Logged in as: ${currentUser?.email ?: "Unknown user"}"
                    else
                        "Not logged in",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )

                if (!showLoginDialog.value) {
                    LazyColumn {
                        items(tasks, key = { it.id }) { task ->
                            val dismissState = rememberDismissState(
                                confirmStateChange = {
                                    if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                                        taskToDelete = task
                                        false
                                    } else false
                                }
                            )
                            SwipeToDismiss(
                                state = dismissState,
                                directions = setOf(DismissDirection.EndToStart),
                                dismissThresholds = { FractionalThreshold(0.75f) },
                                background = {
                                    val color by animateColorAsState(
                                        if (dismissState.dismissDirection == DismissDirection.EndToStart) Color.Red
                                        else Color.Transparent,
                                        label = ""
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                },
                                dismissContent = {
                                    TaskItem(task = task) {
                                        navController.navigate(Screen.AddScreen.route + "/${task.id}")
                                    }
                                }
                            )
                        }

                    }
                }

                taskToDelete?.let { task ->
                    AlertDialog(
                        onDismissRequest = { taskToDelete = null },
                        backgroundColor = MaterialTheme.colors.secondary, // ✅ White in light theme, dark in dark theme
                        title = {
                            Text(
                                text = "Delete Task",
                                color = MaterialTheme.colors.onSurface // ✅ Theme-aware text
                            )
                        },
                        text = {
                            Text(
                                text = "Are you sure you want to delete \"${task.title}\"?",
                                color = MaterialTheme.colors.onSurface // ✅ Theme-aware text
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.deleteTask(task)
                                taskToDelete = null
                            }) {
                                Text(
                                    text = "Delete",
                                    color = MaterialTheme.colors.error
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { taskToDelete = null }) {
                                Text(
                                    text = "Cancel",
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    )
                }

            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onClick: () -> Unit) {
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 10.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )
            )
            Text(
                text = task.description,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )
            )
        }
    }
}

@Composable
fun DrawerContent(
    navController: NavController,
    sessionViewModel: SessionViewModel,
    googleSignInClient: GoogleSignInClient,
    closeDrawer: () -> Unit
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Column(modifier = Modifier.padding(16.dp)) {
        drawerScreens.forEach { screen ->
            DrawerItem(
                selected = currentRoute == screen.route,
                item = screen,
                onDrawerItemClicked = {
                    closeDrawer()
                    navController.navigate(screen.route)
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        //  Manual logout item
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .clickable {
                    sessionViewModel.signOut(googleSignInClient)
                    closeDrawer()
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { inclusive = true }
                    }
                }
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_logout_24), // Replace with your logout icon
                contentDescription = "Log out",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Log out",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Composable
fun DrawerItem(
    selected: Boolean,
    item: Screen,
    onDrawerItemClicked: () -> Unit
) {
    val backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)


    val contentColor = MaterialTheme.colors.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .background(backgroundColor)
            .clickable { onDrawerItemClicked() }
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.title,
            tint = contentColor,
            modifier = Modifier.padding(end = 8.dp, top = 4.dp)
        )
        Text(
            text = item.title,
            color = contentColor,
            style = MaterialTheme.typography.subtitle1
        )
    }
}
