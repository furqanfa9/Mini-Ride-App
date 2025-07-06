package com.example.minirideapp

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.text.isBlank

@Composable
fun LoginScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.3f)
                        )
                    )
                )
        )

        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFFF0048c),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(200.dp)
                )

                Surface(
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Welcome",
                            color = Color(0xFFF0048c),
                            style = MaterialTheme.typography.h5,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 30.sp
                        )
                        Text(
                            "Login to continue",
                            color = Color(0xFFF0048c),
                            style = MaterialTheme.typography.h5,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", color = Color(0xFFF0048c)) },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.LightGray.copy(alpha = 0.7f),
                                focusedIndicatorColor = Color(0xFFF0048c),
                                unfocusedIndicatorColor = Color.Gray,
                                textColor = Color(0xFFF0048c)
                            ),
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = Color(0xFFF0048c)) },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.LightGray.copy(alpha = 0.7f),
                                focusedIndicatorColor = Color(0xFFF0048c),
                                unfocusedIndicatorColor = Color.Gray,
                                textColor = Color(0xFFF0048c)
                            ),
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = "Toggle Password",
                                        tint = Color.DarkGray
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    Toast.makeText(context, "Invalid Email Address", Toast.LENGTH_SHORT).show()
                                } else {
                                    isLoading = true
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnSuccessListener {
                                            val userId = auth.currentUser?.uid ?: ""
                                            FirebaseFirestore.getInstance().collection("users")
                                                .document(userId)
                                                .get()
                                                .addOnSuccessListener { document ->
                                                    isLoading = false
                                                    val role = document.getString("role")
                                                    if (role == "Driver") {
                                                        navController.navigate("driver") { popUpTo(0) }
                                                    } else {
                                                        navController.navigate("passenger") { popUpTo(0) }
                                                    }
                                                }
                                        }
                                        .addOnFailureListener {
                                            isLoading = false
                                            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF0048c)),
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier.size(width = 200.dp, height = 50.dp)
                        ) {
                            Text(text = "Login", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Don't have an account?",
                                color = Color.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                            TextButton(
                                onClick = { navController.navigate("signup") },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = " Sign Up",
                                    color = Color(0xFFF0048c),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

