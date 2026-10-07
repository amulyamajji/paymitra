package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.UpiBlueLight
import com.example.ui.theme.UpiBluePrimary
import com.example.ui.theme.UpiCyanAccent
import com.example.ui.theme.UpiDarkNavy
import com.example.ui.theme.UpiDebitRed
import com.example.ui.theme.UpiSuccessGreen
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.UpiViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(viewModel: UpiViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val balance by viewModel.accountBalance.collectAsState()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsState()
    val budgetLimit by viewModel.monthlyBudgetLimit.collectAsState()
    val totalExpense by viewModel.totalMonthlyExpense.collectAsState()
    val recentTransactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val isAudioGuideEnabled by viewModel.isAudioGuideEnabled.collectAsState()

    val budgetProgress = (totalExpense / budgetLimit).toFloat().coerceIn(0f, 1f)
    val isNearBudgetLimit = totalExpense >= (budgetLimit * 0.85)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen")
    ) {
        // App Header Bar
        item {
            HeaderBar(
                language = language,
                isAudioGuideEnabled = isAudioGuideEnabled,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onToggleAudio = { viewModel.toggleAudioGuide() },
                onSosClick = { viewModel.navigateTo(Screen.SOS) },
                onSpeakHeader = {
                    val txt = if (language == AppLanguage.TELUGU)
                        "పేమిత్ర UPI హోమ్ పేజీ. ఖాతా బ్యాలెన్స్ చూడవచ్చు లేదా డబ్బులు పంపవచ్చు."
                    else
                        "PayMitra UPI Home. You can check balance, send money or view analytics."
                    viewModel.speakIfEnabled(txt)
                }
            )
        }

        // Primary Bank Card with Balance
        item {
            BankBalanceCard(
                balance = balance,
                isBalanceHidden = isBalanceHidden,
                language = language,
                onToggleHide = { viewModel.toggleBalancePrivacy() },
                onReadBalance = { viewModel.speakBalance() },
                onCheckBalance = {
                    viewModel.openPinSheet()
                }
            )
        }

        // Quick Transfer Grid
        item {
            QuickActionsGrid(
                language = language,
                onScanQr = { viewModel.openQrScanner() },
                onPayMobile = {
                    viewModel.setPaymentMode("MOBILE")
                    viewModel.navigateTo(Screen.PAY)
                },
                onPayUpi = {
                    viewModel.setPaymentMode("UPI_ID")
                    viewModel.navigateTo(Screen.PAY)
                },
                onBankTransfer = {
                    viewModel.setPaymentMode("BANK_TRANSFER")
                    viewModel.navigateTo(Screen.PAY)
                },
                onVoicePay = { viewModel.openVoiceCommandDialog() },
                onSosClick = { viewModel.navigateTo(Screen.SOS) }
            )
        }

        // Monthly Budget Widget
        item {
            MonthlyBudgetWidget(
                totalExpense = totalExpense,
                budgetLimit = budgetLimit,
                progress = budgetProgress,
                isWarning = isNearBudgetLimit,
                language = language,
                onClick = { viewModel.navigateTo(Screen.DASHBOARD) }
            )
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.TELUGU) "ఇటీవలి లావాదేవీలు" else "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { viewModel.navigateTo(Screen.HISTORY) }) {
                    Text(
                        text = if (language == AppLanguage.TELUGU) "అన్నీ చూడండి" else "View All",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Recent Transactions items
        if (recentTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (language == AppLanguage.TELUGU) "లావాదేవీలు ఏవీ లేవు" else "No recent transactions found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(recentTransactions.take(5)) { tx ->
                TransactionRowItem(
                    transaction = tx,
                    language = language,
                    onClick = { viewModel.showReceipt(tx) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun HeaderBar(
    language: AppLanguage,
    isAudioGuideEnabled: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleAudio: () -> Unit,
    onSosClick: () -> Unit,
    onSpeakHeader: () -> Unit
) {
    Surface(
        color = UpiBluePrimary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RK",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = AppStrings.get("greeting", language),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "ramesh@paymitra",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Audio Speak Header button
                IconButton(onClick = onSpeakHeader) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Read Aloud",
                        tint = if (isAudioGuideEnabled) UpiCyanAccent else Color.White.copy(alpha = 0.5f)
                    )
                }

                // Bilingual Toggle EN | TE
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .clickable { onToggleLanguage() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("language_toggle_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == AppLanguage.TELUGU) "తెలుగు" else "EN",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // SOS Quick Alert Button
                IconButton(
                    onClick = onSosClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(UpiDebitRed)
                        .testTag("home_sos_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "SOS",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BankBalanceCard(
    balance: Double,
    isBalanceHidden: Boolean,
    language: AppLanguage,
    onToggleHide: () -> Unit,
    onReadBalance: () -> Unit,
    onCheckBalance: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("bank_balance_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            UpiDarkNavy,
                            Color(0xFF0F3460)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "State Bank of India",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "A/C •••• 4821",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onReadBalance) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Read Balance",
                                tint = UpiCyanAccent
                            )
                        }
                        IconButton(onClick = onToggleHide) {
                            Icon(
                                imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Balance",
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = AppStrings.get("account_balance", language),
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isBalanceHidden) "••••••••" else "₹${"%,.2f".format(balance)}",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCheckBalance) {
                        Text(
                            text = AppStrings.get("check_balance", language),
                            color = UpiCyanAccent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(
    language: AppLanguage,
    onScanQr: () -> Unit,
    onPayMobile: () -> Unit,
    onPayUpi: () -> Unit,
    onBankTransfer: () -> Unit,
    onVoicePay: () -> Unit,
    onSosClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = if (language == AppLanguage.TELUGU) "డబ్బుల బదిలీ సేవలు" else "Payment Services",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionButton(
                    icon = Icons.Default.QrCodeScanner,
                    label = AppStrings.get("scan_qr", language),
                    color = UpiBluePrimary,
                    onClick = onScanQr,
                    tag = "quick_scan_qr"
                )

                QuickActionButton(
                    icon = Icons.Default.ContactPhone,
                    label = AppStrings.get("to_mobile", language),
                    color = Color(0xFF0284C7),
                    onClick = onPayMobile,
                    tag = "quick_to_mobile"
                )

                QuickActionButton(
                    icon = Icons.Default.Send,
                    label = AppStrings.get("to_upi", language),
                    color = Color(0xFF7C3AED),
                    onClick = onPayUpi,
                    tag = "quick_to_upi"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionButton(
                    icon = Icons.Default.AccountBalanceWallet,
                    label = AppStrings.get("bank_transfer", language),
                    color = Color(0xFF0D9488),
                    onClick = onBankTransfer,
                    tag = "quick_bank_transfer"
                )

                QuickActionButton(
                    icon = Icons.Default.Mic,
                    label = AppStrings.get("voice_pay", language),
                    color = Color(0xFFEA580C),
                    onClick = onVoicePay,
                    tag = "quick_voice_pay"
                )

                QuickActionButton(
                    icon = Icons.Default.Emergency,
                    label = AppStrings.get("quick_sos", language),
                    color = UpiDebitRed,
                    onClick = onSosClick,
                    tag = "quick_sos_button"
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(tag)
            .padding(4.dp)
            .width(88.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun MonthlyBudgetWidget(
    totalExpense: Double,
    budgetLimit: Double,
    progress: Float,
    isWarning: Boolean,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("monthly_budget_widget"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isWarning) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isWarning) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = UpiDebitRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = AppStrings.get("monthly_budget", language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isWarning) UpiDebitRed else MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (language == AppLanguage.TELUGU) "గ్రాఫ్ చూడండి" else "View Graph",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isWarning) UpiDebitRed else UpiSuccessGreen,
                trackColor = Color.LightGray.copy(alpha = 0.35f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${AppStrings.get("total_spent", language)}: ₹${"%,.0f".format(totalExpense)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${AppStrings.get("budget_limit", language)}: ₹${"%,.0f".format(budgetLimit)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    transaction: TransactionEntity,
    language: AppLanguage,
    onClick: () -> Unit
) {
    val isDebit = transaction.type == "DEBIT"
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)
    val dateStr = sdf.format(Date(transaction.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() }
            .testTag("transaction_item_${transaction.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Category badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDebit) Color(0xFFFEE2E2) else Color(0xFFD1FAE5)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (transaction.categoryKey) {
                            "hospital" -> "🏥"
                            "food" -> "🍔"
                            "travel" -> "⛽"
                            "bills" -> "💡"
                            "shopping" -> "🛍️"
                            "education" -> "📚"
                            "entertainment" -> "🎬"
                            else -> "💳"
                        },
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.payeeName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${if (language == AppLanguage.TELUGU) transaction.categoryNameTe else transaction.categoryNameEn} • $dateStr",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "📍 ${transaction.locationName}",
                        fontSize = 11.sp,
                        color = Color(0xFF0284C7),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isDebit) "-" else "+"}₹${"%,.0f".format(transaction.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isDebit) Color(0xFFDC2626) else UpiSuccessGreen
                )
                Text(
                    text = transaction.status,
                    fontSize = 10.sp,
                    color = UpiSuccessGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
