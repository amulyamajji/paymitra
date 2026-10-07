package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryKey: String,
    val nameEn: String,
    val nameTe: String,
    val iconName: String = "Category",
    val colorHex: String = "#3B82F6",
    val isCustom: Boolean = false,
    val monthlyBudget: Double = 0.0
)
