package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.Formatters
import com.example.viewmodel.OfflineEarningsReport

@Composable
fun OfflineWelcomeDialog(
    report: OfflineEarningsReport,
    onCollect: () -> Unit
) {
    val mins = report.elapsedSeconds / 60
    val secs = report.elapsedSeconds % 60
    val timeLabel = when {
        mins >= 60 -> "${mins / 60}h ${mins % 60}m"
        mins > 0 -> "${mins}m ${secs}s"
        else -> "${secs}s"
    }

    AlertDialog(
        onDismissRequest = onCollect,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "💰", fontSize = 26.sp)
                Column {
                    Text(
                        text = "Welcome Back, Tycoon!",
                        color = BrightGold,
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Idle Empire Performance",
                        color = TextMutedSoft,
                        fontFamily = AppFontFamily,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "While you were away for $timeLabel, your businesses, rental houses, and active investments generated idle cash:",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontFamily = AppFontFamily,
                    lineHeight = 17.sp
                )

                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "CLAIMED INTO MY POCKET CASH",
                            color = Color(0xFF00E676),
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "+${Formatters.formatExactCurrency(report.totalEarnings)}",
                            color = VibrantGreenLight,
                            fontSize = 24.sp,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                HorizontalDivider(color = SoftBorder)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Businesses:", color = TextMutedSoft, fontSize = 11.sp, fontFamily = AppFontFamily)
                    Text("+${Formatters.formatExactCurrency(report.enterprisesEarned)}", color = TextPureWhite, fontSize = 11.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Rental Houses:", color = TextMutedSoft, fontSize = 11.sp, fontFamily = AppFontFamily)
                    Text("+${Formatters.formatExactCurrency(report.realEstateEarned)}", color = TextPureWhite, fontSize = 11.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Bank & Active Investments:", color = TextMutedSoft, fontSize = 11.sp, fontFamily = AppFontFamily)
                    Text("+${Formatters.formatExactCurrency(report.bankInterestEarned)}", color = TextPureWhite, fontSize = 11.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCollect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E676),
                    contentColor = Color(0xFF064E3B)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("collect_offline_earnings_button")
            ) {
                Text(
                    text = "Deposit Into Pocket Cash 💵",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        },
        containerColor = SoftCardBg,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 6.dp
    )
}
