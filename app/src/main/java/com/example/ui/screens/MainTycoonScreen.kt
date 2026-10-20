package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.TycoonTab
import com.example.viewmodel.TycoonViewModel

@Composable
fun MainTycoonScreen(
    viewModel: TycoonViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SoftDarkBg,
        topBar = {
            Column {
                TopDashboardBar(
                    uiState = uiState,
                    onResetGame = { viewModel.resetGame() },
                    onOpenBankVault = { viewModel.openBankVaultDialog() },
                    onOpenManagers = { viewModel.openManagersSheet() },
                    onOpenAscension = { viewModel.openAscensionDialog() },
                    onOpenMilestones = { viewModel.openMilestonesDialog() }
                )
                NewsTickerBar(currentEvent = uiState.currentNewsEvent)
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SoftCardBg,
                contentColor = TextMutedSoft,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("tycoon_navigation_bar")
            ) {
                // Tab 1: Businesses (simple well-known term instead of "Ventures")
                NavigationBarItem(
                    selected = uiState.activeTab == TycoonTab.ENTERPRISES,
                    onClick = { viewModel.selectTab(TycoonTab.ENTERPRISES) },
                    icon = { Icon(Icons.Default.Business, contentDescription = "Businesses") },
                    label = {
                        Text(
                            text = "Businesses",
                            fontSize = 11.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = if (uiState.activeTab == TycoonTab.ENTERPRISES) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0F172A),
                        selectedTextColor = BrightGold,
                        indicatorColor = BrightGold,
                        unselectedIconColor = TextMutedSoft,
                        unselectedTextColor = TextMutedSoft
                    ),
                    modifier = Modifier.testTag("tab_enterprises")
                )

                // Tab 2: Stocks (simple well-known term instead of "Markets")
                NavigationBarItem(
                    selected = uiState.activeTab == TycoonTab.MARKETS,
                    onClick = { viewModel.selectTab(TycoonTab.MARKETS) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Stocks") },
                    label = {
                        Text(
                            text = "Stocks",
                            fontSize = 11.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = if (uiState.activeTab == TycoonTab.MARKETS) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0F172A),
                        selectedTextColor = VividBlue,
                        indicatorColor = VividBlue,
                        unselectedIconColor = TextMutedSoft,
                        unselectedTextColor = TextMutedSoft
                    ),
                    modifier = Modifier.testTag("tab_markets")
                )

                // Tab 3 (CENTER): Tap Cash (simple well-known term instead of "Earnings")
                NavigationBarItem(
                    selected = uiState.activeTab == TycoonTab.EARNINGS,
                    onClick = { viewModel.selectTab(TycoonTab.EARNINGS) },
                    icon = { Icon(Icons.Default.TouchApp, contentDescription = "Tap Cash") },
                    label = {
                        Text(
                            text = "Tap Cash",
                            fontSize = 11.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = if (uiState.activeTab == TycoonTab.EARNINGS) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0F172A),
                        selectedTextColor = VibrantGreen,
                        indicatorColor = VibrantGreen,
                        unselectedIconColor = TextMutedSoft,
                        unselectedTextColor = TextMutedSoft
                    ),
                    modifier = Modifier.testTag("tab_earnings")
                )

                // Tab 4: Houses (simple well-known term instead of "Realty" / "Real Estate")
                NavigationBarItem(
                    selected = uiState.activeTab == TycoonTab.REAL_ESTATE,
                    onClick = { viewModel.selectTab(TycoonTab.REAL_ESTATE) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Houses") },
                    label = {
                        Text(
                            text = "Houses",
                            fontSize = 11.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = if (uiState.activeTab == TycoonTab.REAL_ESTATE) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0F172A),
                        selectedTextColor = BrightGoldLight,
                        indicatorColor = BrightGoldLight,
                        unselectedIconColor = TextMutedSoft,
                        unselectedTextColor = TextMutedSoft
                    ),
                    modifier = Modifier.testTag("tab_real_estate")
                )

                // Tab 5: Rewards (simple well-known term instead of "Prestige" / "Luxury")
                NavigationBarItem(
                    selected = uiState.activeTab == TycoonTab.LUXURY,
                    onClick = { viewModel.selectTab(TycoonTab.LUXURY) },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Rewards") },
                    label = {
                        Text(
                            text = "Rewards",
                            fontSize = 11.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = if (uiState.activeTab == TycoonTab.LUXURY) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0F172A),
                        selectedTextColor = VividPurple,
                        indicatorColor = VividPurple,
                        unselectedIconColor = TextMutedSoft,
                        unselectedTextColor = TextMutedSoft
                    ),
                    modifier = Modifier.testTag("tab_luxury")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SoftDarkBg)
        ) {
            when (uiState.activeTab) {
                TycoonTab.ENTERPRISES -> {
                    EnterprisesScreen(
                        enterprises = uiState.enterprises,
                        userCash = uiState.cash,
                        buyMultiplier = uiState.buyMultiplier,
                        onSetBuyMultiplier = { viewModel.setBuyMultiplier(it) },
                        onUpgrade = { viewModel.upgradeEnterprise(it) },
                        onOpenBankVault = { viewModel.openBankVaultDialog() },
                        onOpenManagers = { viewModel.openManagersSheet() }
                    )
                }
                TycoonTab.MARKETS -> {
                    MarketsScreen(
                        marketAssets = uiState.marketAssets,
                        onSelectAssetForTrading = { viewModel.openTradingDialog(it) }
                    )
                }
                TycoonTab.EARNINGS -> {
                    EarningsScreen(
                        uiState = uiState,
                        onManualClick = { viewModel.makeItRainTap() },
                        onBuyBooster = { viewModel.buyClickBooster(it) }
                    )
                }
                TycoonTab.REAL_ESTATE -> {
                    RealEstateScreen(
                        realEstateList = uiState.realEstate,
                        userCash = uiState.cash,
                        onBuyProperty = { viewModel.buyRealEstate(it) }
                    )
                }
                TycoonTab.LUXURY -> {
                    LuxuryScreen(
                        luxuryList = uiState.luxuryAssets,
                        playerRank = uiState.playerRank,
                        totalPrestigePoints = uiState.totalPrestigePoints,
                        userCash = uiState.cash,
                        onBuyLuxury = { viewModel.buyLuxury(it) }
                    )
                }
            }
        }
    }

    // Trading Sheet
    uiState.selectedTradingAsset?.let { asset ->
        TradingDialog(
            asset = asset,
            userCash = uiState.cash,
            onBuy = { qty -> viewModel.buyMarketAsset(asset.id, qty) },
            onSell = { qty -> viewModel.sellMarketAsset(asset.id, qty) },
            onDismiss = { viewModel.closeTradingDialog() }
        )
    }

    // Bank Vault Sheet
    if (uiState.showBankVaultDialog) {
        BankVaultDialog(
            cash = uiState.cash,
            bankDepositedCash = uiState.bankDepositedCash,
            hourlyBankInterest = uiState.hourlyBankInterest,
            onDeposit = { viewModel.depositToBank(it) },
            onWithdraw = { viewModel.withdrawFromBank(it) },
            onDismiss = { viewModel.closeBankVaultDialog() }
        )
    }

    // Managers Sheet
    if (uiState.showManagersSheet) {
        ManagersDialog(
            enterprises = uiState.enterprises,
            userCash = uiState.cash,
            onHireManager = { viewModel.hireManager(it) },
            onDismiss = { viewModel.closeManagersSheet() }
        )
    }

    // Level Up / Super Bonus IPO Sheet
    if (uiState.showAscensionDialog) {
        AscensionDialog(
            canAscend = uiState.canAscend,
            currentAngelShares = uiState.angelShares,
            claimableAngelShares = uiState.claimableAngelShares,
            currentNetWorth = uiState.totalNetWorth,
            onAscend = { viewModel.ascendAndTakePublic() },
            onDismiss = { viewModel.closeAscensionDialog() }
        )
    }

    // Milestones Sheet
    if (uiState.showMilestonesDialog) {
        MilestonesDialog(
            enterprises = uiState.enterprises,
            onDismiss = { viewModel.closeMilestonesDialog() }
        )
    }

    // Offline Welcome Dialog
    uiState.offlineReport?.let { report ->
        OfflineWelcomeDialog(
            report = report,
            onCollect = { viewModel.dismissOfflineReport() }
        )
    }
}
