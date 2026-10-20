package com.example.model

data class LuxuryConfig(
    val id: String,
    val name: String,
    val description: String,
    val cost: Double,
    val prestigePoints: Int,
    val iconName: String
)

data class LuxuryState(
    val config: LuxuryConfig,
    val isOwned: Boolean = false
) {
    val currentValuation: Double
        get() = if (isOwned) config.cost else 0.0
}

object LuxuryCatalog {
    val SPORTS_CAR = LuxuryConfig(
        id = "sports_car",
        name = "Italian V12 Supercar",
        description = "Precision-engineered exotic speed machine turning heads in financial districts.",
        cost = 80_000.0,
        prestigePoints = 800,
        iconName = "directions_car"
    )

    val PRIVATE_JET = LuxuryConfig(
        id = "private_jet",
        name = "Gulfstream Intercontinental Jet",
        description = "Transcontinental bespoke executive jet for global dealmaking.",
        cost = 2_000_000.0,
        prestigePoints = 25_000,
        iconName = "flight"
    )

    val SUPERYACHT = LuxuryConfig(
        id = "superyacht",
        name = "300ft Monaco Megayacht",
        description = "Flagship ocean vessel with twin helipads and submarine bay.",
        cost = 15_000_000.0,
        prestigePoints = 200_000,
        iconName = "sailing"
    )

    val allConfigs = listOf(SPORTS_CAR, PRIVATE_JET, SUPERYACHT)
}
