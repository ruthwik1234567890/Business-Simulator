package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun BankVaultDialog(
    cash: Double,
    bankDepositedCash: Double,
    hourlyBankInterest: Double,
    onDeposit: (Double) -> Unit,
    onWithdraw: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var amountInput by remember { mutableStateOf("") }
    val amount = amountInput.toDoubleOrNull() ?: 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Bank Savings Vault",
                    color = BrightGold,
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = "Deposit money to earn 3.5% interest every hour",
                    color = TextMutedSoft,
                    fontFamily = AppFontFamily,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                    color = SoftCardElevated
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Savings in Bank:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                            Text(Formatters.formatExactCurrency(bankDepositedCash), color = BrightGoldLight, fontSize = 13.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Hourly Interest (+3.5%):", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                            Text("+${Formatters.formatExactCurrency(hourlyBankInterest)}/hr", color = VibrantGreenLight, fontSize = 13.sp, fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pocket Cash available:", color = TextMutedSoft, fontSize = 12.sp, fontFamily = AppFontFamily)
                            Text(Formatters.formatExactCurrency(cash), color = TextPureWhite, fontSize = 12.sp, fontFamily = AppFontFamily)
                        }
                    }
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount ($)", fontFamily = AppFontFamily) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrightGold,
                        unfocusedBorderColor = SoftBorder,
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onDeposit(if (amount > 0) amount else cash)
                            amountInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VibrantGreen,
                            contentColor = Color(0xFF064E3B)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Deposit", fontFamily = AppFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onWithdraw(if (amount > 0) amount else bankDepositedCash)
                            amountInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VividBlue,
                            contentColor = Color(0xFF0C2442)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Withdraw", fontFamily = AppFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
