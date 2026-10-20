package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey
    val assetId: String,
    val category: String, // "ENTERPRISE", "STOCK", "CRYPTO", "REAL_ESTATE", "LUXURY", "MANAGER", "PRESTIGE"
    val countOrLevel: Int = 0,
    val averageCostBasis: Double = 0.0
)
