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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.DailyExpenditureBarChart
import com.example.ui.components.MonthlyDonutChart
import com.example.ui.theme.UpiBluePrimary
import com.example.ui.theme.UpiDebitRed
import com.example.ui.theme.UpiSuccessGreen
import com.example.ui.viewmodel.UpiViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: UpiViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val totalExpense by viewModel.totalMonthlyExpense.collectAsState()
    val budgetLimit by viewModel.monthlyBudgetLimit.collectAsState()
    val categorySpends by viewModel.monthlySpendingData.collectAsState()
    val dailySpends by viewModel.dailySpendingList.collectAsState()

    var showBudgetDialog by remember { mutableStateOf(false) }
    var editBudgetInput by remember { mutableStateOf(budgetLimit.toInt().toString()) }

    val progress = (totalExpense / budgetLimit).toFloat().coerceIn(0f, 1f)
    val remainingBudget = (budgetLimit - totalExpense).coerceAtLeast(0.0)
    val isNearBudgetLimit = totalExpense >= (budgetLimit * 0.85)

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.get("spending_analytics", language),
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
                            val msg = if (language == AppLanguage.TELUGU) {
                                "ఈ నెలలో మీ మొత్తం ఖర్చు రూపాయలు ${totalExpense.toInt()}. మిగిలిన బడ్జెట్ రూపాయలు ${remainingBudget.toInt()}."
                            } else {
                                "Your total monthly spend is rupees ${totalExpense.toInt()}. Remaining budget is rupees ${remainingBudget.toInt()}."
                            }
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
                .padding(16.dp)
                .testTag("dashboard_screen")
        ) {
            // Budget Summary & Limit Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNearBudgetLimit) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isNearBudgetLimit) Icons.Default.Warning else Icons.Default.AutoGraph,
                                    contentDescription = null,
                                    tint = if (isNearBudgetLimit) UpiDebitRed else UpiBluePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = AppStrings.get("monthly_budget", language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(onClick = { showBudgetDialog = true }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Budget", tint = UpiBluePrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Spend vs Budget display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = AppStrings.get("total_spent", language),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${"%,.0f".format(totalExpense)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isNearBudgetLimit) UpiDebitRed else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = AppStrings.get("remaining_budget", language),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${"%,.0f".format(remainingBudget)}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = UpiSuccessGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = if (isNearBudgetLimit) UpiDebitRed else UpiSuccessGreen,
                            trackColor = Color.LightGray.copy(alpha = 0.4f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isNearBudgetLimit)
                                AppStrings.get("budget_warning", language)
                            else
                                AppStrings.get("budget_safe", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isNearBudgetLimit) UpiDebitRed else UpiSuccessGreen
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Visual Donut Chart for Category breakdown
            item {
                MonthlyDonutChart(
                    categorySpends = categorySpends,
                    totalExpense = totalExpense,
                    language = language
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Daily Spending Bar Chart for trends
            item {
                DailyExpenditureBarChart(
                    dailySpends = dailySpends,
                    language = language
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Financial Insights & Habit Highlights
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
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("spending_trends", language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val topCat = categorySpends.firstOrNull()
                        if (topCat != null) {
                            val topName = if (language == AppLanguage.TELUGU) topCat.nameTe else topCat.nameEn
                            InsightItem(
                                title = if (language == AppLanguage.TELUGU) "అత్యధిక ఖర్చు వర్గం" else "Top Expense Category",
                                desc = if (language == AppLanguage.TELUGU)
                                    "ఈ నెలలో అత్యధికంగా $topName పై ₹${topCat.amount.toInt()} (${topCat.percentage.toInt()}%) ఖర్చు చేశారు."
                                else
                                    "Highest expenditure was on $topName: ₹${topCat.amount.toInt()} (${topCat.percentage.toInt()}%)."
                            )
                        }

                        InsightItem(
                            title = if (language == AppLanguage.TELUGU) "ఆసుపత్రి & అత్యవసర పొదుపు" else "Hospital & Emergency Reserve",
                            desc = if (language == AppLanguage.TELUGU)
                                "ఆసుపత్రి మరియు వైద్య ఖర్చుల కోసం మీ బ్యాలెన్స్ నుండి ప్రత్యేక నిధి అందుబాటులో ఉంది."
                            else
                                "You have sufficient reserve for health and medical emergencies."
                        )

                        InsightItem(
                            title = if (language == AppLanguage.TELUGU) "లావాదేవీల స్థానాల విశ్లేషణ" else "Frequent Expense Location",
                            desc = if (language == AppLanguage.TELUGU)
                                "ఎక్కువ లావాదేవీలు హైదరాబాద్ (బంజారా హిల్స్, మాదాపూర్) పరిధిలో నమోదయ్యాయి."
                            else
                                "Most transactions occurred in Hyderabad (Banjara Hills, Madhapur)."
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Category Wise Breakdown list
            item {
                Text(
                    text = if (language == AppLanguage.TELUGU) "వర్గాల వారీగా పూర్తి వివరాలు" else "Category Breakdown List",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(categorySpends) { cat ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val sliceColor = try {
                                Color(android.graphics.Color.parseColor(cat.colorHex))
                            } catch (e: Exception) {
                                UpiBluePrimary
                            }

                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(sliceColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.TELUGU) cat.nameTe else cat.nameEn,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${cat.transactionCount} ${if (language == AppLanguage.TELUGU) "లావాదేవీలు" else "transactions"}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${"%,.0f".format(cat.amount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${cat.percentage.toInt()}%",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Edit Budget Dialog
    if (showBudgetDialog) {
        Dialog(onDismissRequest = { showBudgetDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = AppStrings.get("monthly_budget", language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (language == AppLanguage.TELUGU)
                            "నెలవారీ బడ్జెట్ పరిమితిని మార్చండి:"
                        else
                            "Update your monthly expense limit:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editBudgetInput,
                        onValueChange = { editBudgetInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Budget (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showBudgetDialog = false }) {
                            Text(if (language == AppLanguage.TELUGU) "రద్దు" else "Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val newLimit = editBudgetInput.toDoubleOrNull()
                                if (newLimit != null && newLimit > 0) {
                                    viewModel.updateMonthlyBudget(newLimit)
                                    showBudgetDialog = false
                                }
                            }
                        ) {
                            Text(if (language == AppLanguage.TELUGU) "భద్రపరుచు" else "Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightItem(title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = "• $title",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = desc,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp,
            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
        )
    }
}
