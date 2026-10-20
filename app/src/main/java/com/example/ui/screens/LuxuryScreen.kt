package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LuxuryState
import com.example.model.PlayerRank
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun LuxuryScreen(
    luxuryList: List<LuxuryState>,
    playerRank: PlayerRank,
    totalPrestigePoints: Int,
    userCash: Double,
    onBuyLuxury: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SoftDarkBg)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, VividPurple.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                color = SoftCardBg,
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = VividPurple, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Trophies & Rewards",
                            color = VividPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = DisplayFontFamily
                        )
                    }

                    Text(
                        text = "Your Status: ${playerRank.title}",
                        color = TextPureWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DisplayFontFamily
                    )
                    Text(
                        text = playerRank.subtitle,
                        color = TextMutedSoft,
                        fontSize = 12.sp,
                        fontFamily = AppFontFamily
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VividPurple.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Trophy Points: $totalPrestigePoints pts",
                            color = BrightGoldLight,
                            fontSize = 12.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Trophy Collection",
                color = BrightGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = DisplayFontFamily
            )
        }

        items(luxuryList) { item ->
            val config = item.config
            val canAfford = userCash >= config.cost && !item.isOwned

            val iconVector: ImageVector = when (config.iconName) {
                "directions_car" -> Icons.Default.DirectionsCar
                "flight" -> Icons.Default.Flight
                else -> Icons.Default.Sailing
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (item.isOwned) VividPurple.copy(alpha = 0.6f) else SoftBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("luxury_${config.id}"),
                color = SoftCardBg,
                tonalElevation = if (item.isOwned) 3.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (item.isOwned) VividPurple.copy(alpha = 0.15f) else SoftCardElevated)
                            .border(1.dp, if (item.isOwned) VividPurple.copy(alpha = 0.4f) else SoftBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            iconVector,
                            contentDescription = config.name,
                            tint = if (item.isOwned) VividPurple else TextMutedSoft,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = config.name,
                            color = TextPureWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = DisplayFontFamily
                        )
                        Text(
                            text = config.description,
                            color = TextMutedSoft,
                            fontSize = 11.sp,
                            fontFamily = AppFontFamily,
                            maxLines = 1
                        )
                        Text(
                            text = "+${config.prestigePoints} Trophy Points",
                            color = BrightGoldLight,
                            fontSize = 11.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (item.isOwned) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(VibrantGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Owned 🏆",
                                color = VibrantGreenLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AppFontFamily
                            )
                        }
                    } else {
                        Button(
                            onClick = { onBuyLuxury(config.id) },
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canAfford) VibrantGreen else SoftCardElevated,
                                contentColor = if (canAfford) Color(0xFF064E3B) else TextMutedSoft,
                                disabledContainerColor = SoftCardElevated,
                                disabledContentColor = TextSubtle
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Get", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = AppFontFamily)
                                Text(
                                    text = Formatters.formatCurrency(config.cost),
                                    fontSize = 10.sp,
                                    fontFamily = AppFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
