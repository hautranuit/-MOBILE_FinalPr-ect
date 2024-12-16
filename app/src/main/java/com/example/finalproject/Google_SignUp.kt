package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Google_SignUp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val googleAuthClient = GoogleAuthClient(applicationContext)

        setContent {
            MaterialTheme {
                // Initialize `isSignIn` as false
                var isSignIn by rememberSaveable { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Background Image with Blur Effect
                    Image(
                        painter = painterResource(id = R.drawable.background_sign_up_by_google),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)) // Light blur effect
                    )

                    // Centered Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(350.dp)
                            .align(Alignment.Center)
                            .shadow(12.dp, RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Welcome Text
                            Text(
                                text = "Welcome to Pothole Discosafe",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Google Icon
                            Image(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(70.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Sign In Button
                            ElevatedButton(
                                onClick = {
                                    lifecycleScope.launch {
                                        if (!isSignIn) {
                                            val success = googleAuthClient.signIn()
                                            if (success) {
                                                // Navigate to the next screen if Sign In is successful
                                                startActivity(
                                                    Intent(this@Google_SignUp, sign_up_google3::class.java)
                                                )
                                                finish()
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(
                                    text = "Sign In With Google",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

