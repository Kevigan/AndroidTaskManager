package com.example.taskmanager.Views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taskmanager.AppScaffold
import com.example.taskmanager.util.UiEventDispatcher

@Composable
fun SettingsView(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    AppScaffold(
        title = "Settings",
        navController = navController
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "Choose Theme", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            ThemeToggleButton(
                text = "Light Mode",
                isSelected = !isDarkTheme,
                onClick = {
                    onThemeChange(false)
                    UiEventDispatcher.send("Switched to Light Mode")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ThemeToggleButton(
                text = "Dark Mode",
                isSelected = isDarkTheme,
                onClick = {
                    onThemeChange(true)
                    UiEventDispatcher.send("Switched to Dark Mode")
                }
            )
        }
    }
}

@Composable
fun ThemeToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
    val contentColor = if (isSelected) Color.White else Color.DarkGray

    Surface(
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(text = text, color = contentColor)
        }
    }
}


