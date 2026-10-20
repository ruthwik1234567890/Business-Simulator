package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey
    val id: Int = 1,
    val cash: Double = 0.0,
    val bankDepositedCash: Double = 0.0,
    val currentClickTier: Int = 1,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val totalEarnedAllTime: Double = 0.0,
    val dealsClosedCount: Int = 0
)
