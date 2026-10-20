package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EnterpriseState
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun EnterprisesScreen(
    enterprises: List<EnterpriseState>,
    userCash: Double,
    buyMultiplier: String,
    onSetBuyMultiplier: (String) -> Unit,
    onUpgrade: (String) -> Unit,
    onOpenBankVault: () -> Unit,
    onOpenManagers: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SoftDarkBg)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Businesses",
                        color = BrightGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = DisplayFontFamily
                    )
                    Text(
                        text = "Upgrade shops to earn passive income every hour",
                        color = TextMutedSoft,
                        fontSize = 12.sp,
                        fontFamily = AppFontFamily
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("1x", "10x", "25x", "MAX").forEach { mult ->
                        val isSelected = buyMultiplier == mult
                        OutlinedButton(
                            onClick = { onSetBuyMultiplier(mult) },
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) BrightGold else SoftBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) BrightGold.copy(alpha = 0.15f) else Color.Transparent
                            )
                        ) {
                            Text(
                                text = mult,
                                fontSize = 11.sp,
                                fontFamily = AppFontFamily,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) BrightGoldLight else TextMutedSoft
                            )
                        }
                    }
                }
            }
        }

        items(enterprises) { enterprise ->
            EnterpriseItemCard(
                enterprise = enterprise,
                userCash = userCash,
                buyMultiplier = buyMultiplier,
                onUpgrade = { onUpgrade(enterprise.config.id) },
                onOpenBankVault = onOpenBankVault
            )
        }
    }
}

@Composable
private fun EnterpriseItemCard(
    enterprise: EnterpriseState,
    userCash: Double,
    buyMultiplier: String,
    onUpgrade: () -> Unit,
    onOpenBankVault: () -> Unit
) {
    val context = LocalContext.current
    val config = enterprise.config
    val isBank = config.isBank
    val isUnlocked = enterprise.level > 0

    val (levelsToAdd, totalCost) = when (buyMultiplier) {
        "10x" -> Pair(10, config.getCostForLevels(enterprise.level, 10))
        "25x" -> Pair(25, config.getCostForLevels(enterprise.level, 25))
        "MAX" -> config.getMaxAffordableLevels(enterprise.level, userCash)
        else -> Pair(1, enterprise.upgradeCost)
    }

    val canAfford = userCash >= totalCost && levelsToAdd > 0

    val imageResId = remember(config.imageResName) {
        val id = context.resources.getIdentifier(config.imageResName, "drawable", context.packageName)
        if (id != 0) id else com.example.R.drawable.tycoon_icon
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (isUnlocked) BrightGold.copy(alpha = 0.5f) else SoftBorder,
                RoundedCornerShape(18.dp)
            )
            .testTag("enterprise_${config.id}"),
        color = SoftCardBg,
        tonalElevation = if (isUnlocked) 3.dp else 1.dp
    ) {
        Column {
            // Enterprise Visual Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = config.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = if (isUnlocked) 0.35f else 0.65f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SoftDarkBg.copy(alpha = 0.88f))
                            .border(1.dp, if (isUnlocked) BrightGoldLight else SoftBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isUnlocked) "Level ${enterprise.level}" else "Not Owned",
                            color = if (isUnlocked) BrightGoldLight else TextMutedSoft,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFontFamily
                        )
                    }

                    if (enterprise.isManagerHired) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(VibrantGreen.copy(alpha = 0.2f))
                                .border(1.dp, VibrantGreenLight, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "⚡ Auto-Earns",
                                color = VibrantGreenLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AppFontFamily
                            )
                        }
                    }
                }
            }

            // Card details
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = config.name,
                    color = TextPureWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DisplayFontFamily
                )
                Text(
                    text = config.description,
                    color = TextMutedSoft,
                    fontSize = 11.sp,
                    fontFamily = AppFontFamily,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Profit / hour", color = TextMutedSoft, fontSize = 10.sp, fontFamily = AppFontFamily)
                        Text(
                            text = "+${Formatters.formatCurrency(enterprise.currentHourlyIncome)}/hr",
                            color = if (enterprise.currentHourlyIncome > 0) VibrantGreenLight else TextMutedSoft,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = DisplayFontFamily
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isBank && isUnlocked) {
                            OutlinedButton(
                                onClick = onOpenBankVault,
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, VividBlue),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VividBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text("Bank Vault", fontSize = 11.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Upgrade / Buy Button
                        Button(
                            onClick = onUpgrade,
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canAfford) VibrantGreen else SoftCardElevated,
                                contentColor = if (canAfford) Color(0xFF064E3B) else TextMutedSoft,
                                disabledContainerColor = SoftCardElevated,
                                disabledContentColor = TextSubtle
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp).testTag("upgrade_${config.id}_button")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (!isUnlocked) "Buy Shop" else "+$levelsToAdd Level",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = AppFontFamily
                                )
                                Text(
                                    text = Formatters.formatCurrency(totalCost),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = AppFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
