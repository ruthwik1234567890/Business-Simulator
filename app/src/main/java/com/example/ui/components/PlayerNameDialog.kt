package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PlayerNameDialog(
    initialName: String,
    onConfirmName: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialName.ifBlank { "Baron Von Cash" }) }

    AlertDialog(
        onDismissRequest = { /* Force confirmation or dismiss with default */ onConfirmName(text.ifBlank { "Tycoon" }) },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("👑", fontSize = 24.sp)
                Text(
                    text = "Welcome to the Billionaire Club!",
                    color = Color(0xFFFFC700),
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "You are now richer than the 250th richest person on Earth! How should the Forbes 250 print your name on the world leaderboard?",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontFamily = AppFontFamily
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.length <= 25) text = it },
                    label = { Text("Your Tycoon Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFC700),
                        unfocusedBorderColor = SoftBorder,
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite,
                        focusedLabelColor = Color(0xFFFFC700)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("player_name_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmName(text.ifBlank { "Tycoon" }) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC700),
                    contentColor = Color(0xFF0F172A)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_player_name_button")
            ) {
                Text("Publish to Forbes 250 🚀", fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp
    )
}
