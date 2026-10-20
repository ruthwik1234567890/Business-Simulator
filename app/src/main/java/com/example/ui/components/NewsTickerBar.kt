package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Icon
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SoftCardBg)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag("news_ticker_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(badgeBg)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = currentEvent.category,
                color = badgeText,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = AppFontFamily
            )
        }

        Icon(
            imageVector = Icons.Default.Campaign,
            contentDescription = "News",
            tint = BrightGold,
            modifier = Modifier.size(16.dp)
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
