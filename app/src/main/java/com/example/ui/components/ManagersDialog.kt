package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EnterpriseState
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun ManagersDialog(
    enterprises: List<EnterpriseState>,
    userCash: Double,
    onHireManager: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Hire Business Helpers",
                    color = BrightGold,
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = "Helpers keep your shops running automatically!",
                    color = TextMutedSoft,
                    fontFamily = AppFontFamily,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(enterprises) { ent ->
                    val mgr = ent.config.manager
                    val canAfford = userCash >= mgr.cost
                    val isHired = ent.isManagerHired

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (isHired) VibrantGreenLight.copy(alpha = 0.5f) else SoftBorder,
                                RoundedCornerShape(12.dp)
                            ),
                        color = SoftCardElevated,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mgr.name,
                                    color = TextPureWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = DisplayFontFamily
                                )
                                Text(
                                    text = mgr.title,
                                    color = BrightGoldLight,
                                    fontSize = 11.sp,
                                    fontFamily = AppFontFamily
                                )
                                Text(
                                    text = "Runs: ${ent.config.name}",
                                    color = TextMutedSoft,
                                    fontSize = 10.sp,
                                    fontFamily = AppFontFamily
                                )
                            }

                            if (isHired) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(VibrantGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Hired ✅",
                                        color = VibrantGreenLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = AppFontFamily
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { onHireManager(ent.config.id) },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (canAfford) VibrantGreen else SoftBorder,
                                        contentColor = if (canAfford) Color(0xFF064E3B) else TextMutedSoft,
                                        disabledContainerColor = SoftBorder,
                                        disabledContentColor = TextSubtle
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        text = "Hire (${Formatters.formatCurrency(mgr.cost)})",
                                        fontSize = 11.sp,
                                        fontFamily = AppFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = BrightGoldLight, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = SoftCardBg,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 6.dp
    )
}
