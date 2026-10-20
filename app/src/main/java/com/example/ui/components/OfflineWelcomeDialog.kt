package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    AlertDialog(
        onDismissRequest = onCollect,
        title = {
            Text(
                text = "Welcome Back! 💰",
                color = BrightGold,
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "While you were away for ${report.elapsedSeconds / 60}m, your shops and houses made you:",
                    color = TextMutedSoft,
                    fontSize = 12.sp,
                    fontFamily = AppFontFamily
                )
                Text(
                    text = "+${Formatters.formatExactCurrency(report.totalEarnings)}",
                    color = VibrantGreenLight,
                    fontSize = 24.sp,
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.ExtraBold
                )
                HorizontalDivider(color = SoftBorder)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Businesses:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                    Text("+${Formatters.formatExactCurrency(report.enterprisesEarned)}", color = TextPureWhite, fontSize = 12.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Houses:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                    Text("+${Formatters.formatExactCurrency(report.realEstateEarned)}", color = TextPureWhite, fontSize = 12.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Bank Interest:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                    Text("+${Formatters.formatExactCurrency(report.bankInterestEarned)}", color = TextPureWhite, fontSize = 12.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCollect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VibrantGreen,
                    contentColor = Color(0xFF064E3B)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Collect My Money! 🎉", fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = SoftCardBg,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 6.dp
    )
}
