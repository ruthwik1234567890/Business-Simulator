package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarketAsset
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun MarketsScreen(
    marketAssets: List<MarketAsset>,
    onSelectAssetForTrading: (MarketAsset) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = All, 1 = Stocks, 2 = Crypto

    val filteredList = when (selectedTab) {
        1 -> marketAssets.filter { !it.isCrypto }
        2 -> marketAssets.filter { it.isCrypto }
        else -> marketAssets
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SoftDarkBg)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Stocks & Coins",
                        color = BrightGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = DisplayFontFamily
                    )
                    Text(
                        text = "Buy low, sell high to boost your profits",
                        color = TextMutedSoft,
                        fontSize = 12.sp,
                        fontFamily = AppFontFamily
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Stocks", "Crypto").forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        OutlinedButton(
                            onClick = { selectedTab = index },
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) VividBlue else SoftBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) VividBlue.copy(alpha = 0.15f) else Color.Transparent
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontFamily = AppFontFamily,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) VividBlue else TextMutedSoft
                            )
                        }
                    }
                }
            }
        }

        items(filteredList) { asset ->
            MarketAssetCard(
                asset = asset,
                onClick = { onSelectAssetForTrading(asset) }
            )
        }
    }
}

@Composable
private fun MarketAssetCard(
    asset: MarketAsset,
    onClick: () -> Unit
) {
    val deltaColor = if (asset.isPositive) VibrantGreenLight else VividCoral
    val sign = if (asset.isPositive) "+" else ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SoftBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("market_asset_${asset.id}"),
        color = SoftCardBg,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = asset.symbol,
                        color = BrightGoldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DisplayFontFamily
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (asset.isCrypto) VividPurple.copy(alpha = 0.2f) else VividBlue.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (asset.isCrypto) "CRYPTO" else "STOCK",
                            color = if (asset.isCrypto) VividPurple else VividBlue,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = asset.name,
                    color = TextMutedSoft,
                    fontSize = 11.sp,
                    fontFamily = AppFontFamily,
                    maxLines = 1
                )

                if (asset.ownedUnits > 0.0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You own: ${Formatters.formatNumber(asset.ownedUnits)} (${Formatters.formatCurrency(asset.totalValue)})",
                        color = VibrantGreenLight,
                        fontSize = 11.sp,
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Formatters.formatExactCurrency(asset.currentPrice),
                    color = TextPureWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DisplayFontFamily
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(deltaColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$sign${Formatters.formatPercent(asset.priceChangePercent)}",
                        color = deltaColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFontFamily
                    )
                }
            }
        }
    }
}
