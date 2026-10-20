package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
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
import com.example.model.RealEstateState
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun RealEstateScreen(
    realEstateList: List<RealEstateState>,
    userCash: Double,
    onBuyProperty: (String) -> Unit,
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
            Column {
                Text(
                    text = "Houses & Properties",
                    color = BrightGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = DisplayFontFamily
                )
                Text(
                    text = "Buy houses to collect steady rent income every hour",
                    color = TextMutedSoft,
                    fontSize = 12.sp,
                    fontFamily = AppFontFamily
                )
            }
        }

        items(realEstateList) { property ->
            val config = property.config
            val canAfford = userCash >= config.cost

            val iconVector: ImageVector = when (config.iconName) {
                "home" -> Icons.Default.Home
                "storefront" -> Icons.Default.Storefront
                else -> Icons.Default.Apartment
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (property.ownedCount > 0) BrightGold.copy(alpha = 0.5f) else SoftBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("real_estate_${config.id}"),
                color = SoftCardBg,
                tonalElevation = if (property.ownedCount > 0) 3.dp else 1.dp
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
                            .background(if (property.ownedCount > 0) BrightGold.copy(alpha = 0.15f) else SoftCardElevated)
                            .border(1.dp, if (property.ownedCount > 0) BrightGold.copy(alpha = 0.4f) else SoftBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            iconVector,
                            contentDescription = config.name,
                            tint = if (property.ownedCount > 0) BrightGoldLight else TextMutedSoft,
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
                            text = "Rent: +${Formatters.formatCurrency(config.hourlyRent)}/hr",
                            color = VibrantGreenLight,
                            fontSize = 12.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                        if (property.ownedCount > 0) {
                            Text(
                                text = "Owned: ${property.ownedCount} (+${Formatters.formatCurrency(property.totalHourlyRent)}/hr)",
                                color = BrightGoldLight,
                                fontSize = 11.sp,
                                fontFamily = AppFontFamily
                            )
                        }
                    }

                    Button(
                        onClick = { onBuyProperty(config.id) },
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
                            Text("Buy", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = AppFontFamily)
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
