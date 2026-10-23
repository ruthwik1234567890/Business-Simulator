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

@Composable
fun AscensionDialog(
    canAscend: Boolean,
    currentAngelShares: Int,
    claimableAngelShares: Int,
    currentNetWorth: Double,
    onAscend: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Mega Level Up & Permanent Bonus",
                color = BrightGold,
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Restart your empire to earn Super Bonus Stars! Super Stars permanently boost all your future cash and shop income.",
                    color = TextMutedSoft,
                    fontSize = 12.sp,
                    fontFamily = AppFontFamily
                )
                Text(
                    text = "Each Super Star gives a permanent +2% income boost forever across all resets!",
                    color = VibrantGreenLight,
                    fontSize = 12.sp,
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(color = SoftBorder)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Current Super Stars:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                    Text("$currentAngelShares (+${currentAngelShares * 2}%)", color = BrightGoldLight, fontSize = 12.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("New Stars on Level Up:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                    Text("+$claimableAngelShares Stars", color = if (canAscend) VibrantGreenLight else TextSubtle, fontSize = 12.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                }
                if (!canAscend) {
                    Text(
                        text = "You need at least $1,000,000 Total Money to Level Up.",
                        color = VividCoral,
                        fontSize = 11.sp,
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAscend,
                enabled = canAscend,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VibrantGreen,
                    contentColor = Color(0xFF064E3B),
                    disabledContainerColor = SoftCardElevated,
                    disabledContentColor = TextSubtle
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Level Up Now! 🚀", fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Playing", color = TextMutedSoft, fontFamily = AppFontFamily)
            }
        },
        containerColor = SoftCardBg,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 6.dp
    )
}
