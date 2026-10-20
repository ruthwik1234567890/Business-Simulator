package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import kotlin.math.floor

@Composable
fun TradingDialog(
    asset: MarketAsset,
    userCash: Double,
    onBuy: (Double) -> Unit,
    onSell: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var quantityInput by remember { mutableStateOf("1") }
    val quantity = quantityInput.toDoubleOrNull() ?: 0.0
    val totalCost = asset.currentPrice * quantity
    val canBuy = quantity > 0 && userCash >= totalCost
    val canSell = quantity > 0 && asset.ownedUnits >= quantity

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${asset.name} (${asset.symbol})",
                        color = BrightGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DisplayFontFamily
                    )
                    Text(
                        text = "Price: ${Formatters.formatExactCurrency(asset.currentPrice)}",
                        color = TextPureWhite,
                        fontSize = 13.sp,
                        fontFamily = AppFontFamily
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMutedSoft)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                    color = SoftCardElevated
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("You own:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                            Text("${Formatters.formatNumber(asset.ownedUnits)} units", color = TextPureWhite, fontSize = 12.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total value:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                            Text(Formatters.formatExactCurrency(asset.totalValue), color = TextPureWhite, fontSize = 12.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Average buy price:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                            Text(Formatters.formatExactCurrency(asset.averageCostBasis), color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                        }
                        if (asset.ownedUnits > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Your profit:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                                Text(
                                    text = "${Formatters.formatExactCurrency(asset.totalUnrealizedPnL)} (${Formatters.formatPercent(asset.pnlPercent)})",
                                    color = if (asset.totalUnrealizedPnL >= 0) VibrantGreenLight else VividCoral,
                                    fontSize = 12.sp,
                                    fontFamily = AppFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Quantity selector presets
                Text("Select amount:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1", "5", "10", "MAX").forEach { preset ->
                        OutlinedButton(
                            onClick = {
                                if (preset == "MAX") {
                                    val maxUnits = floor(userCash / asset.currentPrice)
                                    quantityInput = max(1.0, maxUnits).toInt().toString()
                                } else {
                                    quantityInput = preset
                                }
                            },
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp),
                            border = BorderStroke(1.dp, if (quantityInput == preset) BrightGold else SoftBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (quantityInput == preset) BrightGold.copy(alpha = 0.15f) else Color.Transparent
                            )
                        ) {
                            Text(
                                text = preset,
                                fontSize = 12.sp,
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (quantityInput == preset) BrightGoldLight else TextPureWhite
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = { quantityInput = it },
                    label = { Text("Number of units", fontFamily = AppFontFamily) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrightGold,
                        unfocusedBorderColor = SoftBorder,
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("trade_quantity_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total amount:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                    Text(
                        Formatters.formatExactCurrency(totalCost),
                        color = BrightGoldLight,
                        fontSize = 15.sp,
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        onBuy(quantity)
                        onDismiss()
                    },
                    enabled = canBuy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VibrantGreen,
                        contentColor = Color(0xFF064E3B),
                        disabledContainerColor = SoftBorder,
                        disabledContentColor = TextSubtle
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("buy_market_asset_button")
                ) {
                    Text("Buy", fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onSell(quantity)
                        onDismiss()
                    },
                    enabled = canSell,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VividCoral,
                        contentColor = Color.White,
                        disabledContainerColor = SoftBorder,
                        disabledContentColor = TextSubtle
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("sell_market_asset_button")
                ) {
                    Text("Sell", fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = SoftCardBg,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 6.dp
    )
}

private fun max(a: Double, b: Double): Double = if (a > b) a else b
