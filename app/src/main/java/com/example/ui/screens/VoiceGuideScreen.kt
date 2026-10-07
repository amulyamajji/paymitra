package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UpiBluePrimary
import com.example.ui.theme.UpiCyanAccent
import com.example.ui.theme.UpiDebitRed
import com.example.ui.theme.UpiSuccessGreen
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.UpiViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceGuideScreen(viewModel: UpiViewModel) {
    val language by viewModel.currentLanguage.collectAsState()

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.get("voice_guide", language),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val msg = if (language == AppLanguage.TELUGU)
                                "చదువురాని వారి కోసం సులభమైన వాయిస్ గైడ్. ప్రతి బటన్‌ను తాకితే అది మాట్లాడి వినిపిస్తుంది."
                            else
                                "Spoken voice guide for accessible navigation. Tap any tile to hear its purpose."
                            viewModel.speakIfEnabled(msg)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Read Aloud", tint = UpiBluePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
                .testTag("voice_guide_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big Assistant Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(UpiBluePrimary, UpiCyanAccent))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (language == AppLanguage.TELUGU)
                                "ధ్వని సహాయక కేంద్రం"
                            else
                                "Audio Voice Assistance",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (language == AppLanguage.TELUGU)
                                "చదువుతో సంబంధం లేకుండా, మాట్లాడి డబ్బులు పంపండి లేదా ఖాతా వివరాలు వినండి."
                            else
                                "Designed for seamless spoken guidance and voice command transactions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.openVoiceCommandDialog() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = UpiBluePrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.TELUGU) "ఇప్పుడే మాట్లాడండి (వాయిస్ కమాండ్)" else "Speak Voice Command Now",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Big Accessibility Voice Tiles
            item {
                Text(
                    text = if (language == AppLanguage.TELUGU) "సులభమైన సూచనలు (తాకితే వినిపిస్తుంది):" else "Interactive Spoken Tiles (Tap to hear):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tile 1: Check Balance
            item {
                VoiceActionTile(
                    title = if (language == AppLanguage.TELUGU) "ఖాతాలో డబ్బులు ఎంత ఉన్నాయో వినండి" else "Hear Account Balance",
                    subtitle = if (language == AppLanguage.TELUGU) "మీ స్టేట్ బ్యాంక్ బ్యాలెన్స్ గట్టిగా వినిపిస్తుంది" else "Speaks out your current balance aloud",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = Color(0xFF0284C7),
                    onClick = {
                        viewModel.speakBalance()
                    }
                )
            }

            // Tile 2: Send Money for Food
            item {
                VoiceActionTile(
                    title = if (language == AppLanguage.TELUGU) "ఆహారం / భోజనం కోసం డబ్బులు పంపండి" else "Pay for Food & Dining",
                    subtitle = if (language == AppLanguage.TELUGU) "హోటల్, టిఫిన్ లేదా స్విగ్గీ ఖర్చుల చెల్లింపు" else "Quick payment tagged automatically as Food",
                    icon = Icons.Default.Fastfood,
                    color = Color(0xFFEA580C),
                    onClick = {
                        viewModel.updatePayee("Swiggy Foods")
                        viewModel.updateAmount("350")
                        viewModel.navigateTo(Screen.PAY)
                        val msg = if (language == AppLanguage.TELUGU)
                            "ఆహారం కోసం 350 రూపాయలు చెల్లించడానికి సిద్ధంగా ఉంది. చెల్లించు బటన్ నొక్కండి."
                        else
                            "Ready to pay rupees 350 for food. Tap Pay button."
                        viewModel.speakIfEnabled(msg)
                    }
                )
            }

            // Tile 3: Send Money for Hospital / Doctor
            item {
                VoiceActionTile(
                    title = if (language == AppLanguage.TELUGU) "డాక్టర్ లేదా మందుల కోసం చెల్లించండి" else "Pay Doctor or Medicines",
                    subtitle = if (language == AppLanguage.TELUGU) "ఆసుపత్రి ఖర్చుల కింద స్వయంచాలకంగా నమోదవుతుంది" else "Quick payment tagged automatically as Hospital",
                    icon = Icons.Default.LocalHospital,
                    color = UpiDebitRed,
                    onClick = {
                        viewModel.updatePayee("Apollo Pharmacy")
                        viewModel.updateAmount("850")
                        viewModel.navigateTo(Screen.PAY)
                        val msg = if (language == AppLanguage.TELUGU)
                            "మందుల కోసం అపోలో ఫార్మసీ కి 850 రూపాయలు చెల్లించడానికి సిద్ధంగా ఉంది."
                        else
                            "Ready to pay rupees 850 to Apollo Pharmacy for medicines."
                        viewModel.speakIfEnabled(msg)
                    }
                )
            }

            // Tile 4: Scan QR Code
            item {
                VoiceActionTile(
                    title = if (language == AppLanguage.TELUGU) "అంగడిలో QR కోడ్ స్కాన్ చేయండి" else "Scan QR Code at Shop",
                    subtitle = if (language == AppLanguage.TELUGU) "ఫోన్ కెమెరాతో స్కాన్ చేసి చెల్లించండి" else "Point camera at any merchant QR",
                    icon = Icons.Default.QrCodeScanner,
                    color = Color(0xFF7C3AED),
                    onClick = {
                        val msg = if (language == AppLanguage.TELUGU)
                            "QR కోడ్ స్కానర్ తెరవబడింది. వ్యాపారి కోడ్ చూపించండి."
                        else
                            "QR scanner opened. Show the merchant QR code."
                        viewModel.speakIfEnabled(msg)
                        viewModel.openQrScanner()
                    }
                )
            }

            // Tile 5: Emergency SOS Help
            item {
                VoiceActionTile(
                    title = if (language == AppLanguage.TELUGU) "అత్యవసర సహాయం (ఆపద వచ్చినప్పుడు)" else "Emergency SOS Distress",
                    subtitle = if (language == AppLanguage.TELUGU) "పోలీసులు లేదా అంబులెన్స్ కి తక్షణ సమాచారం" else "Alert contacts and dial 108 ambulance",
                    icon = Icons.Default.Emergency,
                    color = UpiDebitRed,
                    onClick = {
                        viewModel.navigateTo(Screen.SOS)
                        viewModel.triggerSosAlert()
                    }
                )
            }

            // Tile 6: Transaction History
            item {
                VoiceActionTile(
                    title = if (language == AppLanguage.TELUGU) "డబ్బులు ఎవరికి ఎంత ఇచ్చారో చూడండి" else "Check Transaction History",
                    subtitle = if (language == AppLanguage.TELUGU) "గతంలో జరిగిన ఖర్చుల పూర్తి జాబితా" else "Full list of past payments with location",
                    icon = Icons.Default.History,
                    color = UpiSuccessGreen,
                    onClick = {
                        val msg = if (language == AppLanguage.TELUGU)
                            "మీ పాత లావాదేవీల జాబితా తెరవబడింది."
                        else
                            "Your past transaction history is opened."
                        viewModel.speakIfEnabled(msg)
                        viewModel.navigateTo(Screen.HISTORY)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun VoiceActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Speak",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
