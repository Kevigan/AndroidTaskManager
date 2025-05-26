package com.example.taskmanager.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.taskmanager.Data.Task
import com.example.taskmanager.R
import com.example.taskmanager.ViewModels.TaskViewModel
import com.example.taskmanager.util.UiEventDispatcher
import kotlinx.coroutines.launch

@Composable
fun AddEditDetailView(
    id: String,
    viewModel: TaskViewModel,
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = remember { androidx.compose.material.SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isEditMode = id != "new"
    val taskState = if (isEditMode) viewModel.getTaskById(id).collectAsState(initial = null) else null

    // Load task details into ViewModel state
    LaunchedEffect(taskState?.value) {
        if (isEditMode) {
            taskState?.value?.let {
                viewModel.taskTitleState = it.title
                viewModel.taskDescriptionState = it.description
            }
        } else {
            viewModel.taskTitleState = ""
            viewModel.taskDescriptionState = ""
        }
    }

    Scaffold(
        topBar = {
            AppBarView(
                title = if (isEditMode) stringResource(id = R.string.update_task)
                else stringResource(id = R.string.add_task),
                onBackNavClicked = { navController.popBackStack() }
            )
        },
        snackbarHost = { androidx.compose.material.SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        // Show fallback if task doesn't exist in edit mode
        if (isEditMode && taskState?.value == null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Task not found", color = Color.Red)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                TaskTextField(
                    label = "Title",
                    value = viewModel.taskTitleState,
                    onValueChanged = { viewModel.onTaskTitleChanged(it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                TaskTextField(
                    label = "Description",
                    value = viewModel.taskDescriptionState,
                    onValueChanged = { viewModel.onDescriptionChanged(it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (viewModel.taskTitleState.isNotEmpty() &&
                            viewModel.taskDescriptionState.isNotEmpty()
                        ) {
                            if (isEditMode) {
                                viewModel.updateTask(
                                    Task(
                                        id = id,
                                        title = viewModel.taskTitleState.trim(),
                                        description = viewModel.taskDescriptionState.trim()
                                    )
                                )
                            } else {
                                viewModel.addNewTask(
                                    title = viewModel.taskTitleState,
                                    description = viewModel.taskDescriptionState
                                )
                            }

                            scope.launch {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("snackbar_message", if (isEditMode) "Task updated" else "Task created")

                                navController.popBackStack()
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please fill in all fields")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSurface
                    )
                ) {
                    Text(
                        text = if (isEditMode)
                            stringResource(id = R.string.update_task)
                        else
                            stringResource(id = R.string.add_task),
                        style = TextStyle(fontSize = 18.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskTextField(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(text = label, color = MaterialTheme.colors.onSurface) },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface), // Optional — applies to full row
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colors.onSurface,
            backgroundColor = MaterialTheme.colors.surface, // ✅ background inside the field
            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colors.primary,
            focusedLabelColor = MaterialTheme.colors.primary,
            unfocusedLabelColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
    )
}

