package com.example.minirideapp
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()


    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.99f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis =600, easing =LinearOutSlowInEasing ),
            repeatMode = RepeatMode.Reverse
        )
    )


    LaunchedEffect(Unit) {

        val user = auth.currentUser
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    if (role == "Driver") {
                        navController.navigate("driver") { popUpTo("splash") { inclusive = true } }
                    } else {
                        navController.navigate("passenger") { popUpTo("splash") { inclusive = true } }
                    }
                }
        } else {
            delay(1000)
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Semi-transparent overlay (for opacity effect)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.3f))
        )

        // Animated Logo
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
            )
        }
    }
}

