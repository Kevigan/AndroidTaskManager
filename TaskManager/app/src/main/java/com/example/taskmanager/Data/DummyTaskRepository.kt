package com.example.taskmanager.Data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class DummyTaskRepository : TaskRepository {

    private val tasks = MutableStateFlow(
        listOf(
            Task(id = UUID.randomUUID().toString(), title = "Do laundry", description = "Wash whites and colors"),
            Task(id = UUID.randomUUID().toString(), title = "Buy groceries", description = "Milk, eggs, bread"),
            Task(id = UUID.randomUUID().toString(), title = "Read a book", description = "Finish chapter 5")
        )
    )

    override fun getTasks(): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun addTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun getTaskById(id: String): Flow<Task?> {
        TODO("Not yet implemented")
    }
}