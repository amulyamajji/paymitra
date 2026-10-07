package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UpiBluePrimary
import com.example.ui.viewmodel.UpiViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: UpiViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val transactions by viewModel.filteredTransactions.collectAsState()
    val searchQuery by viewModel.historySearchQuery.collectAsState()
    val selectedCategory by viewModel.historyCategoryFilter.collectAsState()
    val selectedType by viewModel.historyTypeFilter.collectAsState()

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.get("transaction_history", language),
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
                                "లావాదేవీల చరిత్ర. మొత్తం ${transactions.size} లావాదేవీలు ఉన్నాయి."
                            else
                                "Transaction history. Total ${transactions.size} transactions found."
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("history_screen")
        ) {
            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.historySearchQuery.value = it },
                    placeholder = {
                        Text(
                            text = AppStrings.get("search_transactions", language),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = UpiBluePrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.historySearchQuery.value = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Filter Chips Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Type filters
                listOf(
                    Pair("ALL", AppStrings.get("all", language)),
                    Pair("DEBIT", AppStrings.get("debit", language)),
                    Pair("CREDIT", AppStrings.get("credit", language))
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedType == key,
                        onClick = { viewModel.historyTypeFilter.value = key },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }

                // Category filters
                listOf(
                    Pair("hospital", AppStrings.get("cat_hospital", language)),
                    Pair("food", AppStrings.get("cat_food", language)),
                    Pair("travel", AppStrings.get("cat_travel", language)),
                    Pair("bills", AppStrings.get("cat_bills", language)),
                    Pair("personal", AppStrings.get("cat_personal", language)),
                    Pair("shopping", AppStrings.get("cat_shopping", language))
                ).forEach { (catKey, catLabel) ->
                    FilterChip(
                        selected = selectedCategory == catKey,
                        onClick = {
                            viewModel.historyCategoryFilter.value = if (selectedCategory == catKey) "ALL" else catKey
                        },
                        label = { Text(catLabel, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transaction items list
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (language == AppLanguage.TELUGU)
                            "లావాదేవీలు ఏవీ కనుగొనబడలేదు"
                        else
                            "No matching transactions found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(transactions) { tx ->
                        TransactionRowItem(
                            transaction = tx,
                            language = language,
                            onClick = { viewModel.showReceipt(tx) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
