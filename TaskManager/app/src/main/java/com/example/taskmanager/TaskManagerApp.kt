package com.example.taskmanager

import android.app.Application
import com.google.firebase.FirebaseApp

class TaskManagerApp: Application() {
    override fun onCreate(){
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}