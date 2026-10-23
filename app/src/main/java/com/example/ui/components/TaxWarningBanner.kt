package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaxStatusState
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun TaxWarningBanner(
    taxStatus: TaxStatusState,
    onPayTax: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = taxStatus.isWarningActive && taxStatus.currentTaxDue > 0.0,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        val minutes = taxStatus.warningRemainingSeconds / 60
        val seconds = taxStatus.warningRemainingSeconds % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)

        Surface(
            color = Color(0xFF2A0808),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                .testTag("tax_warning_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Tax Warning",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "TAX PAYMENT DUE",
                                color = Color(0xFFFCA5A5),
                                fontSize = 11.sp,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = Color(0xFFEF4444),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = timeFormatted,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "2.5% Tax: ${Formatters.formatExactCurrency(taxStatus.currentTaxDue)} • Auto-liquidation at 00:00",
                            color = Color(0xFFE2E8F0),
                            fontSize = 10.sp,
                            fontFamily = AppFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onPayTax,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676),
                        contentColor = Color(0xFF064E3B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("pay_tax_button")
                ) {
                    Text(
                        text = "Pay Tax",
                        fontSize = 12.sp,
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
