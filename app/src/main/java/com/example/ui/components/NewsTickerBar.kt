package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EconomicEvent
import com.example.ui.theme.*

@Composable
fun NewsTickerBar(
    currentEvent: EconomicEvent?,
    modifier: Modifier = Modifier
) {
    if (currentEvent == null) return

    val badgeBg = if (currentEvent.isBullish) VibrantGreen.copy(alpha = 0.2f) else VividCoral.copy(alpha = 0.2f)
    val badgeText = if (currentEvent.isBullish) VibrantGreenLight else VividCoral
    val headline = currentEvent.headline

    Surface(
        color = Color(0xFF131B2E),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .testTag("news_ticker_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = badgeBg,
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(badgeText)
                    )
                    Text(
                        text = currentEvent.category.uppercase(),
                        color = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = DisplayFontFamily
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = "News",
                tint = BrightGold,
                modifier = Modifier.size(15.dp)
            )

            Text(
                text = headline,
                color = TextPureWhite,
                fontSize = 11.sp,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
