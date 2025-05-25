package com.example.taskmanager

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskmanager.util.UiEventDispatcher
import kotlinx.coroutines.launch

// A composable that provides a reusable SnackbarHostState to its children.
// Listens for UI events and displays snackbars when triggered.
// Tied to the lifecycle to ensure proper collection of events.

@Composable
fun ProvideSnackbarHost(
    content: @Composable (SnackbarHostState) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(snackbarHostState) {
        launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                UiEventDispatcher.eventFlow.collect { event ->
                    when (event) {
                        is UiEventDispatcher.UiEvent.ShowSnackbar ->
                            snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    content(snackbarHostState)
}


