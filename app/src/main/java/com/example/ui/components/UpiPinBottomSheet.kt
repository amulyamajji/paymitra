package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UpiBluePrimary
import com.example.ui.theme.UpiSuccessGreen
import com.example.util.AppLanguage
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpiPinBottomSheet(
    amount: Double,
    payeeName: String,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmitPin: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var isPinError by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E293B),
        modifier = Modifier.testTag("upi_pin_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "NPCI UPI Secure",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "NPCI • 256-Bit UPI PIN",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transaction Summary Header
            Text(
                text = "${AppStrings.get("send_money", language)}: $payeeName",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₹${"%,.2f".format(amount)}",
                color = Color(0xFF38BDF8),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = AppStrings.get("enter_upi_pin", language),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PIN Dots Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < pin.length
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isPinError -> Color(0xFFEF4444)
                                    isFilled -> Color(0xFF38BDF8)
                                    else -> Color.DarkGray
                                }
                            )
                    )
                }
            }

            if (isPinError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == AppLanguage.TELUGU) "తప్పుడు పిన్! డిఫాల్ట్ పిన్ 1234" else "Wrong PIN! Use default: 1234",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == AppLanguage.TELUGU) "(డిఫాల్ట్ పిన్ 1234)" else "(Default UPI PIN is 1234)",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Numeric Keypad
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("DEL", "0", "OK")
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (row in keys) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (k in row) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (k) {
                                            "OK" -> UpiSuccessGreen
                                            "DEL" -> Color(0xFF334155)
                                            else -> Color(0xFF1E293B).copy(alpha = 0.8f)
                                        }
                                    )
                                    .clickable {
                                        when (k) {
                                            "DEL" -> {
                                                if (pin.isNotEmpty()) {
                                                    pin = pin.dropLast(1)
                                                }
                                                isPinError = false
                                            }
                                            "OK" -> {
                                                if (pin.length == 4) {
                                                    if (pin == "1234" || pin == "0000") {
                                                        onSubmitPin(pin)
                                                    } else {
                                                        isPinError = true
                                                    }
                                                }
                                            }
                                            else -> {
                                                if (pin.length < 4) {
                                                    pin += k
                                                    isPinError = false
                                                    if (pin.length == 4) {
                                                        if (pin == "1234" || pin == "0000") {
                                                            onSubmitPin(pin)
                                                        } else {
                                                            isPinError = true
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    .testTag("pin_key_$k"),
                                contentAlignment = Alignment.Center
                            ) {
                                when (k) {
                                    "DEL" -> Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Backspace",
                                        tint = Color.White
                                    )
                                    "OK" -> Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Confirm",
                                        tint = Color.White
                                    )
                                    else -> Text(
                                        text = k,
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
