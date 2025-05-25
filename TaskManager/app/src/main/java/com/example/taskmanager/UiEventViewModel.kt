package com.example.taskmanager.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// A singleton for dispatching UI events like showing snackbars.
// Uses a SharedFlow to emit events that can be observed by composables.
// Ensures events are sent on the main thread using coroutines.

object UiEventDispatcher {
    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    fun send(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            _eventFlow.emit(UiEvent.ShowSnackbar(message))
        }
    }
}

