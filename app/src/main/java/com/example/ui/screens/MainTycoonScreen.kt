package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storefront
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

    if (uiState.isMySpaceOpen) {
        // Dedicated, full-screen Jetpack Compose view for My Space
        MySpaceScreen(
            viewModel = viewModel,
            uiState = uiState,
            onBack = { viewModel.closeMySpace() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFF0A0E1A),
            topBar = {
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0E1A))) {
                    TopDashboardBar(
                        uiState = uiState,
                        onResetGame = { viewModel.resetGame() },
                        onOpenBankVault = { viewModel.openBankVaultDialog() },
                        onOpenManagers = { viewModel.openManagersSheet() },
                        onOpenAscension = { viewModel.openAscensionDialog() },
                        onOpenMilestones = { viewModel.openMilestonesDialog() },
                        onNavigateToPocket = { viewModel.selectTab(TycoonTab.EARNINGS) }
                    )
                    NewsTickerBar(currentEvent = uiState.currentNewsEvent)
                    // Sticky Tax Warning Banner at minute 15 with 5-minute countdown and one-tap Pay Tax CTA
                    TaxWarningBanner(
                        taxStatus = uiState.taxStatus,
                        onPayTax = { viewModel.payTax() }
                    )
                }
            },
            bottomBar = {
                Surface(
                    color = Color(0xFF0C101D),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        contentColor = TextMutedSoft,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets.navigationBars,
                        modifier = Modifier.testTag("tycoon_navigation_bar")
                    ) {
                        // Tab 1: Businesses
                        NavigationBarItem(
                            selected = uiState.activeTab == TycoonTab.ENTERPRISES,
                            onClick = { viewModel.selectTab(TycoonTab.ENTERPRISES) },
                            icon = { Icon(Icons.Default.Storefront, contentDescription = "Businesses") },
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

                        // Tab 2: Stocks
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

                        // Tab 3 (CENTER): Tap Cash
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
                                selectedIconColor = Color(0xFF064E3B),
                                selectedTextColor = Color(0xFF00E676),
                                indicatorColor = Color(0xFF00E676),
                                unselectedIconColor = TextMutedSoft,
                                unselectedTextColor = TextMutedSoft
                            ),
                            modifier = Modifier.testTag("tab_earnings")
                        )

                        // Tab 4: Houses
                        NavigationBarItem(
                            selected = uiState.activeTab == TycoonTab.REAL_ESTATE,
                            onClick = { viewModel.selectTab(TycoonTab.REAL_ESTATE) },
                            icon = { Icon(Icons.Default.Apartment, contentDescription = "Houses") },
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

                        // Tab 5: My Space
                        NavigationBarItem(
                            selected = uiState.isMySpaceOpen,
                            onClick = { viewModel.openMySpace() },
                            icon = { Icon(Icons.Default.Stars, contentDescription = "My Space") },
                            label = {
                                Text(
                                    text = "My Space",
                                    fontSize = 11.sp,
                                    fontFamily = AppFontFamily,
                                    fontWeight = if (uiState.isMySpaceOpen) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0F172A),
                                selectedTextColor = Color(0xFFFFC700),
                                indicatorColor = Color(0xFFFFC700),
                                unselectedIconColor = TextMutedSoft,
                                unselectedTextColor = TextMutedSoft
                            ),
                            modifier = Modifier.testTag("tab_my_space")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF0A0E1A))
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
                    TycoonTab.MY_SPACE -> {
                        viewModel.openMySpace()
                    }
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

    // Ascension Sheet
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

    // Offline Welcome Dialog (Idle earnings claimed into "My Pocket Cash")
    uiState.offlineReport?.let { report ->
        OfflineWelcomeDialog(
            report = report,
            onCollect = { viewModel.dismissOfflineReport() }
        )
    }

    // Player Name Input Dialog for Forbes 250 Induction
    if (uiState.showNamePromptDialog) {
        PlayerNameDialog(
            initialName = uiState.playerName,
            onConfirmName = { viewModel.confirmPlayerName(it) }
        )
    }

    // Forbes 250 Grand Celebration Dialog
    if (uiState.showForbesCelebrationDialog && uiState.forbesRank != null) {
        ForbesCelebrationDialog(
            playerName = uiState.playerName,
            playerRank = uiState.forbesRank!!,
            netWorth = uiState.totalNetWorth,
            onClaimAndExplore = {
                viewModel.dismissForbesCelebration()
                viewModel.openMySpace()
            }
        )
    }

    // Forced Asset Liquidation Notice Dialog
    uiState.liquidationNotice?.let { summary ->
        LiquidationNoticeDialog(
            summary = summary,
            onDismiss = { viewModel.dismissLiquidationNotice() }
        )
    }
}
