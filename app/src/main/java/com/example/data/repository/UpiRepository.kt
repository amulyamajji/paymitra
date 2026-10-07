package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.EmergencyContactEntity
import com.example.data.model.TransactionEntity
import com.example.util.AppLanguage
import com.example.util.AutoCategoryClassifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class UpiRepository(private val db: AppDatabase) {

    private val transactionDao = db.transactionDao()
    private val categoryDao = db.categoryDao()
    private val emergencyContactDao = db.emergencyContactDao()

    // In-memory persistent app state
    private val _accountBalance = MutableStateFlow(28450.0)
    val accountBalance: StateFlow<Double> = _accountBalance.asStateFlow()

    private val _monthlyBudgetLimit = MutableStateFlow(25000.0)
    val monthlyBudgetLimit: StateFlow<Double> = _monthlyBudgetLimit.asStateFlow()

    private val _isBiometricLocked = MutableStateFlow(true) // Startup lock for enhanced privacy
    val isBiometricLocked: StateFlow<Boolean> = _isBiometricLocked.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(true)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _isBalanceHidden = MutableStateFlow(false)
    val isBalanceHidden: StateFlow<Boolean> = _isBalanceHidden.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.TELUGU) // Default Telugu for high accessibility
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isAudioGuideEnabled = MutableStateFlow(true)
    val isAudioGuideEnabled: StateFlow<Boolean> = _isAudioGuideEnabled.asStateFlow()

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val emergencyContacts: Flow<List<EmergencyContactEntity>> = emergencyContactDao.getAllContacts()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        if (categoryDao.getCategoryCount() == 0) {
            val defaultCats = AutoCategoryClassifier.getAllDefaultCategories()
            defaultCats.forEach { cat ->
                categoryDao.insertCategory(
                    CategoryEntity(
                        categoryKey = cat.categoryKey,
                        nameEn = cat.nameEn,
                        nameTe = cat.nameTe,
                        iconName = cat.iconName,
                        colorHex = cat.colorHex,
                        isCustom = false
                    )
                )
            }
        }

        if (emergencyContactDao.getContactCount() == 0) {
            emergencyContactDao.insertContact(
                EmergencyContactEntity(
                    name = "Dr. Anitha (Family Doctor)",
                    phone = "+91 94401 23456",
                    relationship = "Doctor / Hospital",
                    isSosAlertEnabled = true
                )
            )
            emergencyContactDao.insertContact(
                EmergencyContactEntity(
                    name = "Suresh (Brother / తమ్ముడు)",
                    phone = "+91 98480 12345",
                    relationship = "Family",
                    isSosAlertEnabled = true
                )
            )
        }

        if (transactionDao.getTransactionCount() == 0) {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            val initialTxList = listOf(
                TransactionEntity(
                    title = "Apollo Pharmacy",
                    payeeName = "Apollo Pharmacy",
                    upiId = "apollopharmacy@icici",
                    amount = 1250.0,
                    type = "DEBIT",
                    categoryKey = "hospital",
                    categoryNameEn = "Hospital & Health",
                    categoryNameTe = "ఆసుపత్రి & ఆరోగ్యం",
                    note = "BP and sugar medicines",
                    timestamp = now - (dayMs * 1),
                    locationName = "Banjara Hills, Hyderabad",
                    latitude = 17.4156,
                    longitude = 78.4357,
                    upiRefId = "UPI/428938174829",
                    status = "SUCCESS",
                    paymentMode = "QR_SCAN"
                ),
                TransactionEntity(
                    title = "Swiggy Foods",
                    payeeName = "Swiggy Online",
                    upiId = "swiggy@axisbank",
                    amount = 640.0,
                    type = "DEBIT",
                    categoryKey = "food",
                    categoryNameEn = "Food & Dining",
                    categoryNameTe = "ఆహారం & భోజనం",
                    note = "Weekend dinner biryani",
                    timestamp = now - (dayMs * 2),
                    locationName = "Madhapur, Hyderabad",
                    latitude = 17.4483,
                    longitude = 78.3915,
                    upiRefId = "UPI/428919028471",
                    status = "SUCCESS",
                    paymentMode = "UPI_ID"
                ),
                TransactionEntity(
                    title = "HPCL Fuel Station",
                    payeeName = "HPCL Petrol Pump",
                    upiId = "hppetrol@sbi",
                    amount = 450.0,
                    type = "DEBIT",
                    categoryKey = "travel",
                    categoryNameEn = "Travel & Fuel",
                    categoryNameTe = "ప్రయాణం & ఇంధనం",
                    note = "Bike petrol tank full",
                    timestamp = now - (dayMs * 3),
                    locationName = "Gachibowli, Hyderabad",
                    latitude = 17.4401,
                    longitude = 78.3489,
                    upiRefId = "UPI/428801938201",
                    status = "SUCCESS",
                    paymentMode = "QR_SCAN"
                ),
                TransactionEntity(
                    title = "D-Mart Supermarket",
                    payeeName = "DMart Retail",
                    upiId = "dmartretail@hdfc",
                    amount = 2150.0,
                    type = "DEBIT",
                    categoryKey = "shopping",
                    categoryNameEn = "Shopping",
                    categoryNameTe = "షాపింగ్",
                    note = "Monthly groceries and essentials",
                    timestamp = now - (dayMs * 4),
                    locationName = "Kukatpally, Hyderabad",
                    latitude = 17.4849,
                    longitude = 78.4138,
                    upiRefId = "UPI/428710928374",
                    status = "SUCCESS",
                    paymentMode = "QR_SCAN"
                ),
                TransactionEntity(
                    title = "Yashoda Hospitals Lab",
                    payeeName = "Yashoda Diagnostics",
                    upiId = "yashoda@kotak",
                    amount = 2800.0,
                    type = "DEBIT",
                    categoryKey = "hospital",
                    categoryNameEn = "Hospital & Health",
                    categoryNameTe = "ఆసుపత్రి & ఆరోగ్యం",
                    note = "Annual body checkup tests",
                    timestamp = now - (dayMs * 6),
                    locationName = "Somajiguda, Hyderabad",
                    latitude = 17.4265,
                    longitude = 78.4593,
                    upiRefId = "UPI/428589102934",
                    status = "SUCCESS",
                    paymentMode = "UPI_ID"
                ),
                TransactionEntity(
                    title = "TSSPDCL Electricity",
                    payeeName = "Telangana Power Board",
                    upiId = "tsspdcl@billdesk",
                    amount = 890.0,
                    type = "DEBIT",
                    categoryKey = "bills",
                    categoryNameEn = "Bills & Utilities",
                    categoryNameTe = "బిల్లులు & యుటిలిటీస్",
                    note = "Home power bill for August",
                    timestamp = now - (dayMs * 8),
                    locationName = "Secunderabad, Telangana",
                    latitude = 17.4399,
                    longitude = 78.4983,
                    upiRefId = "UPI/428410293847",
                    status = "SUCCESS",
                    paymentMode = "UPI_ID"
                ),
                TransactionEntity(
                    title = "Suresh (Personal Transfer)",
                    payeeName = "Suresh Kumar",
                    upiId = "suresh@okicici",
                    amount = 5000.0,
                    type = "DEBIT",
                    categoryKey = "personal",
                    categoryNameEn = "Personal",
                    categoryNameTe = "వ్యక్తిగతం",
                    note = "Sent to brother for festival",
                    timestamp = now - (dayMs * 11),
                    locationName = "Vijayawada Central, AP",
                    latitude = 16.5062,
                    longitude = 80.6480,
                    upiRefId = "UPI/428109284736",
                    status = "SUCCESS",
                    paymentMode = "MOBILE"
                ),
                TransactionEntity(
                    title = "Salary Deposit",
                    payeeName = "TCS Payroll",
                    upiId = "tcspayroll@citi",
                    amount = 45000.0,
                    type = "CREDIT",
                    categoryKey = "personal",
                    categoryNameEn = "Personal",
                    categoryNameTe = "వ్యక్తిగతం",
                    note = "Monthly salary credit",
                    timestamp = now - (dayMs * 14),
                    locationName = "Hitec City, Hyderabad",
                    latitude = 17.4474,
                    longitude = 78.3762,
                    upiRefId = "UPI/427901827461",
                    status = "SUCCESS",
                    paymentMode = "BANK_TRANSFER"
                )
            )

            initialTxList.forEach { tx ->
                transactionDao.insertTransaction(tx)
            }
        }
    }

    suspend fun performPayment(
        payeeName: String,
        upiId: String,
        amount: Double,
        categoryKey: String,
        categoryNameEn: String,
        categoryNameTe: String,
        note: String,
        locationName: String,
        paymentMode: String
    ): Result<TransactionEntity> {
        val currentBal = _accountBalance.value
        if (currentBal < amount) {
            return Result.failure(IllegalStateException("Insufficient balance! / సరిపడా బ్యాలెన్స్ లేదు!"))
        }

        val refId = "UPI/${System.currentTimeMillis() % 100000000000L}"
        val transaction = TransactionEntity(
            title = "Paid to $payeeName",
            payeeName = payeeName,
            upiId = upiId,
            amount = amount,
            type = "DEBIT",
            categoryKey = categoryKey,
            categoryNameEn = categoryNameEn,
            categoryNameTe = categoryNameTe,
            note = note,
            timestamp = System.currentTimeMillis(),
            locationName = locationName,
            latitude = 17.3850 + (Math.random() - 0.5) * 0.05,
            longitude = 78.4867 + (Math.random() - 0.5) * 0.05,
            upiRefId = refId,
            status = "SUCCESS",
            paymentMode = paymentMode
        )

        val id = transactionDao.insertTransaction(transaction)
        _accountBalance.value = currentBal - amount
        return Result.success(transaction.copy(id = id))
    }

    suspend fun addCustomCategory(nameEn: String, nameTe: String, colorHex: String, iconName: String) {
        val key = "custom_${System.currentTimeMillis()}"
        val cat = CategoryEntity(
            categoryKey = key,
            nameEn = nameEn.ifBlank { "Custom Category" },
            nameTe = nameTe.ifBlank { nameEn },
            iconName = iconName,
            colorHex = colorHex,
            isCustom = true
        )
        categoryDao.insertCategory(cat)
    }

    suspend fun addEmergencyContact(name: String, phone: String, relationship: String) {
        emergencyContactDao.insertContact(
            EmergencyContactEntity(
                name = name,
                phone = phone,
                relationship = relationship,
                isSosAlertEnabled = true
            )
        )
    }

    fun updateBudgetLimit(limit: Double) {
        _monthlyBudgetLimit.value = limit
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.TELUGU) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.TELUGU
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun setBiometricLocked(locked: Boolean) {
        _isBiometricLocked.value = locked
    }

    fun toggleBiometricEnabled() {
        _isBiometricEnabled.value = !_isBiometricEnabled.value
    }

    fun toggleBalancePrivacy() {
        _isBalanceHidden.value = !_isBalanceHidden.value
    }

    fun toggleAudioGuide() {
        _isAudioGuideEnabled.value = !_isAudioGuideEnabled.value
    }
}
