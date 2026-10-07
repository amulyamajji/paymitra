package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddCustomCategoryDialog
import com.example.ui.components.FingerprintLockOverlay
import com.example.ui.components.QrScannerDialog
import com.example.ui.components.TransactionReceiptDialog
import com.example.ui.components.UpiPinBottomSheet
import com.example.ui.components.VoiceCommandDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PayScreen
import com.example.ui.screens.SosScreen
import com.example.ui.screens.VoiceGuideScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.UpiBluePrimary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.UpiViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: UpiViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isBiometricLocked by viewModel.isBiometricLocked.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val payState by viewModel.payState.collectAsState()
    val selectedReceiptTx by viewModel.selectedReceiptTx.collectAsState()
    val showVoiceDialog by viewModel.showVoiceCommandDialog.collectAsState()
    val showCustomCatDialog by viewModel.showCustomCategoryDialog.collectAsState()
    val showQrScannerDialog by viewModel.showQrScannerDialog.collectAsState()

    // If biometric lock is enabled and currently locked, show security overlay
    if (isBiometricEnabled && isBiometricLocked) {
        FingerprintLockOverlay(
            language = language,
            onUnlock = { viewModel.unlockBiometric() },
            onSpeak = { text -> viewModel.speakIfEnabled(text) }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Hide bottom bar on SOS and PIN flow for focused emergency experience
            if (currentScreen != Screen.SOS) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val navItems = listOf(
                        Triple(Screen.HOME, Icons.Default.Home, AppStrings.get("home", language)),
                        Triple(Screen.PAY, Icons.Default.Payment, AppStrings.get("pay", language)),
                        Triple(Screen.DASHBOARD, Icons.Default.AutoGraph, AppStrings.get("dashboard", language)),
                        Triple(Screen.HISTORY, Icons.Default.History, AppStrings.get("history", language)),
                        Triple(Screen.VOICE_GUIDE, Icons.Default.RecordVoiceOver, AppStrings.get("voice_guide", language))
                    )

                    navItems.forEach { (screen, icon, label) ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(screen) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) UpiBluePrimary else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) UpiBluePrimary else Color.Gray,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFE0F2FE)
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(viewModel)
                Screen.PAY -> PayScreen(viewModel)
                Screen.DASHBOARD -> DashboardScreen(viewModel)
                Screen.HISTORY -> HistoryScreen(viewModel)
                Screen.SOS -> SosScreen(viewModel)
                Screen.VOICE_GUIDE -> VoiceGuideScreen(viewModel)
            }
        }

        // Global Modal Overlays & Sheets
        if (payState.isPinSheetVisible) {
            val amt = payState.amountText.toDoubleOrNull() ?: 0.0
            UpiPinBottomSheet(
                amount = amt,
                payeeName = payState.payeeName,
                language = language,
                onDismiss = { viewModel.closePinSheet() },
                onSubmitPin = { pin -> viewModel.submitPinAndPay(pin) }
            )
        }

        if (selectedReceiptTx != null) {
            TransactionReceiptDialog(
                transaction = selectedReceiptTx!!,
                language = language,
                onDismiss = { viewModel.closeReceipt() },
                onSpeakReceipt = { tx ->
                    val text = if (language == AppLanguage.TELUGU)
                        "${tx.payeeName} కు ${tx.amount.toInt()} రూపాయల లావాదేవీ విజయవంతమైంది. జరిగిన స్థలం: ${tx.locationName}."
                    else
                        "Transaction of ${tx.amount.toInt()} rupees to ${tx.payeeName} is successful at ${tx.locationName}."
                    viewModel.speakIfEnabled(text)
                },
                onRepeatPayment = { tx ->
                    viewModel.updatePayee(tx.payeeName, tx.upiId)
                    viewModel.updateAmount(tx.amount.toInt().toString())
                    viewModel.selectCategory(tx.categoryKey)
                    viewModel.updateLocation(tx.locationName)
                    viewModel.navigateTo(Screen.PAY)
                }
            )
        }

        if (showVoiceDialog) {
            VoiceCommandDialog(
                language = language,
                onDismiss = { viewModel.closeVoiceCommandDialog() },
                onCommandRecognized = { cmd -> viewModel.processVoiceCommand(cmd) }
            )
        }

        if (showCustomCatDialog) {
            AddCustomCategoryDialog(
                language = language,
                onDismiss = { viewModel.closeAddCategoryDialog() },
                onSaveCategory = { en, te, color, icon ->
                    viewModel.saveCustomCategory(en, te, color, icon)
                }
            )
        }

        if (showQrScannerDialog) {
            QrScannerDialog(
                language = language,
                onDismiss = { viewModel.closeQrScanner() },
                onQrScanned = { payee, upi, amt, note ->
                    viewModel.onQrScanned(payee, upi, amt, note)
                }
            )
        }
    }
}
