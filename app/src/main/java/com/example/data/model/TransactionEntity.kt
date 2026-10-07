package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val payeeName: String,
    val upiId: String,
    val amount: Double,
    val type: String = "DEBIT", // DEBIT or CREDIT
    val categoryKey: String,
    val categoryNameEn: String,
    val categoryNameTe: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val locationName: String = "Hyderabad, Telangana",
    val latitude: Double = 17.3850,
    val longitude: Double = 78.4867,
    val upiRefId: String = "UPI/${System.currentTimeMillis() % 10000000000L}",
    val status: String = "SUCCESS", // SUCCESS, PENDING, FAILED
    val paymentMode: String = "UPI_ID" // QR_SCAN, UPI_ID, MOBILE, BANK_TRANSFER
)
