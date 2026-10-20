package com.example.data.repository

import com.example.data.dao.TycoonDao
import com.example.data.entity.GameStateEntity
import com.example.data.entity.HoldingEntity
import kotlinx.coroutines.flow.Flow

class TycoonRepository(private val dao: TycoonDao) {
    fun getGameStateFlow(): Flow<GameStateEntity?> = dao.getGameStateFlow()
    suspend fun getGameStateOnce(): GameStateEntity? = dao.getGameStateOnce()
    suspend fun saveGameState(state: GameStateEntity) = dao.insertGameState(state)

    fun getAllHoldingsFlow(): Flow<List<HoldingEntity>> = dao.getAllHoldingsFlow()
    suspend fun getAllHoldingsOnce(): List<HoldingEntity> = dao.getAllHoldingsOnce()
    suspend fun saveHolding(holding: HoldingEntity) = dao.insertHolding(holding)
    suspend fun deleteHolding(assetId: String) = dao.deleteHolding(assetId)
    suspend fun clearHoldings() = dao.clearAllHoldings()

    suspend fun resetGame() {
        dao.clearAllHoldings()
        dao.clearGameState()
    }
}
