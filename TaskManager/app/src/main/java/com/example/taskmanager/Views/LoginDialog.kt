package com.example.taskmanager.Views

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.taskmanager.R
import com.example.taskmanager.ViewModels.SessionViewModel
import com.example.taskmanager.util.UiEventDispatcher
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun LoginDialog(
    sessionViewModel: SessionViewModel,
    onLoginSuccess: () -> Unit,
    onDismiss: () -> Unit,
    googleSignInClient: GoogleSignInClient,
    launcher: ActivityResultLauncher<Intent>
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val colorScheme = androidx.compose.material3.MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isRegisterMode) "Register Account" else "Login Required",
                color = colorScheme.onSurface
            )
        },
        text = {
            Column {
                ThemedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email"
                )

                ThemedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    isPassword = true
                )

                if (isRegisterMode) {
                    ThemedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        isPassword = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (email.isBlank() || password.isBlank() || (isRegisterMode && confirmPassword.isBlank())) {
                    UiEventDispatcher.send("Please fill in all fields")
                    return@TextButton
                }

                if (isRegisterMode) {
                    if (password != confirmPassword) {
                        UiEventDispatcher.send("Passwords do not match")
                        return@TextButton
                    }

                    sessionViewModel.register(
                        email.trim(), password,
                        onSuccess = {
                            UiEventDispatcher.send("Registered and logged in")
                            onLoginSuccess()
                        },
                        onFailure = {
                            UiEventDispatcher.send("Registration failed: ${it.message}")
                        }
                    )
                } else {
                    sessionViewModel.login(
                        email.trim(), password,
                        onSuccess = {
                            UiEventDispatcher.send("Login successful")
                            onLoginSuccess()
                        },
                        onFailure = {
                            UiEventDispatcher.send("Login failed: ${it.message}")
                        }
                    )
                }
            }) {
                Text(
                    text = if (isRegisterMode) "Register" else "Login",
                    color = colorScheme.primary
                )
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                    Text(
                        if (isRegisterMode) "Switch to Login" else "Switch to Register",
                        color = colorScheme.secondary
                    )
                }

                TextButton(onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                }) {
                    Text("Sign in with Google", color = colorScheme.tertiary)
                }

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = colorScheme.onSurface)
                }
            }
        }
    )
}

@Composable
fun ThemedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    val colorScheme = androidx.compose.material3.MaterialTheme.colorScheme
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = colorScheme.onSurface) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                val icon = if (showPassword)
                    R.drawable.baseline_visibility_off_24
                else
                    R.drawable.baseline_visibility_24

                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        tint = colorScheme.onSurface
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = colorScheme.onSurface,
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            cursorColor = colorScheme.primary,
            focusedLabelColor = colorScheme.primary,
            unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.7f)
        )
    )
}




