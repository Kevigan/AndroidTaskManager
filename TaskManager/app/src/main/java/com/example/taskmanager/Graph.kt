package com.example.taskmanager

import com.example.taskmanager.Data.FirebaseTaskRepository
import com.example.taskmanager.Data.TaskRepository

object Graph {
    val taskRepository: TaskRepository by lazy{
        FirebaseTaskRepository()
    }
}