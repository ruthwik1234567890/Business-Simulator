package com.example.viewmodel

import android.app.Application
import android.content.Context
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
    MY_SPACE
}

data class TycoonUiState(
    val cash: Double = 0.0,
    val bankDepositedCash: Double = 0.0,
    val currentClickTier: Int = 1,
    val enterprises: List<EnterpriseState> = EnterpriseCatalog.allConfigs.map { EnterpriseState(it, 0) },
    val marketAssets: List<MarketAsset> = InitialMarketCatalog.createInitialAssets(),
    val realEstate: List<RealEstateState> = RealEstateCatalog.allConfigs.map { RealEstateState(it, 0) },
    val luxuryAssets: List<LuxuryState> = LuxuryCatalog.allConfigs.map { LuxuryState(it, false) },
    val collectibles: List<CollectibleState> = UltimateCollectiblesCatalog.allConfigs.map { CollectibleState(it, false) },
    val currentNewsEvent: EconomicEvent? = EconomicEventPool.SAMPLE_EVENTS.first(),
    val offlineReport: OfflineEarningsReport? = null,
    val dealsClosedCount: Int = 0,
    val activeTab: TycoonTab = TycoonTab.EARNINGS,
    val isMySpaceOpen: Boolean = false,
    val selectedTradingAsset: MarketAsset? = null,
    val showBankVaultDialog: Boolean = false,
    val showManagersSheet: Boolean = false,
    val showAscensionDialog: Boolean = false,
    val showMilestonesDialog: Boolean = false,
    val dealFeedbackAmount: Double? = null,
    val buyMultiplier: String = "1x", // "1x", "10x", "25x", "MAX"
    val tapCombo: Int = 0,
    val tapMultiplier: Double = 1.0,
    val angelShares: Int = 0,

    // Tax Engine & Liquidation State
    val taxStatus: TaxStatusState = TaxStatusState(),
    val liquidationNotice: LiquidationSummary? = null,

    // Forbes 250 & Player Identity State
    val playerName: String = "Baron Cash",
    val hasPromptedPlayerName: Boolean = false,
    val hasCelebratedForbesRank: Boolean = false,
    val showNamePromptDialog: Boolean = false,
    val showForbesCelebrationDialog: Boolean = false
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

    val collectiblesValuation: Double
        get() = collectibles.sumOf { it.currentValuation }

    val totalNetWorth: Double
        get() = cash + bankDepositedCash + enterprisesValuation + stocksValuation + realEstateValuation + luxuryValuation + collectiblesValuation

    val totalPrestigePoints: Int
        get() = luxuryAssets.filter { it.isOwned }.sumOf { it.config.prestigePoints } +
                collectibles.filter { it.isOwned }.sumOf { it.config.prestigePoints }

    val playerRank: PlayerRank
        get() = PlayerRank.calculateRank(totalPrestigePoints, totalNetWorth)

    val canAscend: Boolean
        get() = totalNetWorth >= 1_000_000.0

    val claimableAngelShares: Int
        get() = if (totalNetWorth >= 1_000_000.0) floor(sqrt(totalNetWorth / 1_000_000.0)).toInt() else 0

    // Dynamic Forbes 250 Rank Calculation
    val forbesRank: Int?
        get() {
            val threshold = ForbesRichListCatalog.qualificationThreshold
            if (totalNetWorth < threshold) return null
            val higherCount = ForbesRichListCatalog.billionaires.count { it.netWorth > totalNetWorth }
            return (higherCount + 1).coerceAtMost(250)
        }
}

class TycoonViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TycoonRepository = TycoonRepository(
        TycoonDatabase.getDatabase(application).tycoonDao()
    )

    private val sharedPrefs = application.getSharedPreferences("tycoon_offline_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(TycoonUiState())
    val uiState: StateFlow<TycoonUiState> = _uiState.asStateFlow()

    private var incomeTickerJob: Job? = null
    private var cycleProgressJob: Job? = null
    private var marketTickerJob: Job? = null
    private var comboDecayJob: Job? = null
    private var newsEventCounter = 0

    init {
        loadPersistedGameOrStart()
        startGameLoops()
    }

    private fun loadPersistedGameOrStart() {
        val savedPlayerName = sharedPrefs.getString("player_name", "Baron Cash") ?: "Baron Cash"
        val hasPrompted = sharedPrefs.getBoolean("has_prompted_player_name", false)
        val hasCelebrated = sharedPrefs.getBoolean("has_celebrated_forbes_rank", false)

        _uiState.update {
            it.copy(
                playerName = savedPlayerName,
                hasPromptedPlayerName = hasPrompted,
                hasCelebratedForbesRank = hasCelebrated
            )
        }

        // Process initial resume / offline earnings check
        checkAndApplyOfflineEarnings()
    }

    fun onAppPaused() {
        sharedPrefs.edit().putLong("last_active_timestamp", System.currentTimeMillis()).apply()
        persistGameState()
    }

    fun onAppResumed() {
        checkAndApplyOfflineEarnings()
    }

    private fun checkAndApplyOfflineEarnings() {
        val lastTimestamp = sharedPrefs.getLong("last_active_timestamp", 0L)
        val now = System.currentTimeMillis()

        if (lastTimestamp > 0L) {
            val elapsedSeconds = (now - lastTimestamp) / 1000L
            if (elapsedSeconds >= 10L) {
                val current = _uiState.value
                val hoursAway = elapsedSeconds / 3600.0

                val enterprisesEarned = current.hourlyEnterprisesIncome * hoursAway
                val realEstateEarned = current.hourlyRealEstateIncome * hoursAway
                val bankInterestEarned = current.hourlyBankInterest * hoursAway
                val totalPassive = (enterprisesEarned + realEstateEarned + bankInterestEarned) * current.angelBoostMultiplier

                if (totalPassive > 0.0) {
                    // Award passive earnings into "My Pocket Cash"
                    val newCash = current.cash + totalPassive

                    // Add business & rental income to rolling taxable income
                    val taxablePortion = (enterprisesEarned + realEstateEarned)
                    val newTaxable = current.taxStatus.accumulatedTaxableIncome + taxablePortion

                    _uiState.update {
                        it.copy(
                            cash = newCash,
                            offlineReport = OfflineEarningsReport(
                                elapsedSeconds = elapsedSeconds,
                                totalEarnings = totalPassive,
                                enterprisesEarned = enterprisesEarned,
                                realEstateEarned = realEstateEarned,
                                bankInterestEarned = bankInterestEarned
                            ),
                            taxStatus = it.taxStatus.copy(accumulatedTaxableIncome = newTaxable)
                        )
                    }
                }
            }
        }

        // Record fresh timestamp
        sharedPrefs.edit().putLong("last_active_timestamp", now).apply()
    }

    private fun startGameLoops() {
        incomeTickerJob?.cancel()
        incomeTickerJob = viewModelScope.launch {
            var saveCounter = 0
            while (isActive) {
                delay(1000L)
                tickPassiveIncomeAndTaxes()
                saveCounter++
                if (saveCounter >= 5) {
                    saveCounter = 0
                    persistGameState()
                    sharedPrefs.edit().putLong("last_active_timestamp", System.currentTimeMillis()).apply()
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

    private fun tickPassiveIncomeAndTaxes() {
        _uiState.update { current ->
            val hourlyIncome = current.totalHourlyPassiveIncome
            val earnedPerSecond = if (hourlyIncome > 0) hourlyIncome / 3600.0 else 0.0
            val updatedCash = current.cash + earnedPerSecond

            // Add passive income from businesses & rental houses to taxable income
            val taxableEarnedPerSecond = (current.hourlyEnterprisesIncome + current.hourlyRealEstateIncome) / 3600.0
            val newAccumulatedTaxable = current.taxStatus.accumulatedTaxableIncome + taxableEarnedPerSecond

            // Advance rolling tax cycle timer
            val nextRollingSeconds = current.taxStatus.rollingWindowSeconds + 1
            val isWarning = nextRollingSeconds >= 900 // Minute 15 reached!
            val countdown = max(0, 1200 - nextRollingSeconds) // Minute 20 countdown

            val updatedTaxStatus = current.taxStatus.copy(
                rollingWindowSeconds = nextRollingSeconds,
                accumulatedTaxableIncome = newAccumulatedTaxable,
                isWarningActive = isWarning && (newAccumulatedTaxable * 0.025 > 0.0),
                warningRemainingSeconds = countdown
            )

            current.copy(
                cash = updatedCash,
                taxStatus = updatedTaxStatus
            )
        }

        // Check if minute 20 expired and taxes remain unpaid
        val stateAfterTick = _uiState.value
        if (stateAfterTick.taxStatus.rollingWindowSeconds >= 1200) {
            handleTaxCycleExpiration()
        }

        // Forbes 250 Qualification Check
        checkForbesQualification()
    }

    private fun handleTaxCycleExpiration() {
        val current = _uiState.value
        val taxDue = current.taxStatus.currentTaxDue

        if (taxDue > 0.0) {
            // Penalty Logic at 20 Minutes:
            // 1. All uncollected income is revoked
            // 2. Liquidation cascade starting from least valuable asset
            executeLiquidationCascade(taxDue)
        } else {
            // No tax owed; clean cycle reset
            _uiState.update {
                it.copy(
                    taxStatus = it.taxStatus.copy(
                        rollingWindowSeconds = 0,
                        accumulatedTaxableIncome = 0.0,
                        isWarningActive = false,
                        warningRemainingSeconds = 300
                    )
                )
            }
        }
    }

    private fun executeLiquidationCascade(debtToPay: Double) {
        val current = _uiState.value
        var remainingDebt = debtToPay
        val liquidatedNames = mutableListOf<String>()
        var totalValueLiquidated = 0.0
        var refundToPocket = 0.0

        // Mutable copies of assets
        var updatedMarkets = current.marketAssets.toMutableList()
        var updatedEnterprises = current.enterprises.toMutableList()
        var updatedRealEstate = current.realEstate.toMutableList()
        var updatedCollectibles = current.collectibles.toMutableList()

        // -------------------------------------------------------------
        // TIER 1: CRYPTO (Least valuable crypto position first)
        // -------------------------------------------------------------
        val ownedCryptos = updatedMarkets.filter { it.isCrypto && it.ownedUnits > 0 }
            .sortedBy { it.currentPrice * it.ownedUnits }

        for (crypto in ownedCryptos) {
            if (remainingDebt <= 0.0) break
            val assetVal = crypto.currentPrice * crypto.ownedUnits
            if (assetVal <= 0.0) continue

            totalValueLiquidated += assetVal
            liquidatedNames.add("${crypto.name} (${String.format("%.2f", crypto.ownedUnits)} units)")

            if (assetVal >= remainingDebt) {
                val excess = assetVal - remainingDebt
                refundToPocket += excess
                remainingDebt = 0.0
                // Liquidate asset
                val idx = updatedMarkets.indexOfFirst { it.id == crypto.id }
                if (idx != -1) updatedMarkets[idx] = crypto.copy(ownedUnits = 0.0)
                break
            } else {
                remainingDebt -= assetVal
                val idx = updatedMarkets.indexOfFirst { it.id == crypto.id }
                if (idx != -1) updatedMarkets[idx] = crypto.copy(ownedUnits = 0.0)
            }
        }

        // -------------------------------------------------------------
        // TIER 2: BUSINESSES (Lowest-income / least valuable enterprise first)
        // -------------------------------------------------------------
        if (remainingDebt > 0.0) {
            val ownedBusinesses = updatedEnterprises.filter { it.level > 0 }
                .sortedBy { it.config.baseCost * it.level * 1.2 }

            for (ent in ownedBusinesses) {
                if (remainingDebt <= 0.0) break
                val entVal = ent.config.baseCost * ent.level * 1.2
                if (entVal <= 0.0) continue

                totalValueLiquidated += entVal
                liquidatedNames.add("${ent.config.name} (Lvl ${ent.level})")

                if (entVal >= remainingDebt) {
                    val excess = entVal - remainingDebt
                    refundToPocket += excess
                    remainingDebt = 0.0
                    val idx = updatedEnterprises.indexOfFirst { it.config.id == ent.config.id }
                    if (idx != -1) updatedEnterprises[idx] = ent.copy(level = 0, isManagerHired = false)
                    break
                } else {
                    remainingDebt -= entVal
                    val idx = updatedEnterprises.indexOfFirst { it.config.id == ent.config.id }
                    if (idx != -1) updatedEnterprises[idx] = ent.copy(level = 0, isManagerHired = false)
                }
            }
        }

        // -------------------------------------------------------------
        // TIER 3: REAL ESTATE (Least valuable rental property first)
        // -------------------------------------------------------------
        if (remainingDebt > 0.0) {
            val ownedProperties = updatedRealEstate.filter { it.ownedCount > 0 }
                .sortedBy { it.config.cost * it.ownedCount }

            for (re in ownedProperties) {
                if (remainingDebt <= 0.0) break
                val reVal = re.config.cost * re.ownedCount
                if (reVal <= 0.0) continue

                totalValueLiquidated += reVal
                liquidatedNames.add("${re.config.name} (${re.ownedCount} owned)")

                if (reVal >= remainingDebt) {
                    val excess = reVal - remainingDebt
                    refundToPocket += excess
                    remainingDebt = 0.0
                    val idx = updatedRealEstate.indexOfFirst { it.config.id == re.config.id }
                    if (idx != -1) updatedRealEstate[idx] = re.copy(ownedCount = 0)
                    break
                } else {
                    remainingDebt -= reVal
                    val idx = updatedRealEstate.indexOfFirst { it.config.id == re.config.id }
                    if (idx != -1) updatedRealEstate[idx] = re.copy(ownedCount = 0)
                }
            }
        }

        // -------------------------------------------------------------
        // TIER 4: PERSONAL ASSETS / COLLECTIBLES (Least valuable first)
        // -------------------------------------------------------------
        if (remainingDebt > 0.0) {
            val ownedCollectibles = updatedCollectibles.filter { it.isOwned }
                .sortedBy { it.config.cost }

            for (col in ownedCollectibles) {
                if (remainingDebt <= 0.0) break
                val colVal = col.config.cost

                totalValueLiquidated += colVal
                liquidatedNames.add(col.config.name)

                if (colVal >= remainingDebt) {
                    val excess = colVal - remainingDebt
                    refundToPocket += excess
                    remainingDebt = 0.0
                    val idx = updatedCollectibles.indexOfFirst { it.config.id == col.config.id }
                    if (idx != -1) updatedCollectibles[idx] = col.copy(isOwned = false)
                    break
                } else {
                    remainingDebt -= colVal
                    val idx = updatedCollectibles.indexOfFirst { it.config.id == col.config.id }
                    if (idx != -1) updatedCollectibles[idx] = col.copy(isOwned = false)
                }
            }
        }

        val summary = LiquidationSummary(
            timestamp = System.currentTimeMillis(),
            totalDebtDemanded = debtToPay,
            liquidatedAssetsNames = liquidatedNames,
            totalValueLiquidated = totalValueLiquidated,
            refundPaidToPocket = refundToPocket,
            revokedIncomeAmount = current.taxStatus.accumulatedTaxableIncome
        )

        // Reset tax cycle and update user pocket cash with surplus refund
        _uiState.update {
            it.copy(
                cash = it.cash + refundToPocket,
                marketAssets = updatedMarkets,
                enterprises = updatedEnterprises,
                realEstate = updatedRealEstate,
                collectibles = updatedCollectibles,
                taxStatus = it.taxStatus.copy(
                    rollingWindowSeconds = 0,
                    accumulatedTaxableIncome = 0.0,
                    isWarningActive = false,
                    warningRemainingSeconds = 300,
                    lastLiquidationSummary = summary
                ),
                liquidationNotice = summary
            )
        }

        persistGameState()
    }

    fun dismissLiquidationNotice() {
        _uiState.update { it.copy(liquidationNotice = null) }
    }

    fun payTax() {
        val current = _uiState.value
        val taxDue = current.taxStatus.currentTaxDue
        if (taxDue <= 0.0) return

        if (current.cash >= taxDue) {
            _uiState.update {
                it.copy(
                    cash = it.cash - taxDue,
                    taxStatus = it.taxStatus.copy(
                        rollingWindowSeconds = 0,
                        accumulatedTaxableIncome = 0.0,
                        isWarningActive = false,
                        warningRemainingSeconds = 300,
                        lastPaidTaxAmount = taxDue,
                        totalTaxesPaidAllTime = it.taxStatus.totalTaxesPaidAllTime + taxDue
                    )
                )
            }
        } else if (current.cash + current.bankDepositedCash >= taxDue) {
            val fromCash = current.cash
            val fromBank = taxDue - fromCash
            _uiState.update {
                it.copy(
                    cash = 0.0,
                    bankDepositedCash = it.bankDepositedCash - fromBank,
                    taxStatus = it.taxStatus.copy(
                        rollingWindowSeconds = 0,
                        accumulatedTaxableIncome = 0.0,
                        isWarningActive = false,
                        warningRemainingSeconds = 300,
                        lastPaidTaxAmount = taxDue,
                        totalTaxesPaidAllTime = it.taxStatus.totalTaxesPaidAllTime + taxDue
                    )
                )
            }
        }
        persistGameState()
    }

    // -------------------------------------------------------------
    // FORBES 250 & PLAYER IDENTITY
    // -------------------------------------------------------------
    private fun checkForbesQualification() {
        val current = _uiState.value
        val rank = current.forbesRank

        if (rank != null) {
            if (!current.hasPromptedPlayerName && !current.showNamePromptDialog) {
                // First time qualifying! Prompt for player name
                _uiState.update { it.copy(showNamePromptDialog = true) }
            } else if (current.hasPromptedPlayerName && !current.hasCelebratedForbesRank && !current.showForbesCelebrationDialog) {
                // Grand celebration!
                _uiState.update { it.copy(showForbesCelebrationDialog = true) }
            }
        }
    }

    fun confirmPlayerName(name: String) {
        val cleanName = name.trim().ifBlank { "Baron Cash" }
        sharedPrefs.edit()
            .putString("player_name", cleanName)
            .putBoolean("has_prompted_player_name", true)
            .apply()

        _uiState.update {
            it.copy(
                playerName = cleanName,
                hasPromptedPlayerName = true,
                showNamePromptDialog = false,
                showForbesCelebrationDialog = true // Trigger celebration immediately!
            )
        }
    }

    fun dismissForbesCelebration() {
        sharedPrefs.edit().putBoolean("has_celebrated_forbes_rank", true).apply()
        _uiState.update {
            it.copy(
                hasCelebratedForbesRank = true,
                showForbesCelebrationDialog = false
            )
        }
    }

    fun reopenForbesCelebration() {
        _uiState.update { it.copy(showForbesCelebrationDialog = true) }
    }

    fun showNamePromptDialog() {
        _uiState.update { it.copy(showNamePromptDialog = true) }
    }

    // -------------------------------------------------------------
    // NAVIGATION & TAB SELECTION
    // -------------------------------------------------------------
    fun selectTab(tab: TycoonTab) {
        if (tab == TycoonTab.MY_SPACE) {
            _uiState.update { it.copy(isMySpaceOpen = true) }
        } else {
            _uiState.update { it.copy(activeTab = tab, isMySpaceOpen = false) }
        }
    }

    fun openMySpace() {
        _uiState.update { it.copy(isMySpaceOpen = true) }
    }

    fun closeMySpace() {
        _uiState.update { it.copy(isMySpaceOpen = false) }
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
                it.copy(cash = newCash, enterprises = updatedList)
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
                it.copy(cash = newCash, enterprises = updatedList)
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
            val freshCollectibles = UltimateCollectiblesCatalog.allConfigs.map { CollectibleState(it, false) }

            _uiState.update {
                it.copy(
                    cash = 0.0,
                    bankDepositedCash = 0.0,
                    enterprises = freshEnterprises,
                    realEstate = freshRealEstate,
                    luxuryAssets = freshLuxury,
                    collectibles = freshCollectibles,
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
        val costBasis = asset.averageCostBasis * actualQuantity
        val profit = proceeds - costBasis

        // If sold for profit, add realized profit to rolling taxable income!
        val realizedStockProfit = if (profit > 0.0) profit else 0.0
        val newTaxable = current.taxStatus.accumulatedTaxableIncome + realizedStockProfit

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
                selectedTradingAsset = updatedMarkets.find { a -> a.id == assetId },
                taxStatus = it.taxStatus.copy(accumulatedTaxableIncome = newTaxable)
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

    fun buyCollectible(configId: String) {
        val current = _uiState.value
        val collectible = current.collectibles.find { it.config.id == configId } ?: return
        if (collectible.isOwned) return
        val cost = collectible.config.cost

        if (current.cash >= cost) {
            val newCash = current.cash - cost
            val updatedList = current.collectibles.map {
                if (it.config.id == configId) it.copy(isOwned = true) else it
            }

            _uiState.update {
                it.copy(cash = newCash, collectibles = updatedList)
            }

            viewModelScope.launch {
                repository.saveHolding(
                    HoldingEntity(
                        assetId = configId,
                        category = "COLLECTIBLE",
                        countOrLevel = 1
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

    private fun startFromScratch() {
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
            collectibles = UltimateCollectiblesCatalog.allConfigs.map { CollectibleState(it, false) },
            offlineReport = null,
            dealsClosedCount = 0,
            activeTab = TycoonTab.EARNINGS,
            tapCombo = 0,
            tapMultiplier = 1.0,
            angelShares = 0
        )
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
