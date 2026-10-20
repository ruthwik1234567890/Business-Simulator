package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.util.Formatters
import com.example.viewmodel.TycoonUiState

@Composable
fun TopDashboardBar(
    uiState: TycoonUiState,
    onMakeItRain: () -> Unit = {},
    onResetGame: () -> Unit,
    onOpenBankVault: () -> Unit,
    onOpenManagers: () -> Unit,
    onOpenAscension: () -> Unit,
    onOpenMilestones: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showNetWorthBreakdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SoftDarkBg)
    ) {
        // Hero Visual Banner with Soft Gradient Scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_tycoon_hero),
                contentDescription = "Money Empire",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Soft modern gradient scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F172A).copy(alpha = 0.55f),
                                Color(0xFF0F172A).copy(alpha = 0.88f),
                                SoftDarkBg
                            )
                        )
                    )
            )

            // Content inside hero header
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Title, Bonus pill, and Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(BrightGold)
                        )
                        Text(
                            text = "MONEY TYCOON",
                            color = BrightGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = DisplayFontFamily,
                            letterSpacing = 0.5.sp
                        )
                        if (uiState.angelShares > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VividPurple.copy(alpha = 0.2f))
                                    .border(1.dp, VividPurple.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+${uiState.angelShares * 2}% Bonus",
                                    color = VividPurple,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = AppFontFamily
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Helpers / Managers Shortcut
                        FilledTonalButton(
                            onClick = onOpenManagers,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SoftCardElevated,
                                contentColor = BrightGold
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("open_managers_button")
                        ) {
                            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Helpers", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = AppFontFamily)
                        }

                        // Super Level Up / IPO Shortcut
                        FilledTonalButton(
                            onClick = onOpenAscension,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (uiState.canAscend) VibrantGreen else SoftCardElevated,
                                contentColor = if (uiState.canAscend) Color(0xFF064E3B) else TextMutedSoft
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("open_ascension_button")
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Level Up", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = AppFontFamily)
                        }

                        // Reset Button
                        IconButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.size(28.dp).testTag("reset_game_icon_button")
                        ) {
                            Icon(
                                Icons.Default.RestartAlt,
                                contentDescription = "Reset Game",
                                tint = TextMutedSoft,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Rank Badge & Goals Shortcut
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SoftCardElevated)
                            .border(1.dp, BrightGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Level: ${uiState.playerRank.title}",
                            color = BrightGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFontFamily
                        )
                    }

                    TextButton(
                        onClick = onOpenMilestones,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = VividBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Goals & Bonuses >",
                            color = VividBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFontFamily
                        )
                    }
                }
            }
        }

        // Main Dashboard Metrics Area: Total Wealth & Idle Income (Active cash is in Tap Cash tab)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Wealth Card (Net Worth)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SoftBorder, RoundedCornerShape(14.dp))
                        .clickable { showNetWorthBreakdown = !showNetWorthBreakdown },
                    color = SoftCardBg,
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL WEALTH",
                                color = TextMutedSoft,
                                fontSize = 10.sp,
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.Info, contentDescription = "View Details", tint = TextMutedSoft, modifier = Modifier.size(12.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Formatters.formatCurrency(uiState.totalNetWorth),
                            color = TextPureWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = DisplayFontFamily,
                            modifier = Modifier.testTag("net_worth_display")
                        )
                    }
                }

                // Idle Income Card (Passive Income)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SoftBorder, RoundedCornerShape(14.dp)),
                    color = SoftCardBg,
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "IDLE INCOME",
                            color = TextMutedSoft,
                            fontSize = 10.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.totalHourlyPassiveIncome > 0) VibrantGreenLight else TextSubtle)
                            )
                            Text(
                                text = "+${Formatters.formatCurrency(uiState.totalHourlyPassiveIncome)}/hr",
                                color = VibrantGreenLight,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = DisplayFontFamily,
                                modifier = Modifier.testTag("passive_income_display")
                            )
                        }
                    }
                }
            }

            // Wealth Detailed Breakdown Accordion
            AnimatedVisibility(visible = showNetWorthBreakdown) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SoftBorderHighlight, RoundedCornerShape(14.dp)),
                    color = SoftCardElevated,
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "MY TOTAL WEALTH BREAKDOWN",
                            color = BrightGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = DisplayFontFamily
                        )
                        HorizontalDivider(color = SoftBorder, thickness = 1.dp)
                        BreakdownRow("Cash in Hand & Bank", uiState.cash + uiState.bankDepositedCash)
                        BreakdownRow("Businesses Value", uiState.enterprisesValuation)
                        BreakdownRow("Stocks & Crypto", uiState.stocksValuation)
                        BreakdownRow("Houses & Properties", uiState.realEstateValuation)
                        BreakdownRow("Rewards & Luxury Items", uiState.luxuryValuation)
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Start Over From Scratch?",
                    color = BrightGold,
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "This will wipe all your businesses, houses, and money back to absolute scratch ($0.00 cash, 0 assets, Tier 1 $0.50/click).",
                    color = TextMutedSoft,
                    fontFamily = AppFontFamily,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VividCoral),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset All", color = Color.White, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextMutedSoft, fontFamily = AppFontFamily)
                }
            },
            containerColor = SoftCardBg,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 6.dp
        )
    }
}

@Composable
private fun BreakdownRow(label: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMutedSoft, fontSize = 11.sp, fontFamily = AppFontFamily)
        Text(
            Formatters.formatCurrency(value),
            color = TextPureWhite,
            fontSize = 11.sp,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}
