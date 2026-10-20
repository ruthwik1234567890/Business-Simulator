package com.example.model

data class MarketAsset(
    val id: String,
    val symbol: String,
    val name: String,
    val isCrypto: Boolean,
    val currentPrice: Double,
    val previousPrice: Double,
    val basePrice: Double,
    val volatility: Double,
    val drift: Double,
    val ownedUnits: Double = 0.0,
    val averageCostBasis: Double = 0.0,
    val priceHistory: List<Double> = emptyList()
) {
    val priceChange: Double
        get() = currentPrice - previousPrice

    val priceChangePercent: Double
        get() = if (previousPrice > 0.0) ((currentPrice - previousPrice) / previousPrice) * 100.0 else 0.0

    val isPositive: Boolean
        get() = priceChange >= 0.0

    val totalValue: Double
        get() = currentPrice * ownedUnits

    val totalUnrealizedPnL: Double
        get() = if (ownedUnits > 0.0) (currentPrice - averageCostBasis) * ownedUnits else 0.0

    val pnlPercent: Double
        get() = if (averageCostBasis > 0.0 && ownedUnits > 0.0) ((currentPrice - averageCostBasis) / averageCostBasis) * 100.0 else 0.0
}

object InitialMarketCatalog {
    fun createInitialAssets(): List<MarketAsset> = listOf(
        MarketAsset(
            id = "techcorp",
            symbol = "TECH",
            name = "TechCorp Systems",
            isCrypto = false,
            currentPrice = 185.50,
            previousPrice = 184.20,
            basePrice = 180.00,
            volatility = 0.015,
            drift = 0.002,
            priceHistory = listOf(178.0, 180.5, 182.0, 181.5, 184.2, 185.5)
        ),
        MarketAsset(
            id = "energyx",
            symbol = "NRGX",
            name = "EnergyX Clean Power",
            isCrypto = false,
            currentPrice = 72.80,
            previousPrice = 73.10,
            basePrice = 70.00,
            volatility = 0.02,
            drift = 0.001,
            priceHistory = listOf(68.0, 70.0, 71.5, 73.0, 73.1, 72.8)
        ),
        MarketAsset(
            id = "retailplus",
            symbol = "RTLP",
            name = "RetailPlus Global",
            isCrypto = false,
            currentPrice = 45.20,
            previousPrice = 44.80,
            basePrice = 45.00,
            volatility = 0.012,
            drift = 0.001,
            priceHistory = listOf(43.5, 44.0, 44.5, 45.0, 44.8, 45.2)
        ),
        MarketAsset(
            id = "bitcoin",
            symbol = "BTC",
            name = "BitCoin Digital Reserve",
            isCrypto = true,
            currentPrice = 64_500.00,
            previousPrice = 63_200.00,
            basePrice = 60_000.00,
            volatility = 0.065,
            drift = 0.004,
            priceHistory = listOf(58000.0, 61000.0, 59500.0, 62000.0, 63200.0, 64500.0)
        ),
        MarketAsset(
            id = "etherx",
            symbol = "ETHX",
            name = "EtherX Smart Fuel",
            isCrypto = true,
            currentPrice = 3_420.00,
            previousPrice = 3_510.00,
            basePrice = 3_200.00,
            volatility = 0.075,
            drift = 0.003,
            priceHistory = listOf(3100.0, 3300.0, 3250.0, 3450.0, 3510.0, 3420.0)
        ),
        MarketAsset(
            id = "memecoin",
            symbol = "MEME",
            name = "MemeCoin Moon Rocket",
            isCrypto = true,
            currentPrice = 0.42,
            previousPrice = 0.38,
            basePrice = 0.35,
            volatility = 0.18,
            drift = 0.005,
            priceHistory = listOf(0.25, 0.30, 0.28, 0.35, 0.38, 0.42)
        )
    )
}
