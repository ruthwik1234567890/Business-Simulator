package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
    onNavigateToPocket: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SoftDarkBg)
    ) {
        // Hero Visual Banner with Status Bar Safe Area Padding
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            // Background Hero Skyline Image
            Image(
                painter = painterResource(id = R.drawable.img_tycoon_hero),
                contentDescription = "Money Empire",
                modifier = Modifier
                    .matchParentSize(),
                contentScale = ContentScale.Crop
            )

            // Deep luxury gradient scrim
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF070B14).copy(alpha = 0.85f),
                                Color(0xFF0F172A).copy(alpha = 0.88f),
                                SoftDarkBg
                            )
                        )
                    )
            )

            // Content inside hero header, respecting statusBars insets
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Row: Brand & Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrightGold,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("$", color = Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(
                            text = "MONEY TYCOON",
                            color = BrightGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = DisplayFontFamily,
                            letterSpacing = 0.5.sp
                        )
                        if (uiState.angelShares > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = VividPurple.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VividPurple.copy(alpha = 0.6f)),
                                modifier = Modifier.padding(start = 2.dp)
                            ) {
                                Text(
                                    text = "+${uiState.angelShares * 2}%",
                                    color = VividPurple,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = AppFontFamily,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Helpers / Managers Button
                        Button(
                            onClick = onOpenManagers,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftCardElevated,
                                contentColor = BrightGold
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp).testTag("open_managers_button")
                        ) {
                            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Helpers", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = AppFontFamily)
                        }

                        // Super Level Up / IPO Button
                        Button(
                            onClick = onOpenAscension,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.canAscend) VibrantGreen else SoftCardElevated,
                                contentColor = if (uiState.canAscend) Color(0xFF064E3B) else TextMutedSoft
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp).testTag("open_ascension_button")
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Level Up", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = AppFontFamily)
                        }

                        // Reset Button
                        IconButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.size(30.dp).testTag("reset_game_icon_button")
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

                // Rank Ribbon & Goals Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF1E293B).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("🎖️", fontSize = 10.sp)
                            Text(
                                text = "Rank: ${uiState.playerRank.title}",
                                color = BrightGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AppFontFamily
                            )
                        }
                    }

                    Surface(
                        color = VividBlue.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VividBlue.copy(alpha = 0.35f)),
                        modifier = Modifier.clickable { onOpenMilestones() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = VividBlue, modifier = Modifier.size(13.dp))
                            Text(
                                text = "Goals & Bonuses ›",
                                color = VividBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AppFontFamily
                            )
                        }
                    }
                }
            }
        }

        // Financial Cockpit Metrics: Total Money & Idle Income
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Money Card (Pocket Cash) - Redirects directly to pocket cash screen
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SoftBorder, RoundedCornerShape(14.dp))
                        .clickable { onNavigateToPocket() }
                        .testTag("total_money_card"),
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
                                text = "TOTAL MONEY",
                                color = TextMutedSoft,
                                fontSize = 10.sp,
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "View Pocket Cash",
                                tint = BrightGold,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Formatters.formatCurrency(uiState.cash),
                            color = BrightGoldLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = DisplayFontFamily,
                            modifier = Modifier.testTag("total_money_display")
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
                                    .size(7.dp)
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
                    text = "This will reset all your businesses, properties, and cash back to $0.00.",
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
