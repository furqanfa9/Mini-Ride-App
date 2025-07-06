package com.example.minirideapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.google.firebase.firestore.Query
import androidx.compose.material3.Card


@Composable
fun PassengerScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var rides by remember { mutableStateOf<List<Ride>>(emptyList()) }
    var pickup by remember { mutableStateOf("") }
    var dropoff by remember { mutableStateOf("") }
    var rideType by remember { mutableStateOf("Car") }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch latest ride with status "Requested"
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect

        val listenerRegistration = db.collection("rides")
            .whereEqualTo("userId", userId)
            // .whereEqualTo("status", "Requested") // optional filter
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    isLoading = false
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val ridesList = snapshot.documents.map { doc ->
                        Ride(
                            pickup = doc.getString("pickup") ?: "",
                            dropoff = doc.getString("dropoff") ?: "",
                            rideType = doc.getString("rideType") ?: "",
                            status = doc.getString("status") ?: ""
                        )
                    }
                    rides = ridesList
                } else {
                    rides = emptyList()
                }

                isLoading = false
            }

    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
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
                .padding(top=100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                RideRequestForm(
                    pickup = pickup,
                    dropoff = dropoff,
                    rideType = rideType,
                    onPickupChange = { pickup = it },
                    onDropoffChange = { dropoff = it },
                    onRideTypeChange = { rideType = it },
                    onRequest = {
                        val userId = auth.currentUser?.uid ?: return@RideRequestForm
                        val ride = hashMapOf(
                            "userId" to userId,
                            "pickup" to pickup,
                            "dropoff" to dropoff,
                            "rideType" to rideType,
                            "status" to "Requested",
                            "timestamp" to FieldValue.serverTimestamp()
                        )
                        FirebaseFirestore.getInstance().collection("rides")
                            .add(ride)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Ride Requested!", Toast.LENGTH_SHORT).show()
                                pickup = ""
                                dropoff = ""
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to request ride", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Divider(
                    color = Color(0xFFF0048C),
                    thickness = 2.dp,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.9f)
                )
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                androidx.compose.material.Text(
                    text = "Rides Status",
                    color = Color(0xFFF0048c),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFFF0048c))
                } else {
                    if (rides.isEmpty()) {
                        Text("No rides yet", color = Color.Gray, fontSize = 16.sp)
                    } else {
                        rides.forEach { ride ->
                            RideStatusCard(ride)
//                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RideStatusCard(ride: Ride) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = 8.dp,
        modifier = Modifier.fillMaxWidth().padding(24.dp) .border(
            width = 2.dp,
            color = Color(0xFFFFC0CB), // Pink color
            shape = RoundedCornerShape(20.dp)
        )
    )  {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            androidx.compose.material.Text(
                "Pickup: ${ride.pickup}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Monospace
            )
            androidx.compose.material.Text(
                "Dropoff: ${ride.dropoff}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Monospace
            )
            androidx.compose.material.Text(
                "Ride Type: ${ride.rideType}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(20.dp))

            Icon(
                imageVector = when (ride.status) {
                    "Requested" -> Icons.Default.HourglassBottom
                    "Accepted" -> Icons.Default.HowToVote
                    "In Progress" -> Icons.Default.DirectionsCar
                    "Completed" -> Icons.Default.Star
                    else -> Icons.Default.HourglassBottom
                },
                contentDescription = "Status Icon",
                tint = Color(0xFFF0048c),
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            androidx.compose.material.Text(
                text = "Status: ${ride.status}",
                fontSize = 18.sp,
                color = Color(0xFFF0048c),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun RideRequestForm(
    pickup: String,
    dropoff: String,
    rideType: String,
    onPickupChange: (String) -> Unit,
    onDropoffChange: (String) -> Unit,
    onRideTypeChange: (String) -> Unit,
    onRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Request a Ride",
            color = Color(0xFFF0048c),
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = pickup,
            onValueChange = onPickupChange,
            label = { Text("Pickup Location", color = Color(0xFFF0048c)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.LightGray.copy(alpha = 0.4f),
                focusedBorderColor = Color(0xFFF0048c),
                textColor = Color.Black
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        Icon(Icons.Default.ArrowDownward, contentDescription = "Switch", tint = Color(0xFFF0048c), modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = dropoff,
            onValueChange = onDropoffChange,
            label = { Text("Drop-off Location", color = Color(0xFFF0048c)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.LightGray.copy(alpha = 0.4f),
                focusedBorderColor = Color(0xFFF0048c),
                textColor = Color.Black
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Select Ride Type", color = Color(0xFFF0048c), fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            RideTypeOption("Car", rideType == "Car", Icons.Default.DirectionsCar, onRideTypeChange)
            RideTypeOption("Bike", rideType == "Bike", Icons.Default.DirectionsBike, onRideTypeChange)
            RideTypeOption("Rickshaw", rideType == "Rickshaw", Icons.Default.ElectricRickshaw, onRideTypeChange)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onRequest,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0048C)),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.size(250.dp, 50.dp)
        ) {
            Text("Send Request", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}


@Composable
fun RideTypeOption(type: String, selected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, onSelect: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onSelect(type) }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = type,
            tint = if (selected) Color(0xFFF0048c) else Color.Gray,
            modifier = Modifier.size(40.dp)
        )
        Text(type, color = if (selected) Color(0xFFF0048c) else Color.Gray, fontSize = 12.sp)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideAppHeader(navController: NavController, title: String = "Mini Ride App") {
    var showProfileCard by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .clickable(enabled = showProfileCard) { showProfileCard = false }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF0048C),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu",modifier = Modifier.size(35.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(Icons.Default.History, contentDescription = "History",modifier = Modifier.size(35.dp))
                    }
                    IconButton(onClick = {  FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0)
                        } }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Profile",modifier = Modifier.size(35.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    }
}
