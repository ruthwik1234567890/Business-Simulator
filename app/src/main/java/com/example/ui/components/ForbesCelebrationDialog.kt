package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun ForbesCelebrationDialog(
    playerName: String,
    playerRank: Int,
    netWorth: Double,
    onClaimAndExplore: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClaimAndExplore,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFFF9800))
                            ),
                            shape = CircleShape
                        )
                        .border(2.dp, Color(0xFFFFC700), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 34.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "OFFICIALLY INDUCTED!",
                    color = Color(0xFFFFC700),
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Global Forbes 250 Rich List",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontFamily = AppFontFamily
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Congratulations, $playerName! Your financial empire has shattered global records, surpassing the world's 250th richest titan.",
                    color = Color(0xFFF1F5F9),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = AppFontFamily,
                    lineHeight = 18.sp
                )

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFC700).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = "OFFICIAL WORLD RANK",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = DisplayFontFamily,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "#$playerRank IN THE WORLD",
                            color = Color(0xFF00E676),
                            fontSize = 22.sp,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Certified Net Worth: ${Formatters.formatCurrency(netWorth)}",
                            color = Color(0xFFFFD54F),
                            fontSize = 13.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎉 🥂 🚀 🍾 ✨",
                        fontSize = 18.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onClaimAndExplore,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC700),
                    contentColor = Color(0xFF0F172A)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("celebrate_forbes_button")
            ) {
                Text(
                    text = "Claim Glory & Open Forbes 250 ✨",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        },
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 10.dp
    )
}
