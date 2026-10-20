package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.GameStateEntity
import com.example.data.entity.HoldingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TycoonDao {
    @Query("SELECT * FROM game_state WHERE id = 1 LIMIT 1")
    fun getGameStateFlow(): Flow<GameStateEntity?>

    @Query("SELECT * FROM game_state WHERE id = 1 LIMIT 1")
    suspend fun getGameStateOnce(): GameStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(state: GameStateEntity)

    @Query("SELECT * FROM holdings")
    fun getAllHoldingsFlow(): Flow<List<HoldingEntity>>

    @Query("SELECT * FROM holdings")
    suspend fun getAllHoldingsOnce(): List<HoldingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: HoldingEntity)

    @Query("DELETE FROM holdings WHERE assetId = :assetId")
    suspend fun deleteHolding(assetId: String)

    @Query("DELETE FROM holdings")
    suspend fun clearAllHoldings()

    @Query("DELETE FROM game_state")
    suspend fun clearGameState()
}
