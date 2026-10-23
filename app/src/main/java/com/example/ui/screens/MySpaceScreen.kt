package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.util.Formatters
import com.example.viewmodel.TycoonUiState
import com.example.viewmodel.TycoonViewModel

enum class MySpaceSection(val label: String, val iconEmoji: String) {
    TAX_OFFICE("Tax Office", "🏛️"),
    FORBES_250("Forbes 250", "🏆"),
    COLLECTIBLES("Collectibles", "💎")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySpaceScreen(
    viewModel: TycoonViewModel,
    uiState: TycoonUiState,
    onBack: () -> Unit
) {
    // Intercept back navigation
    BackHandler {
        onBack()
    }

    var activeSection by remember { mutableStateOf(MySpaceSection.TAX_OFFICE) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF0A0E1A),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "My Space",
                                color = Color(0xFFFFC700),
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Surface(
                                color = Color(0xFFFFC700).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "EXECUTIVE HQ",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 9.sp,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${uiState.playerName} • Net Worth ${Formatters.formatCurrency(uiState.totalNetWorth)}",
                            color = TextMutedSoft,
                            fontSize = 11.sp,
                            fontFamily = AppFontFamily
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("my_space_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Tycoon",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Forbes rank chip in top bar
                    val rank = uiState.forbesRank
                    Surface(
                        color = if (rank != null) Color(0xFF065F46) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { activeSection = MySpaceSection.FORBES_250 }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(if (rank != null) "🏆" else "🌍", fontSize = 12.sp)
                            Text(
                                text = if (rank != null) "#$rank in World" else "Unranked",
                                color = if (rank != null) Color(0xFF00E676) else TextMutedSoft,
                                fontSize = 11.sp,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121829),
                    titleContentColor = Color.White
                ),
                windowInsets = WindowInsets.statusBars
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0A0E1A))
        ) {
            // Section Switcher Ribbon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121829))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MySpaceSection.values().forEach { section ->
                    val isSelected = activeSection == section
                    Surface(
                        color = if (isSelected) Color(0xFFFFC700) else Color(0xFF1E293B),
                        contentColor = if (isSelected) Color(0xFF0F172A) else TextMutedSoft,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeSection = section }
                            .testTag("tab_${section.name.lowercase()}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 7.dp)
                        ) {
                            Text(text = section.iconEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = section.label,
                                fontSize = 10.sp,
                                fontFamily = AppFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Sub-Screen Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                when (activeSection) {
                    MySpaceSection.TAX_OFFICE -> {
                        TaxOfficeSubScreen(
                            uiState = uiState,
                            onPayTax = { viewModel.payTax() }
                        )
                    }
                    MySpaceSection.FORBES_250 -> {
                        Forbes250SubScreen(
                            uiState = uiState,
                            onPromptName = { viewModel.showNamePromptDialog() },
                            onCelebrate = { viewModel.reopenForbesCelebration() }
                        )
                    }
                    MySpaceSection.COLLECTIBLES -> {
                        CollectiblesSubScreen(
                            uiState = uiState,
                            onBuyCollectible = { viewModel.buyCollectible(it) }
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// SUB-SCREEN 1: TAX OFFICE & FORCED ASSET LIQUIDATION
// ---------------------------------------------------------------------------
@Composable
private fun TaxOfficeSubScreen(
    uiState: TycoonUiState,
    onPayTax: () -> Unit
) {
    val taxStatus = uiState.taxStatus
    val rollingSeconds = taxStatus.rollingWindowSeconds
    val rollingMinutes = rollingSeconds / 60
    val rollingSecs = rollingSeconds % 60

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Main Tax Overview Card
            Surface(
                color = Color(0xFF121829),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (taxStatus.isWarningActive) Color(0xFFEF4444) else Color(0xFF334155)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🏛️", fontSize = 24.sp)
                            Column {
                                Text(
                                    text = "Department of Revenue",
                                    color = Color.White,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Rolling 15-Minute Tax Cycle",
                                    color = TextMutedSoft,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            color = if (taxStatus.isWarningActive) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (taxStatus.isWarningActive) "⚠️ TAX WARNING" else "🟢 COMPLIANT",
                                color = if (taxStatus.isWarningActive) Color(0xFFFCA5A5) else Color(0xFF00E676),
                                fontSize = 10.sp,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFF1E293B))

                    // Rolling Timer & Progress
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Rolling Cycle Elapsed:", color = TextMutedSoft, fontSize = 11.sp)
                            Text(
                                text = String.format("%02d:%02d / 15:00", rollingMinutes.coerceAtMost(15), rollingSecs),
                                color = Color(0xFFFFD54F),
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        LinearProgressIndicator(
                            progress = { (rollingSeconds.coerceAtMost(900).toFloat() / 900f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = if (taxStatus.isWarningActive) Color(0xFFEF4444) else Color(0xFFFFC700),
                            trackColor = Color(0xFF1E293B)
                        )
                    }

                    // Financial metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Taxable Income (15m)", color = TextMutedSoft, fontSize = 10.sp)
                                Text(
                                    text = Formatters.formatExactCurrency(taxStatus.accumulatedTaxableIncome),
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Tax Due (2.5%)", color = Color(0xFFFCA5A5), fontSize = 10.sp)
                                Text(
                                    text = Formatters.formatExactCurrency(taxStatus.currentTaxDue),
                                    color = Color(0xFFEF4444),
                                    fontSize = 13.sp,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    // Warning & Pay Action
                    if (taxStatus.isWarningActive && taxStatus.currentTaxDue > 0.0) {
                        val minsLeft = taxStatus.warningRemainingSeconds / 60
                        val secsLeft = taxStatus.warningRemainingSeconds % 60
                        Surface(
                            color = Color(0xFF2A0808),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "🚨 Grace Period Expiring in ${String.format("%02d:%02d", minsLeft, secsLeft)}!",
                                    color = Color(0xFFFCA5A5),
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "If unpaid by minute 20:00, all uncollected revenues will be revoked and your least valuable assets will be forcefully liquidated via cascade.",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp
                                )
                                Button(
                                    onClick = onPayTax,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00E676),
                                        contentColor = Color(0xFF064E3B)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Pay Tax Now (${Formatters.formatExactCurrency(taxStatus.currentTaxDue)})",
                                        fontFamily = AppFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else if (taxStatus.currentTaxDue > 0.0) {
                        Button(
                            onClick = onPayTax,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFC700),
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Proactively Pay Tax (${Formatters.formatExactCurrency(taxStatus.currentTaxDue)})",
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            // Liquidation Cascade Order Information Card
            Surface(
                color = Color(0xFF121829),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("⚖️", fontSize = 20.sp)
                        Text(
                            text = "Forced Liquidation Cascade Protocol",
                            color = Color(0xFFFFD54F),
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Text(
                        text = "If tax debt remains unsettled when the 20-minute timer hits zero, the Revenue Authority initiates sequential liquidation starting from your least valuable asset:",
                        color = TextMutedSoft,
                        fontSize = 11.sp
                    )

                    val cascadeSteps = listOf(
                        Pair("Tier 1: Crypto Assets", "Least valuable crypto positions liquidated first"),
                        Pair("Tier 2: Business Ventures", "Lowest-income enterprise levels auctioned off"),
                        Pair("Tier 3: Rental Real Estate", "Suburban or lowest-cost properties repossessed"),
                        Pair("Tier 4: Personal Collectibles", "Mansions, vehicles, and gems liquidated last")
                    )

                    cascadeSteps.forEachIndexed { idx, (tier, desc) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Surface(
                                color = Color(0xFF334155),
                                shape = CircleShape,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("${idx + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Column {
                                Text(tier, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text(desc, color = TextMutedSoft, fontSize = 10.sp)
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFF1E293B))

                    Text(
                        text = "Any surplus cash remaining after satisfying the tax debt is returned immediately to 'My Pocket Cash'.",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontFamily = AppFontFamily
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// SUB-SCREEN 2: FORBES 250 RICH LIST
// ---------------------------------------------------------------------------
@Composable
private fun Forbes250SubScreen(
    uiState: TycoonUiState,
    onPromptName: () -> Unit,
    onCelebrate: () -> Unit
) {
    val playerNetWorth = uiState.totalNetWorth
    val billionaires = remember { ForbesRichListCatalog.billionaires }
    val playerRank = uiState.forbesRank
    val threshold = remember { ForbesRichListCatalog.qualificationThreshold }

    var searchQuery by remember { mutableStateOf("") }
    val filteredBillionaires = remember(searchQuery) {
        if (searchQuery.isBlank()) billionaires
        else billionaires.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.company.contains(searchQuery, ignoreCase = true)
        }
    }

    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Player Forbes Status Banner
        Surface(
            color = if (playerRank != null) Color(0xFF064E3B) else Color(0xFF1E293B),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (playerRank != null) Color(0xFF00E676) else Color(0xFF334155)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (playerRank != null) "👑" else "💼", fontSize = 26.sp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = uiState.playerName,
                                color = Color.White,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            IconButton(
                                onClick = onPromptName,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = TextMutedSoft, modifier = Modifier.size(14.dp))
                            }
                        }
                        Text(
                            text = if (playerRank != null) "World Rank #$playerRank of 250" else "Ranked #250+ (Need ${Formatters.formatCurrency(threshold)})",
                            color = if (playerRank != null) Color(0xFF00E676) else Color(0xFFFFD54F),
                            fontSize = 11.sp,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (playerRank != null) {
                    Button(
                        onClick = onCelebrate,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC700),
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Celebrate! 🥂", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search 250 billionaires...", color = TextMutedSoft, fontSize = 11.sp) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMutedSoft) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFC700),
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF121829),
                unfocusedContainerColor = Color(0xFF121829)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // The List of 250
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize().testTag("forbes_250_list")
        ) {
            items(filteredBillionaires, key = { it.rank }) { b ->
                // Check if player should be displayed above this billionaire
                val isPlayerAbove = playerRank != null && playerRank == b.rank

                if (isPlayerAbove) {
                    // Player Card Inserted
                    Surface(
                        color = Color(0xFF282810),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFC700)),
                        modifier = Modifier.fillMaxWidth().testTag("forbes_player_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    color = Color(0xFFFFC700),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("#$playerRank", color = Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                                Text("👑", fontSize = 20.sp)
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(uiState.playerName, color = Color(0xFFFFC700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Surface(color = Color(0xFF00E676), shape = RoundedCornerShape(4.dp)) {
                                            Text("YOU", color = Color(0xFF064E3B), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Text("Global Money Tycoon Empire", color = TextMutedSoft, fontSize = 10.sp)
                                }
                            }
                            Text(
                                text = Formatters.formatCurrency(playerNetWorth),
                                color = Color(0xFF00E676),
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Regular Billionaire Item
                Surface(
                    color = Color(0xFF121829),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = CircleShape,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "#${b.rank}",
                                        color = if (b.rank <= 10) Color(0xFFFFD54F) else TextMutedSoft,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(b.avatarEmoji, fontSize = 18.sp)

                            Column {
                                Text(
                                    text = b.name,
                                    color = if (b.rank <= 3) Color(0xFFFFC700) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${b.company} • ${b.country}",
                                    color = TextMutedSoft,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Text(
                            text = Formatters.formatCurrency(b.netWorth),
                            color = Color(0xFFE2E8F0),
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// SUB-SCREEN 3: ULTIMATE COLLECTIBLES MARKETPLACE
// ---------------------------------------------------------------------------
@Composable
private fun CollectiblesSubScreen(
    uiState: TycoonUiState,
    onBuyCollectible: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<CollectibleCategory?>(null) }
    val allItems = uiState.collectibles
    val filteredItems = remember(selectedCategory, allItems) {
        if (selectedCategory == null) allItems
        else allItems.filter { it.config.category == selectedCategory }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().testTag("collectibles_catalog")
    ) {
        item {
            // Category selector filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = if (selectedCategory == null) Color(0xFFFFC700) else Color(0xFF1E293B),
                    contentColor = if (selectedCategory == null) Color(0xFF0F172A) else TextMutedSoft,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { selectedCategory = null }
                ) {
                    Text(
                        text = "All Items",
                        fontSize = 11.sp,
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                CollectibleCategory.values().forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        color = if (isSelected) Color(0xFFFFC700) else Color(0xFF1E293B),
                        contentColor = if (isSelected) Color(0xFF0F172A) else TextMutedSoft,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(cat.iconEmoji, fontSize = 11.sp)
                            Text(
                                text = cat.displayName,
                                fontSize = 11.sp,
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        items(filteredItems, key = { it.config.id }) { item ->
            val config = item.config
            val canAfford = uiState.cash >= config.cost

            Surface(
                color = Color(0xFF121829),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (item.isOwned) Color(0xFF00E676) else Color(0xFF1E293B)
                ),
                modifier = Modifier.fillMaxWidth().testTag("collectible_${config.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(config.iconEmoji, fontSize = 26.sp)
                            Column {
                                Text(
                                    text = config.name,
                                    color = Color.White,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${config.category.displayName} • ${config.statusTitle}",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 10.sp,
                                    fontFamily = AppFontFamily
                                )
                            }
                        }

                        if (item.isOwned) {
                            Surface(
                                color = Color(0xFF064E3B),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "OWNED ✓",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "+${config.prestigePoints} Prestige",
                                    color = Color(0xFFA855F7),
                                    fontSize = 10.sp,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = config.description,
                        color = TextMutedSoft,
                        fontSize = 11.sp,
                        fontFamily = AppFontFamily
                    )

                    HorizontalDivider(color = Color(0xFF1E293B))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Market Valuation:", color = TextMutedSoft, fontSize = 10.sp)
                            Text(
                                text = Formatters.formatCurrency(config.cost),
                                color = Color(0xFFFFC700),
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        if (!item.isOwned) {
                            Button(
                                onClick = { onBuyCollectible(config.id) },
                                enabled = canAfford,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFC700),
                                    contentColor = Color(0xFF0F172A),
                                    disabledContainerColor = Color(0xFF1E293B),
                                    disabledContentColor = TextMutedSoft
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (canAfford) "Acquire Asset" else "Need Funds",
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
    }
}

