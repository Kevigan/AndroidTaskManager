package com.example.taskmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.taskmanager.Views.TestView
import com.example.taskmanager.ui.theme.TaskManagerTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContent {
            val context = LocalContext.current

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1004096202121-2uphf3sifv3ui95bbb8sti39b7egdgh6.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = remember {
                GoogleSignIn.getClient(context, gso)
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Signed in with Google", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Google sign-in failed: ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } catch (e: ApiException) {
                    Toast.makeText(context, "Google sign-in error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
            CompositionLocalProvider(LocalContentColor provides Color.Black) {
                TaskManagerTheme() {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White
                    ) {
                        Navigation(
                            googleSignInClient = googleSignInClient,
                            googleSignInLauncher = launcher
                        )
                    }
                }
            }
        }
    }
}
