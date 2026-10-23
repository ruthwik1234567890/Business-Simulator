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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClickBooster
import com.example.model.ClickBoosterCatalog
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

        // SECTION 3: CLICK BOOSTER STORE (TAP UPGRADES)
        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "Tap Upgrades",
                    color = BrightGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = DisplayFontFamily
                )
                Text(
                    text = "Upgrade your tap equipment to maximize click yields",
                    color = TextMutedSoft,
                    fontSize = 12.sp,
                    fontFamily = AppFontFamily
                )
            }
        }

        items(ClickBoosterCatalog.ALL_BOOSTERS, key = { it.tier }) { booster ->
            BoosterTierCard(
                booster = booster,
                currentTier = uiState.currentClickTier,
                playerCash = uiState.cash,
                onBuyBooster = { onBuyBooster(booster.tier) }
            )
        }
    }
}

@Composable
private fun BoosterTierCard(
    booster: ClickBooster,
    currentTier: Int,
    playerCash: Double,
    onBuyBooster: () -> Unit
) {
    val isOwned = booster.tier <= currentTier
    val isCurrentActive = booster.tier == currentTier
    val isNextUp = booster.tier == currentTier + 1
    val canAfford = playerCash >= booster.cost && isNextUp

    val borderColor = when {
        isCurrentActive -> BrightGold
        isNextUp && canAfford -> VibrantGreenLight
        isNextUp -> SoftBorderHighlight
        isOwned -> SoftBorder
        else -> SoftBorder.copy(alpha = 0.5f)
    }

    val iconVector: ImageVector = when (booster.iconName) {
        "touch_app" -> Icons.Default.TouchApp
        "point_of_sale" -> Icons.Default.PointOfSale
        "credit_card" -> Icons.Default.CreditCard
        "qr_code_scanner" -> Icons.Default.QrCodeScanner
        "smart_toy" -> Icons.Default.SmartToy
        "terminal" -> Icons.Default.Terminal
        "speed" -> Icons.Default.Speed
        "bolt" -> Icons.Default.Bolt
        "account_balance" -> Icons.Default.AccountBalance
        "language" -> Icons.Default.Language
        else -> Icons.Default.AutoAwesome
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .testTag("booster_tier_${booster.tier}"),
        color = if (isCurrentActive) SoftCardElevated else SoftCardBg,
        tonalElevation = if (isCurrentActive) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isOwned) BrightGold.copy(alpha = 0.15f) else SoftCardElevated)
                    .border(1.dp, if (isOwned) BrightGold.copy(alpha = 0.4f) else SoftBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = booster.name,
                    tint = if (isOwned) BrightGoldLight else TextMutedSoft,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Middle Column with clean horizontal layout (NO vertical wrapping!)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Level ${booster.tier}: ${booster.name}",
                        color = if (isOwned) BrightGoldLight else TextPureWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DisplayFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isCurrentActive) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = VibrantGreen.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, VibrantGreenLight)
                        ) {
                            Text(
                                text = "ACTIVE ✓",
                                color = VibrantGreenLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = AppFontFamily,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Gives: +${Formatters.formatExactCurrency(booster.perClickAmount)} per tap",
                    color = VibrantGreenLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = AppFontFamily
                )

                Text(
                    text = booster.description,
                    color = TextMutedSoft,
                    fontSize = 11.sp,
                    fontFamily = AppFontFamily,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action: Owned / Upgrade Button / Locked
            when {
                isOwned -> {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCurrentActive) BrightGold.copy(alpha = 0.15f) else Color.Transparent,
                        border = BorderStroke(1.dp, if (isCurrentActive) BrightGold.copy(alpha = 0.6f) else SoftBorder),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = if (isCurrentActive) "Equipped" else "Owned",
                                color = if (isCurrentActive) BrightGoldLight else TextMutedSoft,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AppFontFamily
                            )
                        }
                    }
                }
                isNextUp -> {
                    Button(
                        onClick = onBuyBooster,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) VibrantGreen else SoftCardElevated,
                            contentColor = if (canAfford) Color(0xFF064E3B) else TextMutedSoft,
                            disabledContainerColor = SoftCardElevated,
                            disabledContentColor = TextMutedSoft
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(38.dp).testTag("buy_booster_tier_${booster.tier}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Upgrade", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = AppFontFamily)
                            Text(Formatters.formatCurrency(booster.cost), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, fontFamily = AppFontFamily)
                        }
                    }
                }
                else -> {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SoftDarkBg,
                        border = BorderStroke(1.dp, SoftBorder),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("Locked", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = AppFontFamily)
                        }
                    }
                }
            }
        }
    }
}
