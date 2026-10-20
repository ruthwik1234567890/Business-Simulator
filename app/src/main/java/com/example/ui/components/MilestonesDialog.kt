package com.example.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EnterpriseState
import com.example.ui.theme.*

@Composable
fun MilestonesDialog(
    enterprises: List<EnterpriseState>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Goals & Level Bonuses",
                    color = BrightGold,
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = "Reach levels to multiply your earnings!",
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
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, SoftBorder, RoundedCornerShape(12.dp)),
                        color = SoftCardElevated
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "${ent.config.name} (Level ${ent.level})",
                                color = TextPureWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = DisplayFontFamily
                            )
                            ent.config.milestones.forEach { m ->
                                val reached = ent.level >= m.level
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "• Level ${m.level}: ${m.label}",
                                        color = if (reached) VibrantGreenLight else TextMutedSoft,
                                        fontSize = 12.sp,
                                        fontFamily = AppFontFamily
                                    )
                                    Text(
                                        text = if (reached) "Unlocked ⭐" else "Goal",
                                        color = if (reached) VibrantGreenLight else TextSubtle,
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
