package com.example.model

data class EconomicEvent(
    val headline: String,
    val category: String, // "MARKET", "TECH", "MACRO", "VENTURE"
    val affectedAssetId: String?,
    val priceImpactPercent: Double = 0.0,
    val durationSeconds: Int = 20,
    val isBullish: Boolean = true
)

object EconomicEventPool {
    val SAMPLE_EVENTS = listOf(
        EconomicEvent(
            headline = "FEDERAL RESERVE INJECTS LIQUIDITY: MARKETS RALLY ACROSS EQUITIES & CRYPTO",
            category = "MACRO",
            affectedAssetId = null,
            priceImpactPercent = 8.5,
            isBullish = true
        ),
        EconomicEvent(
            headline = "TECHCORP ANNOUNCES QUANTUM CHIP BREAKTHROUGH: SHARES SURGE +18%",
            category = "TECH",
            affectedAssetId = "techcorp",
            priceImpactPercent = 18.0,
            isBullish = true
        ),
        EconomicEvent(
            headline = "GLOBAL SUPPLY CRUNCH: COMMODITIES DRIFT HIGHER AS VOLATILITY EXPANDS",
            category = "MARKET",
            affectedAssetId = "energyx",
            priceImpactPercent = 12.0,
            isBullish = true
        ),
        EconomicEvent(
            headline = "BITCOIN BREAKS MAJOR RESISTANCE AS INSTITUTIONAL INFLOWS ACCELERATE",
            category = "MARKET",
            affectedAssetId = "bitcoin",
            priceImpactPercent = 22.0,
            isBullish = true
        ),
        EconomicEvent(
            headline = "REGULATORY CLOUD: CRYPTO TRADING FACES SHORT-TERM LIQUIDATION CRUNCH",
            category = "MARKET",
            affectedAssetId = "memecoin",
            priceImpactPercent = -25.0,
            isBullish = false
        ),
        EconomicEvent(
            headline = "CONSUMER SPENDING HITS RECORD HIGHS BOOSTING RETAIL ENTERPRISES",
            category = "VENTURE",
            affectedAssetId = "retailplus",
            priceImpactPercent = 9.5,
            isBullish = true
        )
    )

    fun getRandomEvent(): EconomicEvent {
        return SAMPLE_EVENTS.random()
    }
}
