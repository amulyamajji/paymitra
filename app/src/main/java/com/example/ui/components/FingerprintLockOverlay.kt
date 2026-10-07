package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UpiBluePrimary
import com.example.ui.theme.UpiCyanAccent
import com.example.ui.theme.UpiDarkNavy
import com.example.ui.theme.UpiSuccessGreen
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun FingerprintLockOverlay(
    language: AppLanguage,
    onUnlock: () -> Unit,
    onSpeak: (String) -> Unit
) {
    var showPinDialog by remember { mutableStateOf(false) }
    var pinEntered by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("fingerprint_overlay"),
        color = UpiDarkNavy
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top App Logo & Security Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(UpiBluePrimary, UpiCyanAccent))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security Lock",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = AppStrings.get("fingerprint_title", language),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = AppStrings.get("fingerprint_desc", language),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.75f)
                    ),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = {
                        val msg = if (language == AppLanguage.TELUGU)
                            "పేమిత్ర భద్రతా లాక్. యాప్‌ను అన్‌లాక్ చేయడానికి వేలిముద్ర సెన్సార్‌ను తాకండి లేదా పిన్ 1234 ఉపయోగించండి."
                        else
                            "PayMitra security lock. Touch fingerprint sensor or use PIN 1234 to unlock."
                        onSpeak(msg)
                    },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Read Aloud",
                        tint = UpiCyanAccent
                    )
                }
            }

            // Fingerprint Sensor Target
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    UpiCyanAccent.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(UpiBluePrimary, Color(0xFF0284C7)))
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onUnlock()
                            }
                            .testTag("fingerprint_sensor_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Sensor",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = AppStrings.get("fingerprint_hint", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = UpiCyanAccent
                    )
                )
                Text(
                    text = if (language == AppLanguage.TELUGU) "(సెన్సార్‌పై నొక్కండి / Tap to verify)" else "(Tap sensor to verify)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }

            // Fallback PIN Options
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                OutlinedButton(
                    onClick = { showPinDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("use_pin_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Pin,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = UpiCyanAccent
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = AppStrings.get("use_upi_pin", language),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onUnlock
                ) {
                    Text(
                        text = if (language == AppLanguage.TELUGU) "త్వరిత అన్‌లాక్ (డెమో)" else "Quick Demo Unlock",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    // PIN Dialog
    if (showPinDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showPinDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = AppStrings.get("enter_upi_pin", language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (language == AppLanguage.TELUGU) "డిఫాల్ట్ పిన్ 1234 ఉపయోగించండి" else "Use default PIN 1234",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Masked PIN display
                    androidx.compose.foundation.layout.Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (i in 0 until 4) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i < pinEntered.length)
                                            if (pinError) MaterialTheme.colorScheme.error else UpiBluePrimary
                                        else
                                            Color.LightGray
                                    )
                            )
                        }
                    }

                    if (pinError) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == AppLanguage.TELUGU) "తప్పుడు పిన్! మళ్ళీ ప్రయత్నించండి" else "Incorrect PIN! Try 1234",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Numeric keypad for PIN
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val keypad = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("C", "0", "OK")
                        )

                        for (row in keypad) {
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (key in row) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (key == "OK") UpiSuccessGreen
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable {
                                                when (key) {
                                                    "C" -> {
                                                        if (pinEntered.isNotEmpty()) {
                                                            pinEntered = pinEntered.dropLast(1)
                                                        }
                                                        pinError = false
                                                    }
                                                    "OK" -> {
                                                        if (pinEntered == "1234") {
                                                            showPinDialog = false
                                                            onUnlock()
                                                        } else {
                                                            pinError = true
                                                        }
                                                    }
                                                    else -> {
                                                        if (pinEntered.length < 4) {
                                                            pinEntered += key
                                                            if (pinEntered.length == 4) {
                                                                if (pinEntered == "1234") {
                                                                    showPinDialog = false
                                                                    onUnlock()
                                                                } else {
                                                                    pinError = true
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = key,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = if (key == "OK") Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
