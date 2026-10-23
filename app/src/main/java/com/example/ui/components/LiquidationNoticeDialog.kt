package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LiquidationSummary
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun LiquidationNoticeDialog(
    summary: LiquidationSummary,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("⚠️", fontSize = 22.sp)
                Text(
                    text = "Asset Liquidation Notice",
                    color = Color(0xFFEF4444),
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "The 20-minute tax grace window expired without payment. The Revenue Authority has executed a forced asset liquidation cascade:",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontFamily = AppFontFamily
                )

                HorizontalDivider(color = SoftBorder)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tax Debt Demanded:", color = TextMutedSoft, fontSize = 11.sp)
                    Text(Formatters.formatExactCurrency(summary.totalDebtDemanded), color = Color(0xFFF87171), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Assets Seized / Sold:", color = TextMutedSoft, fontSize = 11.sp)
                    Text("${summary.liquidatedAssetsNames.size} items", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                if (summary.liquidatedAssetsNames.isNotEmpty()) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            summary.liquidatedAssetsNames.take(4).forEach { name ->
                                Text("• $name", color = Color(0xFFFCD34D), fontSize = 11.sp)
                            }
                            if (summary.liquidatedAssetsNames.size > 4) {
                                Text("+ ${summary.liquidatedAssetsNames.size - 4} more...", color = TextMutedSoft, fontSize = 10.sp)
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Refund Deposited to Pocket:", color = TextMutedSoft, fontSize = 11.sp)
                    Text("+${Formatters.formatExactCurrency(summary.refundPaidToPocket)}", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                if (summary.revokedIncomeAmount > 0) {
                    Text(
                        text = "Note: ${Formatters.formatExactCurrency(summary.revokedIncomeAmount)} in uncollected revenue was revoked as penalty.",
                        color = Color(0xFFF87171),
                        fontSize = 10.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dismiss_liquidation_button")
            ) {
                Text("Acknowledge Enforcement", fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp
    )
}
