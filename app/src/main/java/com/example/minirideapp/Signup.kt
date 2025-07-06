package com.example.minirideapp

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
import kotlin.to

@Composable
fun SignupScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Passenger") }
    var passwordVisible by remember { mutableStateOf(false) }

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
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Create Account",
                        color = Color(0xFFF0048c),
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 30.sp
                    )
                    Text(
                        "Signup to Start Journey",
                        color = Color(0xFFF0048c),
                        style = MaterialTheme.typography.h5,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name", color = Color(0xFFF0048c)) },
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
                                Icon(imageVector = image, contentDescription = "Toggle Password", tint = Color.DarkGray)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = role == "Driver",
                            onClick = { role = "Driver" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFF0048c),
                                unselectedColor = Color.Black
                            )
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.driver),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(40.dp)
                            )
                            Text("Driver", color = Color.Black, fontSize = 8.sp,fontWeight = FontWeight.ExtraBold,fontFamily = FontFamily.Monospace)

                        }

                        Spacer(modifier = Modifier.width(30.dp))
                        RadioButton(
                            selected = role == "Passenger",
                            onClick = { role = "Passenger" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFF0048c),
                                unselectedColor = Color.Black
                            )
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.passenger),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(40.dp)
                            )
                            Text("Passenger", color = Color.Black, fontSize = 8.sp,fontWeight = FontWeight.ExtraBold,fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    val userId = auth.currentUser?.uid ?: ""
                                    val user = if (role == "Driver") {
                                        hashMapOf(
                                            "name" to name,
                                            "email" to email,
                                            "role" to role,
                                            "availability" to false
                                        )
                                    } else {
                                        hashMapOf(
                                            "name" to name,
                                            "email" to email,
                                            "role" to role,
                                        )
                                    }


                                    FirebaseFirestore.getInstance().collection("users")
                                        .document(userId)
                                        .set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "SignUp Successful", Toast.LENGTH_SHORT).show()
                                            navController.navigate("login")
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "SignUp Failed", Toast.LENGTH_SHORT).show()
                                }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF0048c)),
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.size(width = 200.dp, height = 50.dp)
                    ) {
                        Text("Sign Up", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold,fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Already have an account?",
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        TextButton(
                            onClick = { navController.navigate("login") },
                        ) {
                            Text(
                                text = " Login Here",
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

