package com.example.taskmanager.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.Data.Task
import com.example.taskmanager.Data.TaskRepository
import com.example.taskmanager.Graph
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

// ViewModel for managing task-related operations and UI state.
// Handles creating, updating, deleting, and retrieving tasks via a repository.
// Emits user feedback events (e.g., snackbars) and supports task cleanup by user.

class TaskViewModel(
    private val repository: TaskRepository = Graph.taskRepository
) : ViewModel() {

    var taskTitleState by mutableStateOf("")
    var taskDescriptionState by mutableStateOf("")

    val getAllTasks: Flow<List<Task>> = repository.getTasks()

    // Event flow for one-time messages (e.g., snackbar)
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onTaskTitleChanged(newString: String) {
        taskTitleState = newString
    }

    fun onDescriptionChanged(newString: String) {
        taskDescriptionState = newString
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun addNewTask(title: String, description: String) {
        viewModelScope.launch {
            repository.addTask(
                Task(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    description = description.trim()
                )
            )
            _eventFlow.emit("Task created")
        }
    }

    fun updateTask(task: Task) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updatedTask = task.copy(userId = userId)

        viewModelScope.launch {
            repository.updateTask(updatedTask)
            _eventFlow.emit("Task updated")
        }
    }

    fun getTaskById(id: String): Flow<Task?> {
        return repository.getTaskById(id)
    }

    fun deleteUserTasks(
        uid: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("tasks")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                for (doc in snapshot.documents) {
                    batch.delete(doc.reference)
                }
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }
}
