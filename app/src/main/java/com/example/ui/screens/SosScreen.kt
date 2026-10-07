package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.UpiDebitRed
import com.example.ui.theme.UpiSuccessGreen
import com.example.ui.viewmodel.UpiViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosScreen(viewModel: UpiViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val emergencyContacts by viewModel.emergencyContacts.collectAsState(initial = emptyList())
    val context = LocalContext.current

    var isAlarmActive by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    var showSmsPreviewDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "beacon")
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beaconPulse"
    )

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.get("emergency_sos", language),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val msg = if (language == AppLanguage.TELUGU)
                                "అత్యవసర సహాయం పేజీ. ప్రమాదంలో ఉన్నప్పుడు పెద్ద ఎరుపు బటన్ నొక్కండి లేదా 108 అంబులెన్స్ కు కాల్ చేయండి."
                            else
                                "Emergency SOS screen. Press the red panic button for immediate distress alert, or dial 108 ambulance."
                            viewModel.speakIfEnabled(msg)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Read Aloud", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UpiDebitRed
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .testTag("sos_screen")
        ) {
            // Panic Beacon Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAlarmActive) Color(0xFF450A0A) else Color(0xFFFEF2F2)
                    ),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isAlarmActive)
                                AppStrings.get("alert_triggered", language)
                            else
                                AppStrings.get("panic_alert", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isAlarmActive) Color.White else UpiDebitRed,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Big Panic Circle
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(if (isAlarmActive) beaconPulse else 1f)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(UpiDebitRed.copy(alpha = 0.4f), Color.Transparent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFFDC2626), Color(0xFF991B1B)))
                                    )
                                    .clickable {
                                        isAlarmActive = !isAlarmActive
                                        if (isAlarmActive) {
                                            viewModel.triggerSosAlert()
                                        }
                                    }
                                    .testTag("sos_panic_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Emergency,
                                    contentDescription = "Panic SOS",
                                    tint = Color.White,
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isAlarmActive)
                                (if (language == AppLanguage.TELUGU) "హెచ్చరిక ఆపడానికి మళ్ళీ నొక్కండి" else "Tap again to cancel alarm")
                            else
                                (if (language == AppLanguage.TELUGU) "ఒక్క ట్యాప్‌తో అత్యవసర అలారం ప్రారంభించండి" else "One-tap rapid emergency distress siren"),
                            fontSize = 12.sp,
                            color = if (isAlarmActive) Color.White.copy(alpha = 0.8f) else Color(0xFF991B1B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Live GPS Location Tag Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = UpiDebitRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("current_coordinates", language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "📍 Banjara Hills Road No 12, Hyderabad, Telangana",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Lat: 17.4156° N, Long: 78.4357° E • Precision: 5 meters",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showSmsPreviewDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = UpiDebitRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("send_sos_sms", language),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Instant Emergency UPI Reserve Fund (₹2,000)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = UpiSuccessGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.TELUGU) "తక్షణ అత్యవసర వైద్య నిధి" else "Instant Emergency Medical Reserve",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == AppLanguage.TELUGU)
                                "క్లిష్ట పరిస్థితుల్లో ఆసుపత్రి అడ్మిషన్ లేదా మందుల కోసం 1-ట్యాప్‌తో ₹2,000 తక్షణమే విడుదల చేయండి."
                            else
                                "Instant 1-tap withdrawal of ₹2,000 for hospital admission or critical pharmacy medicines.",
                            fontSize = 12.sp,
                            color = Color(0xFF14532D)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.sendInstantEmergencyFund(2000.0) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("instant_medical_fund_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = UpiSuccessGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("instant_medical_fund", language),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Emergency Speed Dial Section
            item {
                Text(
                    text = AppStrings.get("emergency_speed_dial", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpeedDialCard(
                        title = AppStrings.get("ambulance", language),
                        number = "108",
                        color = Color(0xFFDC2626),
                        modifier = Modifier.weight(1f),
                        onDial = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                            context.startActivity(intent)
                        }
                    )

                    SpeedDialCard(
                        title = AppStrings.get("police", language),
                        number = "100",
                        color = Color(0xFF0F4C81),
                        modifier = Modifier.weight(1f),
                        onDial = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:100"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Emergency Contacts Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.get("sos_contacts", language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = { showAddContactDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = AppStrings.get("add_contact", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Contacts items
            items(emergencyContacts) { contact ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = contact.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${contact.relationship} • ${contact.phone}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = UpiSuccessGreen
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Add Emergency Contact Dialog
    if (showAddContactDialog) {
        var contactName by remember { mutableStateOf("") }
        var contactPhone by remember { mutableStateOf("") }
        var contactRel by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddContactDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = AppStrings.get("add_contact", language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text("Name / పేరు") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Phone Number / ఫోన్ నంబర్") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = contactRel,
                        onValueChange = { contactRel = it },
                        label = { Text("Relationship / బంధుత్వం") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddContactDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (contactName.isNotBlank() && contactPhone.isNotBlank()) {
                                    viewModel.addEmergencyContact(contactName, contactPhone, contactRel.ifBlank { "Emergency" })
                                    showAddContactDialog = false
                                }
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    // SOS SMS Preview Dialog
    if (showSmsPreviewDialog) {
        Dialog(onDismissRequest = { showSmsPreviewDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "SOS DISTRESS ALERT PREVIEW",
                        fontWeight = FontWeight.Bold,
                        color = UpiDebitRed,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF2F2))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "EMERGENCY ALERT!\nI need urgent help. My current live location is Banjara Hills Road No 12, Hyderabad (Lat: 17.4156, Long: 78.4357).\nPlease contact me immediately or dispatch assistance.",
                            fontSize = 13.sp,
                            color = Color(0xFF7F1D1D)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val msg = "EMERGENCY! I need immediate help. Location: Banjara Hills, Hyderabad (17.4156, 78.4357)."
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:108")).apply {
                                putExtra("sms_body", msg)
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback
                            }
                            showSmsPreviewDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = UpiDebitRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Send SOS Broadcast")
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeedDialCard(
    title: String,
    number: String,
    color: Color,
    modifier: Modifier = Modifier,
    onDial: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onDial() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = "Dial $number",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
