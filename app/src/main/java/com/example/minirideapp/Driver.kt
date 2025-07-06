package com.example.minirideapp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip


// Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query



data class Ride(
    val id: String = "",
    val pickup: String = "",
    val dropoff: String = "",
    val rideType: String = "",
    val status: String = ""
)

@Composable
fun DriverScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var rideRequests by remember { mutableStateOf<List<Ride>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Fetch all rides with status "Requested"
    LaunchedEffect(Unit) {
        db.collection("rides")
            .whereEqualTo("status", "Requested")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    rideRequests = snapshot.documents.map { doc ->
                        Ride(
                            id = doc.id,
                            pickup = doc.getString("pickup") ?: "",
                            dropoff = doc.getString("dropoff") ?: "",
                            rideType = doc.getString("rideType") ?: "",
                            status = doc.getString("status") ?: ""
                        )
                    }
                } else {
                    rideRequests = emptyList()
                }
                isLoading = false
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.3f)
                        )
                    )
                )
        )

        RideAppHeader(navController = navController, title = "Jeeny")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                androidx.compose.material.Text(
                    text = "Ride Requests",
                    color = Color(0xFFF0048c),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            item {
                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFFF0048C))
                } else {
                    if (rideRequests.isEmpty()) {
                        Text("No ride requests", color = Color.Gray, fontSize = 16.sp)
                    } else {
                        rideRequests.forEach { ride ->
                            RideRequestCardForDriver(ride) { accepted ->
                                val newStatus = if (accepted) "Accepted" else "Rejected"
                                db.collection("rides").document(ride.id!!)
                                    .update("status", newStatus)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Ride $newStatus!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun RideRequestCardForDriver(ride: Ride, onAction: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(0.9f)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF0048C),
                        Color.Transparent,
                        Color.Transparent,
                        Color(0xFFF0048C)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(2.dp) // To show pink around corners
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pickup: ${ride.pickup}", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text("Dropoff: ${ride.dropoff}", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text("Type: ${ride.rideType}", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text("Status: ${ride.status}",fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { onAction(true) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp)).border(
                                width = 2.dp,
                                color = Color(0xFFF0048C), // Pink color
                                shape = RoundedCornerShape(20.dp)
                            )
//                            .background(Color(0xFFFFC0CB)) // Pink corner
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Accept",
                            tint = Color(0xFF4CAF50), // Green tint
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    IconButton(
                        onClick = { onAction(false) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp)).border(
                                width = 2.dp,
                                color = Color(0xFFF0048C), // Pink color
                                shape = RoundedCornerShape(20.dp)
                            )
//                            .background(Color(0xFFFFC0CB)) // Pink corner
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reject",
                            tint = Color(0xFFF44336), // Red tint
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

            }
        }
    }
}
