package com.example.taskmanager.Views

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TestView() {
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(isDark) {
        Log.d("ThemeDebug", "System in dark mode? $isDark")
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "asshole")
    }
}

