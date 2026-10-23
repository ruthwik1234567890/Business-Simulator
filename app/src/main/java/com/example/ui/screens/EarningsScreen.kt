package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.Formatters
import com.example.viewmodel.TycoonUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class FloatingText(
    val id: Long,
    val text: String,
    val xOffset: Float
)

@Composable
fun EarningsScreen(
    uiState: TycoonUiState,
    onManualClick: () -> Unit,
    onBuyBooster: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }
    val floatingTexts = remember { mutableStateListOf<FloatingText>() }
    var textCounter by remember { mutableLongStateOf(0L) }

    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "clickScale"
    )

    fun handleUserClick() {
        onManualClick()
        isPressed = true
        coroutineScope.launch {
            delay(85)
            isPressed = false
        }

        val currentPerClick = uiState.currentPerClickAmount * uiState.tapMultiplier
        val id = ++textCounter
        val xOffset = (-45..45).random().toFloat()
        val formattedGain = "+${Formatters.formatExactCurrency(currentPerClick)}"
        floatingTexts.add(FloatingText(id, formattedGain, xOffset))

        coroutineScope.launch {
            delay(850)
            floatingTexts.removeAll { it.id == id }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SoftDarkBg)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SECTION 1: EXCLUSIVE ACTIVE CASH DISPLAY
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, BrightGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                color = SoftCardBg,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BrightGold)
                        )
                        Text(
                            text = "MY POCKET CASH",
                            color = BrightGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = DisplayFontFamily,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = Formatters.formatExactCurrency(uiState.cash),
                        color = BrightGoldLight,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = DisplayFontFamily,
                        modifier = Modifier.testTag("active_cash_display")
                    )

                    Text(
                        text = "Spendable cash available for business expansions & investments",
                        color = TextMutedSoft,
                        fontSize = 11.sp,
                        fontFamily = AppFontFamily
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SoftBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Per Tap",
                                color = TextMutedSoft,
                                fontSize = 10.sp,
                                fontFamily = AppFontFamily
                            )
                            Text(
                                text = "+${Formatters.formatExactCurrency(uiState.currentPerClickAmount)}",
                                color = VibrantGreenLight,
                                fontSize = 14.sp,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Tap Level",
                                color = TextMutedSoft,
                                fontSize = 10.sp,
                                fontFamily = AppFontFamily
                            )
                            Text(
                                text = "Level ${uiState.currentClickTier}",
                                color = BrightGoldLight,
                                fontSize = 14.sp,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Tap Multiplier",
                                color = TextMutedSoft,
                                fontSize = 10.sp,
                                fontFamily = AppFontFamily
                            )
                            Text(
                                text = "${uiState.tapMultiplier.toInt()}x",
                                color = if (uiState.tapMultiplier > 1.0) VividCoral else VibrantGreenLight,
                                fontSize = 14.sp,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // SECTION 2: THE INTERACTIVE SCREEN CLICK AREA
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        2.dp,
                        if (uiState.tapCombo > 0) VividCoral else BrightGold.copy(alpha = 0.5f),
                        RoundedCornerShape(22.dp)
                    )
                    .scale(scaleAnim)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { handleUserClick() }
                    )
                    .testTag("earnings_click_area"),
                color = SoftCardElevated,
                tonalElevation = 6.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (uiState.tapCombo > 0) VividCoral.copy(alpha = 0.22f) else BrightGold.copy(alpha = 0.16f),
                                    SoftCardElevated
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Glowing tactile gold coin / vault button
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(BrightGoldLight, BrightGold, Color(0xFFD97706))
                                    )
                                )
                                .border(3.5.dp, Color(0xFFFEF3C7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "Tap to Earn Cash",
                                tint = Color(0xFF451A03),
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Tap to Make Money!",
                            color = BrightGoldLight,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = DisplayFontFamily
                        )

                        Text(
                            text = "+${Formatters.formatExactCurrency(uiState.currentPerClickAmount * uiState.tapMultiplier)} per tap",
                            color = TextPureWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = AppFontFamily
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        if (uiState.tapCombo > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = VividCoral.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, VividCoral)
                            ) {
                                Text(
                                    text = "🔥 Streak: ${uiState.tapCombo} Taps (${uiState.tapMultiplier.toInt()}x Bonus!)",
                                    color = VividCoral,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = AppFontFamily,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "Tap quickly to unlock streak multipliers!",
                                color = TextMutedSoft,
                                fontSize = 11.sp,
                                fontFamily = AppFontFamily
                            )
                        }
                    }

                    // Floating text particles popping from taps
                    floatingTexts.forEach { ft ->
                        val floatTransition = rememberInfiniteTransition(label = "floatAnim")
                        val yOffsetAnim by floatTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = -90f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 850, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "yOffset"
                        )

                        Box(modifier = Modifier.offset(x = ft.xOffset.dp, y = yOffsetAnim.dp)) {
                            Text(
                                text = ft.text,
                                color = BrightGoldLight,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = DisplayFontFamily
                            )
                        }
                    }
                }
            }
        }

        // SECTION 3: 1 THIN ROW SHOWING WEALTH PROGRESS & MULTIPLIER REWARD
        item {
            NextMultiplierRow(
                uiState = uiState,
                onBuyBooster = onBuyBooster
            )
        }
    }
}

@Composable
private fun NextMultiplierRow(
    uiState: TycoonUiState,
    onBuyBooster: (Int) -> Unit
) {
    val nextBooster = uiState.nextBooster
    if (nextBooster == null) {
        // Max Tier Reached
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BrightGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .testTag("next_multiplier_row"),
            color = SoftCardBg,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("👑", fontSize = 16.sp)
                    Text(
                        text = "Max Tap Boost Active (${Formatters.formatPerTap(uiState.currentBooster.perClickAmount)})",
                        color = BrightGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DisplayFontFamily
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrightGold.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "MAX LEVEL",
                        color = BrightGoldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
        return
    }

    val canAfford = uiState.cash >= nextBooster.cost
    val progress = (uiState.totalNetWorth / nextBooster.cost).coerceIn(0.0, 1.0).toFloat()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (canAfford) VibrantGreenLight.copy(alpha = 0.6f) else SoftBorder,
                RoundedCornerShape(14.dp)
            )
            .testTag("next_multiplier_row"),
        color = SoftCardBg,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1) {current wealth}/{wealth needed to access new multiplier}
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("⚡", fontSize = 16.sp)
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Money:",
                                color = TextMutedSoft,
                                fontSize = 11.sp,
                                fontFamily = AppFontFamily
                            )
                            Text(
                                text = "${Formatters.formatCurrency(uiState.cash)} / ${Formatters.formatCurrency(nextBooster.cost)}",
                                color = if (uiState.cash >= nextBooster.cost) VibrantGreenLight else BrightGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = DisplayFontFamily
                            )
                        }
                    }
                }

                // 2) Exact rate per tap you get if you spend the required amount + Upgrade action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, BrightGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = Formatters.formatPerTap(nextBooster.perClickAmount),
                            color = BrightGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = DisplayFontFamily,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Button(
                        onClick = { onBuyBooster(nextBooster.tier) },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VibrantGreen,
                            contentColor = Color(0xFF064E3B),
                            disabledContainerColor = SoftCardElevated,
                            disabledContentColor = TextMutedSoft
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("buy_next_multiplier_button")
                    ) {
                        Text(
                            text = if (canAfford) "Unlock" else "Need ${Formatters.formatCurrency(nextBooster.cost)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFontFamily
                        )
                    }
                }
            }

            // Smooth progress indicator to visually track progression to required wealth
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape),
                color = if (canAfford) VibrantGreenLight else BrightGold,
                trackColor = SoftBorder.copy(alpha = 0.4f)
            )
        }
    }
}
