package com.example.taskmanager.Data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseTaskRepository: TaskRepository {

    private var firestore = FirebaseFirestore.getInstance()
    private val tasksCollection = firestore.collection("tasks")

    override fun getTasks(): Flow<List<Task>> = callbackFlow {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val listener = tasksCollection
            .whereEqualTo("userId", currentUserId)
            .addSnapshotListener { snapshot, _ ->
                val tasks = snapshot?.toObjects(Task::class.java) ?: emptyList()
                trySend(tasks).isSuccess
            }

        awaitClose { listener.remove() }
    }


    override suspend fun addTask(task: Task) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

        val taskWithUser = task.copy(userId = currentUserId)
        tasksCollection.document(taskWithUser.id).set(taskWithUser).await()
    }

    override suspend fun deleteTask(task: Task) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (task.userId == currentUserId) {
            tasksCollection.document(task.id).delete().await()
        }
    }

    override suspend fun updateTask(task: Task) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (task.userId == currentUserId) {
            tasksCollection.document(task.id).set(task).await()
        }
    }

    override fun getTaskById(id: String): Flow<Task?> = callbackFlow {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            trySend(null).isSuccess
            close()
            return@callbackFlow
        }

        val docRef = tasksCollection.document(id)
        val listener = docRef.addSnapshotListener { snapshot, _ ->
            val task = snapshot?.toObject(Task::class.java)
            if (task?.userId == currentUserId) {
                trySend(task).isSuccess
            } else {
                trySend(null).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }


}