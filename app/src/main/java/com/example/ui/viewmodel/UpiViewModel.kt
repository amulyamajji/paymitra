package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.EmergencyContactEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.UpiRepository
import com.example.util.AppLanguage
import com.example.util.AutoCategoryClassifier
import com.example.util.AutoCategoryResult
import com.example.util.VoiceGuidanceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class Screen {
    HOME,
    PAY,
    DASHBOARD,
    HISTORY,
    SOS,
    VOICE_GUIDE
}

data class CategorySpend(
    val categoryKey: String,
    val nameEn: String,
    val nameTe: String,
    val colorHex: String,
    val iconName: String,
    val amount: Double,
    val percentage: Float,
    val transactionCount: Int
)

data class DailySpend(
    val dayLabel: String,
    val amount: Double
)

data class PayUiState(
    val payeeName: String = "",
    val upiId: String = "",
    val amountText: String = "",
    val note: String = "",
    val locationName: String = "Banjara Hills, Hyderabad",
    val paymentMode: String = "UPI_ID",
    val autoDetectedCategory: AutoCategoryResult? = null,
    val selectedCategoryKey: String = "food",
    val isPinSheetVisible: Boolean = false,
    val isPaying: Boolean = false,
    val errorMessage: String? = null,
    val completedTransaction: TransactionEntity? = null
)

class UpiViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = UpiRepository(db)
    val voiceManager = VoiceGuidanceManager(application)

    // Navigation
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen stack for BackHandler
    private val screenStack = mutableListOf(Screen.HOME)

    // UI Dialogs
    private val _showCustomCategoryDialog = MutableStateFlow(false)
    val showCustomCategoryDialog: StateFlow<Boolean> = _showCustomCategoryDialog.asStateFlow()

    private val _showVoiceCommandDialog = MutableStateFlow(false)
    val showVoiceCommandDialog: StateFlow<Boolean> = _showVoiceCommandDialog.asStateFlow()

    private val _showQrScannerDialog = MutableStateFlow(false)
    val showQrScannerDialog: StateFlow<Boolean> = _showQrScannerDialog.asStateFlow()

    private val _selectedReceiptTx = MutableStateFlow<TransactionEntity?>(null)
    val selectedReceiptTx: StateFlow<TransactionEntity?> = _selectedReceiptTx.asStateFlow()

    // Search and filters in History
    val historySearchQuery = MutableStateFlow("")
    val historyCategoryFilter = MutableStateFlow("ALL")
    val historyTypeFilter = MutableStateFlow("ALL") // ALL, DEBIT, CREDIT

    // Pay Flow UI State
    private val _payState = MutableStateFlow(PayUiState())
    val payState: StateFlow<PayUiState> = _payState.asStateFlow()

    // Repository Flows
    val accountBalance = repository.accountBalance
    val monthlyBudgetLimit = repository.monthlyBudgetLimit
    val isBiometricLocked = repository.isBiometricLocked
    val isBiometricEnabled = repository.isBiometricEnabled
    val isBalanceHidden = repository.isBalanceHidden
    val currentLanguage = repository.currentLanguage
    val isAudioGuideEnabled = repository.isAudioGuideEnabled
    val allTransactions = repository.allTransactions
    val allCategories = repository.allCategories
    val emergencyContacts = repository.emergencyContacts

    // Filtered Transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        historySearchQuery,
        historyCategoryFilter,
        historyTypeFilter
    ) { txList, query, catFilter, typeFilter ->
        txList.filter { tx ->
            val matchesQuery = query.isBlank() ||
                tx.payeeName.contains(query, ignoreCase = true) ||
                tx.title.contains(query, ignoreCase = true) ||
                tx.note.contains(query, ignoreCase = true) ||
                tx.locationName.contains(query, ignoreCase = true) ||
                tx.categoryNameEn.contains(query, ignoreCase = true) ||
                tx.categoryNameTe.contains(query, ignoreCase = true)

            val matchesCat = catFilter == "ALL" || tx.categoryKey.equals(catFilter, ignoreCase = true)
            val matchesType = typeFilter == "ALL" || tx.type.equals(typeFilter, ignoreCase = true)

            matchesQuery && matchesCat && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly Analytics calculations
    val monthlySpendingData: StateFlow<List<CategorySpend>> = allTransactions.mapCategorySpending()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalMonthlyExpense: StateFlow<Double> = allTransactions.mapTotalMonthlyExpense()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val dailySpendingList: StateFlow<List<DailySpend>> = allTransactions.mapDailySpending()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun unlockBiometric() {
        repository.setBiometricLocked(false)
        speakIfEnabled(
            if (currentLanguage.value == AppLanguage.TELUGU)
                "పేమిత్ర కి స్వాగతం. మీ ఖాతా సురక్షితంగా తెరవబడింది."
            else
                "Welcome to PayMitra. Account unlocked successfully."
        )
    }

    fun lockBiometric() {
        repository.setBiometricLocked(true)
    }

    fun toggleBiometricEnabled() {
        repository.toggleBiometricEnabled()
    }

    fun toggleBalancePrivacy() {
        repository.toggleBalancePrivacy()
    }

    fun toggleLanguage() {
        repository.toggleLanguage()
    }

    fun setLanguage(lang: AppLanguage) {
        repository.setLanguage(lang)
    }

    fun toggleAudioGuide() {
        repository.toggleAudioGuide()
    }

    fun speakIfEnabled(text: String) {
        if (isAudioGuideEnabled.value) {
            voiceManager.speak(text, currentLanguage.value)
        }
    }

    fun speakBalance() {
        voiceManager.speakBalance(accountBalance.value, currentLanguage.value)
    }

    // Payment Form methods
    fun updatePayee(name: String, upiId: String = "") {
        val detected = AutoCategoryClassifier.classify(name, upiId, _payState.value.note)
        _payState.value = _payState.value.copy(
            payeeName = name,
            upiId = if (upiId.isNotBlank()) upiId else if (name.contains("@")) name else "${name.lowercase().replace(" ", "")}@upi",
            autoDetectedCategory = detected,
            selectedCategoryKey = detected?.categoryKey ?: _payState.value.selectedCategoryKey
        )
    }

    fun updateAmount(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' }
        _payState.value = _payState.value.copy(amountText = filtered)
    }

    fun addQuickAmount(delta: Int) {
        val current = _payState.value.amountText.toDoubleOrNull() ?: 0.0
        val newAmt = (current + delta).toInt().toString()
        _payState.value = _payState.value.copy(amountText = newAmt)
    }

    fun updateNote(note: String) {
        val detected = AutoCategoryClassifier.classify(_payState.value.payeeName, _payState.value.upiId, note)
        _payState.value = _payState.value.copy(
            note = note,
            autoDetectedCategory = detected ?: _payState.value.autoDetectedCategory,
            selectedCategoryKey = detected?.categoryKey ?: _payState.value.selectedCategoryKey
        )
    }

    fun selectCategory(categoryKey: String) {
        _payState.value = _payState.value.copy(selectedCategoryKey = categoryKey)
    }

    fun updateLocation(loc: String) {
        _payState.value = _payState.value.copy(locationName = loc)
    }

    fun setPaymentMode(mode: String) {
        _payState.value = _payState.value.copy(paymentMode = mode)
    }

    fun openPinSheet() {
        val amt = _payState.value.amountText.toDoubleOrNull()
        if (amt == null || amt <= 0.0) {
            _payState.value = _payState.value.copy(errorMessage = "Please enter valid amount / సరైన మొత్తం నమోదు చేయండి")
            return
        }
        if (_payState.value.payeeName.isBlank()) {
            _payState.value = _payState.value.copy(errorMessage = "Please enter recipient / గ్రహీత వివరాలు నమోదు చేయండి")
            return
        }
        _payState.value = _payState.value.copy(isPinSheetVisible = true, errorMessage = null)
        speakIfEnabled(
            if (currentLanguage.value == AppLanguage.TELUGU)
                "దయచేసి మీ 4 అంకెల UPI పిన్ నమోదు చేయండి"
            else
                "Please enter your 4-digit UPI PIN"
        )
    }

    fun closePinSheet() {
        _payState.value = _payState.value.copy(isPinSheetVisible = false)
    }

    fun submitPinAndPay(pin: String) {
        if (pin != "1234" && pin != "0000" && pin.length != 4) {
            _payState.value = _payState.value.copy(errorMessage = "Incorrect UPI PIN! Default is 1234")
            speakIfEnabled("తప్పుడు UPI పిన్. డిఫాల్ట్ పిన్ 1234")
            return
        }

        val state = _payState.value
        val amount = state.amountText.toDoubleOrNull() ?: return
        val catInfo = AutoCategoryClassifier.getCategoryByKey(state.selectedCategoryKey)

        viewModelScope.launch {
            _payState.value = _payState.value.copy(isPaying = true, isPinSheetVisible = false)
            val result = repository.performPayment(
                payeeName = state.payeeName,
                upiId = state.upiId,
                amount = amount,
                categoryKey = state.selectedCategoryKey,
                categoryNameEn = catInfo.nameEn,
                categoryNameTe = catInfo.nameTe,
                note = state.note,
                locationName = state.locationName,
                paymentMode = state.paymentMode
            )

            result.onSuccess { tx ->
                _payState.value = _payState.value.copy(
                    isPaying = false,
                    completedTransaction = tx,
                    errorMessage = null
                )
                voiceManager.speakPaymentSuccess(tx.amount, tx.payeeName, currentLanguage.value)
            }.onFailure { err ->
                _payState.value = _payState.value.copy(
                    isPaying = false,
                    errorMessage = err.message
                )
            }
        }
    }

    fun clearPayState() {
        _payState.value = PayUiState()
    }

    // Custom Category
    fun openAddCategoryDialog() {
        _showCustomCategoryDialog.value = true
    }

    fun closeAddCategoryDialog() {
        _showCustomCategoryDialog.value = false
    }

    fun saveCustomCategory(nameEn: String, nameTe: String, colorHex: String, iconName: String) {
        viewModelScope.launch {
            repository.addCustomCategory(nameEn, nameTe, colorHex, iconName)
            _showCustomCategoryDialog.value = false
        }
    }

    // Voice Command
    fun openVoiceCommandDialog() {
        _showVoiceCommandDialog.value = true
        speakIfEnabled(
            if (currentLanguage.value == AppLanguage.TELUGU)
                "కమాండ్ చెప్పండి. ఉదాహరణకు: ఆహారానికి 500 చెల్లించండి లేదా బ్యాలెన్స్ ఎంత."
            else
                "Speak command. For example: Pay 500 for food or Check balance."
        )
    }

    fun closeVoiceCommandDialog() {
        _showVoiceCommandDialog.value = false
    }

    fun processVoiceCommand(command: String) {
        val lower = command.lowercase()
        _showVoiceCommandDialog.value = false

        when {
            lower.contains("balance") || lower.contains("బ్యాలెన్స్") || lower.contains("డబ్బులు ఎంత") -> {
                speakBalance()
            }
            lower.contains("sos") || lower.contains("emergency") || lower.contains("సహాయం") || lower.contains("ఆపద") -> {
                navigateTo(Screen.SOS)
                triggerSosAlert()
            }
            lower.contains("history") || lower.contains("చరిత్ర") -> {
                navigateTo(Screen.HISTORY)
            }
            lower.contains("analytics") || lower.contains("dashboard") || lower.contains("విశ్లేషణ") || lower.contains("గ్రాఫ్") -> {
                navigateTo(Screen.DASHBOARD)
            }
            lower.contains("pay") || lower.contains("చెల్లించు") || lower.contains("పంపు") -> {
                // Parse amount and category
                val numbers = Regex("""\d+""").findAll(lower).map { it.value }.toList()
                val detectedAmt = numbers.firstOrNull() ?: "500"
                var detectedPayee = "Suresh Kumar"
                if (lower.contains("hospital") || lower.contains("ఆసుపత్రి")) {
                    detectedPayee = "Apollo Pharmacy"
                } else if (lower.contains("food") || lower.contains("ఆహారం") || lower.contains("తిండి")) {
                    detectedPayee = "Swiggy Foods"
                }

                updatePayee(detectedPayee)
                updateAmount(detectedAmt)
                navigateTo(Screen.PAY)
                speakIfEnabled("డబ్బులు పంపడానికి స్క్రీన్ తెరవబడింది. మొత్తం రూపాయలు $detectedAmt.")
            }
            else -> {
                navigateTo(Screen.PAY)
            }
        }
    }

    // QR Scanner
    fun openQrScanner() {
        _showQrScannerDialog.value = true
    }

    fun closeQrScanner() {
        _showQrScannerDialog.value = false
    }

    fun onQrScanned(payee: String, upiId: String, suggestedAmount: String, note: String) {
        _showQrScannerDialog.value = false
        updatePayee(payee, upiId)
        if (suggestedAmount.isNotBlank()) {
            updateAmount(suggestedAmount)
        }
        if (note.isNotBlank()) {
            updateNote(note)
        }
        setPaymentMode("QR_SCAN")
        navigateTo(Screen.PAY)
    }

    // Receipt Dialog
    fun showReceipt(tx: TransactionEntity) {
        _selectedReceiptTx.value = tx
    }

    fun closeReceipt() {
        _selectedReceiptTx.value = null
    }

    // SOS Emergency
    fun triggerSosAlert() {
        voiceManager.speakSosAlert(currentLanguage.value)
    }

    fun sendInstantEmergencyFund(amount: Double = 2000.0) {
        viewModelScope.launch {
            repository.performPayment(
                payeeName = "Apollo Emergency Care",
                upiId = "apolloemergency@icici",
                amount = amount,
                categoryKey = "hospital",
                categoryNameEn = "Hospital & Health",
                categoryNameTe = "ఆసుపత్రి & ఆరోగ్యం",
                note = "CRITICAL EMERGENCY HOSPITAL FUND / అత్యవసర నిధి",
                locationName = "Emergency Ward, Hyderabad",
                paymentMode = "UPI_ID"
            )
            voiceManager.speak(
                if (currentLanguage.value == AppLanguage.TELUGU)
                    "అత్యవసర నిధి రూపాయలు ${amount.toInt()} తక్షణమే అపోలో ఎమర్జెన్సీ కి బదిలీ చేయబడింది."
                else
                    "Emergency medical fund of rupees ${amount.toInt()} transferred instantly to Apollo Emergency.",
                currentLanguage.value
            )
        }
    }

    fun addEmergencyContact(name: String, phone: String, relationship: String) {
        viewModelScope.launch {
            repository.addEmergencyContact(name, phone, relationship)
        }
    }

    fun updateMonthlyBudget(newLimit: Double) {
        repository.updateBudgetLimit(newLimit)
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
    }
}

// Extension functions for calculating flows
private fun Flow<List<TransactionEntity>>.mapCategorySpending(): Flow<List<CategorySpend>> {
    return this.map { list: List<TransactionEntity> ->
        val debits = list.filter { it.type == "DEBIT" }
        val totalDebits = debits.sumOf { it.amount }
        if (totalDebits == 0.0) return@map emptyList<CategorySpend>()

        val grouped = debits.groupBy { it.categoryKey }
        grouped.map { (catKey: String, txGroup: List<TransactionEntity>) ->
            val catTotal = txGroup.sumOf { it.amount }
            val first = txGroup.first()
            val catInfo = AutoCategoryClassifier.getCategoryByKey(catKey)
            CategorySpend(
                categoryKey = catKey,
                nameEn = first.categoryNameEn.ifBlank { catInfo.nameEn },
                nameTe = first.categoryNameTe.ifBlank { catInfo.nameTe },
                colorHex = catInfo.colorHex,
                iconName = catInfo.iconName,
                amount = catTotal,
                percentage = ((catTotal / totalDebits) * 100).toFloat(),
                transactionCount = txGroup.size
            )
        }.sortedByDescending { it.amount }
    }
}

private fun Flow<List<TransactionEntity>>.mapTotalMonthlyExpense(): Flow<Double> {
    return this.map { list: List<TransactionEntity> ->
        list.filter { it.type == "DEBIT" }.sumOf { it.amount }
    }
}

private fun Flow<List<TransactionEntity>>.mapDailySpending(): Flow<List<DailySpend>> {
    return this.map { list: List<TransactionEntity> ->
        val sdf = SimpleDateFormat("dd MMM", Locale.ENGLISH)
        val debits = list.filter { it.type == "DEBIT" }
        val groupedByDay = debits.groupBy { sdf.format(Date(it.timestamp)) }

        groupedByDay.map { (day: String, txs: List<TransactionEntity>) ->
            DailySpend(
                dayLabel = day,
                amount = txs.sumOf { it.amount }
            )
        }.takeLast(7)
    }
}
