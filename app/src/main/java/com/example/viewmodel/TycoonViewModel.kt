package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.TycoonDatabase
import com.example.data.entity.GameStateEntity
import com.example.data.entity.HoldingEntity
import com.example.data.repository.TycoonRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

data class OfflineEarningsReport(
    val elapsedSeconds: Long,
    val totalEarnings: Double,
    val enterprisesEarned: Double,
    val realEstateEarned: Double,
    val bankInterestEarned: Double
)

enum class TycoonTab {
    ENTERPRISES,
    MARKETS,
    EARNINGS,
    REAL_ESTATE,
    LUXURY
}

data class TycoonUiState(
    val cash: Double = 0.0,
    val bankDepositedCash: Double = 0.0,
    val currentClickTier: Int = 1,
    val enterprises: List<EnterpriseState> = EnterpriseCatalog.allConfigs.map { EnterpriseState(it, 0) },
    val marketAssets: List<MarketAsset> = InitialMarketCatalog.createInitialAssets(),
    val realEstate: List<RealEstateState> = RealEstateCatalog.allConfigs.map { RealEstateState(it, 0) },
    val luxuryAssets: List<LuxuryState> = LuxuryCatalog.allConfigs.map { LuxuryState(it, false) },
    val currentNewsEvent: EconomicEvent? = EconomicEventPool.SAMPLE_EVENTS.first(),
    val offlineReport: OfflineEarningsReport? = null,
    val dealsClosedCount: Int = 0,
    val activeTab: TycoonTab = TycoonTab.EARNINGS,
    val selectedTradingAsset: MarketAsset? = null,
    val showBankVaultDialog: Boolean = false,
    val showManagersSheet: Boolean = false,
    val showAscensionDialog: Boolean = false,
    val showMilestonesDialog: Boolean = false,
    val dealFeedbackAmount: Double? = null,
    val buyMultiplier: String = "1x", // "1x", "10x", "25x", "MAX"
    val tapCombo: Int = 0,
    val tapMultiplier: Double = 1.0,
    val angelShares: Int = 0
) {
    val currentBooster: ClickBooster
        get() = ClickBoosterCatalog.getBoosterForTier(currentClickTier)

    val currentPerClickAmount: Double
        get() = currentBooster.perClickAmount

    val nextBooster: ClickBooster?
        get() = ClickBoosterCatalog.getNextBooster(currentClickTier)

    val bankLevel: Int
        get() = enterprises.find { it.config.id == "commercial_bank" }?.level ?: 0

    val isBankUnlocked: Boolean
        get() = bankLevel > 0

    val angelBoostMultiplier: Double
        get() = 1.0 + (angelShares * 0.02)

    val hourlyEnterprisesIncome: Double
        get() = enterprises.sumOf { it.currentHourlyIncome }

    val hourlyRealEstateIncome: Double
        get() = realEstate.sumOf { it.totalHourlyRent }

    val hourlyBankInterest: Double
        get() = if (isBankUnlocked) bankDepositedCash * 0.035 else 0.0

    val totalHourlyPassiveIncome: Double
        get() = (hourlyEnterprisesIncome + hourlyRealEstateIncome + hourlyBankInterest) * angelBoostMultiplier

    val enterprisesValuation: Double
        get() = enterprises.sumOf { enterprise ->
            if (enterprise.level == 0) 0.0
            else enterprise.config.baseCost * enterprise.level * 1.2
        }

    val stocksValuation: Double
        get() = marketAssets.sumOf { it.totalValue }

    val realEstateValuation: Double
        get() = realEstate.sumOf { it.totalValuation }

    val luxuryValuation: Double
        get() = luxuryAssets.sumOf { it.currentValuation }

    val totalNetWorth: Double
        get() = cash + bankDepositedCash + enterprisesValuation + stocksValuation + realEstateValuation + luxuryValuation

    val totalPrestigePoints: Int
        get() = luxuryAssets.filter { it.isOwned }.sumOf { it.config.prestigePoints }

    val playerRank: PlayerRank
        get() = PlayerRank.calculateRank(totalPrestigePoints, totalNetWorth)

    val canAscend: Boolean
        get() = totalNetWorth >= 1_000_000.0

    val claimableAngelShares: Int
        get() = if (totalNetWorth >= 1_000_000.0) floor(sqrt(totalNetWorth / 1_000_000.0)).toInt() else 0
}

class TycoonViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TycoonRepository = TycoonRepository(
        TycoonDatabase.getDatabase(application).tycoonDao()
    )

    private val _uiState = MutableStateFlow(TycoonUiState())
    val uiState: StateFlow<TycoonUiState> = _uiState.asStateFlow()

    private var incomeTickerJob: Job? = null
    private var cycleProgressJob: Job? = null
    private var marketTickerJob: Job? = null
    private var comboDecayJob: Job? = null
    private var newsEventCounter = 0

    init {
        startFromScratch()
        startGameLoops()
    }

    fun startFromScratch() {
        viewModelScope.launch {
            repository.resetGame()
        }
        _uiState.value = TycoonUiState(
            cash = 0.0,
            bankDepositedCash = 0.0,
            currentClickTier = 1,
            enterprises = EnterpriseCatalog.allConfigs.map { EnterpriseState(it, 0) },
            marketAssets = InitialMarketCatalog.createInitialAssets(),
            realEstate = RealEstateCatalog.allConfigs.map { RealEstateState(it, 0) },
            luxuryAssets = LuxuryCatalog.allConfigs.map { LuxuryState(it, false) },
            offlineReport = null,
            dealsClosedCount = 0,
            activeTab = TycoonTab.EARNINGS,
            tapCombo = 0,
            tapMultiplier = 1.0,
            angelShares = 0
        )
    }

    private fun startGameLoops() {
        incomeTickerJob?.cancel()
        incomeTickerJob = viewModelScope.launch {
            var saveCounter = 0
            while (isActive) {
                delay(1000L)
                tickPassiveIncome()
                saveCounter++
                if (saveCounter >= 5) {
                    saveCounter = 0
                    persistGameState()
                }
            }
        }

        cycleProgressJob?.cancel()
        cycleProgressJob = viewModelScope.launch {
            while (isActive) {
                delay(100L)
                _uiState.update { current ->
                    val updated = current.enterprises.map { ent ->
                        if (ent.level > 0) {
                            val step = 0.1f / ent.config.cycleSeconds
                            var nextProg = ent.cycleProgress + step
                            if (nextProg >= 1f) nextProg = 0f
                            ent.copy(cycleProgress = nextProg)
                        } else {
                            ent
                        }
                    }
                    current.copy(enterprises = updated)
                }
            }
        }

        marketTickerJob?.cancel()
        marketTickerJob = viewModelScope.launch {
            while (isActive) {
                delay(3000L)
                tickFinancialMarkets()
            }
        }
    }

    private fun tickPassiveIncome() {
        _uiState.update { current ->
            val hourlyIncome = current.totalHourlyPassiveIncome
            if (hourlyIncome > 0) {
                val earnedPerSecond = hourlyIncome / 3600.0
                current.copy(cash = current.cash + earnedPerSecond)
            } else {
                current
            }
        }
    }

    private fun tickFinancialMarkets() {
        newsEventCounter++
        val shouldTriggerNews = newsEventCounter >= 6 && Random.nextFloat() < 0.65f

        val activeEvent = if (shouldTriggerNews) {
            newsEventCounter = 0
            EconomicEventPool.getRandomEvent()
        } else {
            _uiState.value.currentNewsEvent
        }

        _uiState.update { current ->
            val updatedMarkets = current.marketAssets.map { asset ->
                val prev = asset.currentPrice
                val randomFactor = (Random.nextDouble() - 0.48) * 2.0 * asset.volatility
                var deltaMultiplier = 1.0 + randomFactor + asset.drift

                if (activeEvent != null && shouldTriggerNews) {
                    if (activeEvent.affectedAssetId == asset.id) {
                        deltaMultiplier *= (1.0 + (activeEvent.priceImpactPercent / 100.0))
                    } else if (activeEvent.affectedAssetId == null) {
                        deltaMultiplier *= (1.0 + (activeEvent.priceImpactPercent / 200.0))
                    }
                }

                var nextPrice = prev * deltaMultiplier
                val minPrice = max(0.01, asset.basePrice * 0.15)
                val maxPrice = asset.basePrice * 10.0
                nextPrice = nextPrice.coerceIn(minPrice, maxPrice)

                val history = (asset.priceHistory + nextPrice).takeLast(15)

                asset.copy(
                    previousPrice = prev,
                    currentPrice = nextPrice,
                    priceHistory = history
                )
            }

            current.copy(
                marketAssets = updatedMarkets,
                currentNewsEvent = activeEvent
            )
        }
    }

    fun selectTab(tab: TycoonTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun setBuyMultiplier(multiplier: String) {
        _uiState.update { it.copy(buyMultiplier = multiplier) }
    }

    fun openTradingDialog(asset: MarketAsset) {
        _uiState.update { it.copy(selectedTradingAsset = asset) }
    }

    fun closeTradingDialog() {
        _uiState.update { it.copy(selectedTradingAsset = null) }
    }

    fun openBankVaultDialog() {
        _uiState.update { it.copy(showBankVaultDialog = true) }
    }

    fun closeBankVaultDialog() {
        _uiState.update { it.copy(showBankVaultDialog = false) }
    }

    fun openManagersSheet() {
        _uiState.update { it.copy(showManagersSheet = true) }
    }

    fun closeManagersSheet() {
        _uiState.update { it.copy(showManagersSheet = false) }
    }

    fun openAscensionDialog() {
        _uiState.update { it.copy(showAscensionDialog = true) }
    }

    fun closeAscensionDialog() {
        _uiState.update { it.copy(showAscensionDialog = false) }
    }

    fun openMilestonesDialog() {
        _uiState.update { it.copy(showMilestonesDialog = true) }
    }

    fun closeMilestonesDialog() {
        _uiState.update { it.copy(showMilestonesDialog = false) }
    }

    fun dismissOfflineReport() {
        _uiState.update { it.copy(offlineReport = null) }
        persistGameState()
    }

    fun upgradeEnterprise(configId: String) {
        val current = _uiState.value
        val enterprise = current.enterprises.find { it.config.id == configId } ?: return

        val (levelsToAdd, totalCost) = when (current.buyMultiplier) {
            "10x" -> Pair(10, enterprise.config.getCostForLevels(enterprise.level, 10))
            "25x" -> Pair(25, enterprise.config.getCostForLevels(enterprise.level, 25))
            "MAX" -> enterprise.config.getMaxAffordableLevels(enterprise.level, current.cash)
            else -> Pair(1, enterprise.upgradeCost)
        }

        if (current.cash >= totalCost && levelsToAdd > 0) {
            val newCash = current.cash - totalCost
            val newLevel = enterprise.level + levelsToAdd
            val updatedList = current.enterprises.map {
                if (it.config.id == configId) it.copy(level = newLevel) else it
            }

            _uiState.update {
                it.copy(
                    cash = newCash,
                    enterprises = updatedList
                )
            }

            viewModelScope.launch {
                repository.saveHolding(
                    HoldingEntity(
                        assetId = configId,
                        category = "ENTERPRISE",
                        countOrLevel = newLevel
                    )
                )
                persistGameState()
            }
        }
    }

    fun hireManager(enterpriseId: String) {
        val current = _uiState.value
        val enterprise = current.enterprises.find { it.config.id == enterpriseId } ?: return
        if (enterprise.isManagerHired) return
        val cost = enterprise.config.manager.cost

        if (current.cash >= cost) {
            val newCash = current.cash - cost
            val updatedList = current.enterprises.map {
                if (it.config.id == enterpriseId) it.copy(isManagerHired = true) else it
            }

            _uiState.update {
                it.copy(
                    cash = newCash,
                    enterprises = updatedList
                )
            }

            viewModelScope.launch {
                repository.saveHolding(
                    HoldingEntity(
                        assetId = "mgr_$enterpriseId",
                        category = "MANAGER",
                        countOrLevel = 1
                    )
                )
                persistGameState()
            }
        }
    }

    fun makeItRainTap() {
        val current = _uiState.value
        val newCombo = min(50, current.tapCombo + 1)
        val newMultiplier = when {
            newCombo >= 25 -> 5.0
            newCombo >= 15 -> 3.0
            newCombo >= 5 -> 2.0
            else -> 1.0
        }

        val baseEarn = current.currentPerClickAmount
        val totalEarn = baseEarn * newMultiplier

        _uiState.update {
            it.copy(
                cash = it.cash + totalEarn,
                dealsClosedCount = it.dealsClosedCount + 1,
                tapCombo = newCombo,
                tapMultiplier = newMultiplier,
                dealFeedbackAmount = totalEarn
            )
        }

        comboDecayJob?.cancel()
        comboDecayJob = viewModelScope.launch {
            delay(1500L)
            _uiState.update { it.copy(tapCombo = 0, tapMultiplier = 1.0, dealFeedbackAmount = null) }
        }
    }

    fun buyClickBooster(tier: Int) {
        val current = _uiState.value
        val targetBooster = ClickBoosterCatalog.getBoosterForTier(tier)
        if (tier != current.currentClickTier + 1) return
        if (current.cash >= targetBooster.cost) {
            _uiState.update {
                it.copy(
                    cash = it.cash - targetBooster.cost,
                    currentClickTier = tier
                )
            }
        }
    }

    fun ascendAndTakePublic() {
        val current = _uiState.value
        if (!current.canAscend) return
        val sharesToAdd = current.claimableAngelShares

        viewModelScope.launch {
            val totalAngelShares = current.angelShares + sharesToAdd

            repository.clearHoldings()
            repository.saveHolding(
                HoldingEntity(
                    assetId = "angel_shares",
                    category = "PRESTIGE",
                    countOrLevel = totalAngelShares
                )
            )

            val freshEnterprises = EnterpriseCatalog.allConfigs.map { EnterpriseState(it, 0) }
            val freshRealEstate = RealEstateCatalog.allConfigs.map { RealEstateState(it, 0) }
            val freshLuxury = LuxuryCatalog.allConfigs.map { LuxuryState(it, false) }
            val freshMarkets = InitialMarketCatalog.createInitialAssets()

            _uiState.update {
                it.copy(
                    cash = 0.0,
                    bankDepositedCash = 0.0,
                    enterprises = freshEnterprises,
                    realEstate = freshRealEstate,
                    luxuryAssets = freshLuxury,
                    marketAssets = freshMarkets,
                    angelShares = totalAngelShares,
                    showAscensionDialog = false
                )
            }

            persistGameState()
        }
    }

    fun buyMarketAsset(assetId: String, quantity: Double) {
        if (quantity <= 0.0) return
        val current = _uiState.value
        val asset = current.marketAssets.find { it.id == assetId } ?: return
        val totalCost = asset.currentPrice * quantity

        if (current.cash >= totalCost) {
            val newCash = current.cash - totalCost
            val existingUnits = asset.ownedUnits
            val existingCostBasis = asset.averageCostBasis * existingUnits
            val newTotalUnits = existingUnits + quantity
            val newAvgBasis = (existingCostBasis + totalCost) / newTotalUnits

            val updatedMarkets = current.marketAssets.map {
                if (it.id == assetId) {
                    it.copy(ownedUnits = newTotalUnits, averageCostBasis = newAvgBasis)
                } else it
            }

            _uiState.update {
                it.copy(
                    cash = newCash,
                    marketAssets = updatedMarkets,
                    selectedTradingAsset = updatedMarkets.find { a -> a.id == assetId }
                )
            }

            viewModelScope.launch {
                repository.saveHolding(
                    HoldingEntity(
                        assetId = assetId,
                        category = if (asset.isCrypto) "CRYPTO" else "STOCK",
                        countOrLevel = newTotalUnits.toInt(),
                        averageCostBasis = newAvgBasis
                    )
                )
                persistGameState()
            }
        }
    }

    fun sellMarketAsset(assetId: String, quantity: Double) {
        if (quantity <= 0.0) return
        val current = _uiState.value
        val asset = current.marketAssets.find { it.id == assetId } ?: return
        val actualQuantity = min(quantity, asset.ownedUnits)
        if (actualQuantity <= 0.0) return

        val proceeds = asset.currentPrice * actualQuantity
        val newUnits = asset.ownedUnits - actualQuantity
        val newAvgBasis = if (newUnits <= 0.0) 0.0 else asset.averageCostBasis

        val updatedMarkets = current.marketAssets.map {
            if (it.id == assetId) {
                it.copy(ownedUnits = newUnits, averageCostBasis = newAvgBasis)
            } else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash + proceeds,
                marketAssets = updatedMarkets,
                selectedTradingAsset = updatedMarkets.find { a -> a.id == assetId }
            )
        }

        viewModelScope.launch {
            repository.saveHolding(
                HoldingEntity(
                    assetId = assetId,
                    category = if (asset.isCrypto) "CRYPTO" else "STOCK",
                    countOrLevel = newUnits.toInt(),
                    averageCostBasis = newAvgBasis
                )
            )
            persistGameState()
        }
    }

    fun buyRealEstate(configId: String) {
        val current = _uiState.value
        val property = current.realEstate.find { it.config.id == configId } ?: return
        val cost = property.config.cost

        if (current.cash >= cost) {
            val newCash = current.cash - cost
            val newCount = property.ownedCount + 1
            val updatedList = current.realEstate.map {
                if (it.config.id == configId) it.copy(ownedCount = newCount) else it
            }

            _uiState.update {
                it.copy(cash = newCash, realEstate = updatedList)
            }

            viewModelScope.launch {
                repository.saveHolding(
                    HoldingEntity(
                        assetId = configId,
                        category = "REAL_ESTATE",
                        countOrLevel = newCount
                    )
                )
                persistGameState()
            }
        }
    }

    fun buyLuxury(configId: String) {
        val current = _uiState.value
        val luxury = current.luxuryAssets.find { it.config.id == configId } ?: return
        if (luxury.isOwned) return
        val cost = luxury.config.cost

        if (current.cash >= cost) {
            val newCash = current.cash - cost
            val updatedList = current.luxuryAssets.map {
                if (it.config.id == configId) it.copy(isOwned = true) else it
            }

            _uiState.update {
                it.copy(cash = newCash, luxuryAssets = updatedList)
            }

            viewModelScope.launch {
                repository.saveHolding(
                    HoldingEntity(
                        assetId = configId,
                        category = "LUXURY",
                        countOrLevel = 1
                    )
                )
                persistGameState()
            }
        }
    }

    fun depositToBank(amount: Double) {
        val current = _uiState.value
        if (!current.isBankUnlocked || amount <= 0.0) return
        val actualDeposit = min(amount, current.cash)

        if (actualDeposit > 0) {
            _uiState.update {
                it.copy(
                    cash = it.cash - actualDeposit,
                    bankDepositedCash = it.bankDepositedCash + actualDeposit
                )
            }
            persistGameState()
        }
    }

    fun withdrawFromBank(amount: Double) {
        val current = _uiState.value
        if (!current.isBankUnlocked || amount <= 0.0) return
        val actualWithdraw = min(amount, current.bankDepositedCash)

        if (actualWithdraw > 0) {
            _uiState.update {
                it.copy(
                    cash = it.cash + actualWithdraw,
                    bankDepositedCash = it.bankDepositedCash - actualWithdraw
                )
            }
            persistGameState()
        }
    }

    fun resetGame() {
        startFromScratch()
    }

    private fun persistGameState() {
        viewModelScope.launch {
            val state = _uiState.value
            repository.saveGameState(
                GameStateEntity(
                    id = 1,
                    cash = state.cash,
                    bankDepositedCash = state.bankDepositedCash,
                    currentClickTier = state.currentClickTier,
                    lastActiveTimestamp = System.currentTimeMillis(),
                    totalEarnedAllTime = state.cash,
                    dealsClosedCount = state.dealsClosedCount
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        incomeTickerJob?.cancel()
        cycleProgressJob?.cancel()
        marketTickerJob?.cancel()
        comboDecayJob?.cancel()
    }
}
