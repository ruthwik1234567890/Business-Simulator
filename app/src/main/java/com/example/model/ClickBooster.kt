package com.example.model

data class ClickBooster(
    val tier: Int,
    val name: String,
    val perClickAmount: Double,
    val cost: Double,
    val iconName: String,
    val description: String
)

object ClickBoosterCatalog {
    val ALL_BOOSTERS = listOf(
        ClickBooster(
            tier = 1,
            name = "Manual Hustle",
            perClickAmount = 0.50,
            cost = 0.0,
            iconName = "touch_app",
            description = "Starting baseline. Every tap earns $0.50 active cash."
        ),
        ClickBooster(
            tier = 2,
            name = "Cash Register",
            perClickAmount = 1.50,
            cost = 25.0,
            iconName = "point_of_sale",
            description = "Mechanical ringing till boosts earnings to $1.50 per click."
        ),
        ClickBooster(
            tier = 3,
            name = "Digital POS Terminal",
            perClickAmount = 5.00,
            cost = 120.0,
            iconName = "credit_card",
            description = "Contactless payment reader speeds yield to $5.00 per click."
        ),
        ClickBooster(
            tier = 4,
            name = "Barcode Laser Scanner",
            perClickAmount = 18.00,
            cost = 600.0,
            iconName = "qr_code_scanner",
            description = "High-speed optical scanner accelerates earnings to $18.00 per click."
        ),
        ClickBooster(
            tier = 5,
            name = "Algorithmic Clicker",
            perClickAmount = 75.00,
            cost = 3_500.0,
            iconName = "smart_toy",
            description = "Automated high-rate script firing $75.00 per click."
        ),
        ClickBooster(
            tier = 6,
            name = "Trading Floor Desk",
            perClickAmount = 350.00,
            cost = 20_000.0,
            iconName = "terminal",
            description = "Dedicated desk broker clicking $350.00 per tap."
        ),
        ClickBooster(
            tier = 7,
            name = "High Frequency Engine",
            perClickAmount = 1_800.00,
            cost = 125_000.0,
            iconName = "speed",
            description = "Sub-millisecond fiber optic engine generating $1,800.00 per click."
        ),
        ClickBooster(
            tier = 8,
            name = "Quantum Arbitrage",
            perClickAmount = 10_000.00,
            cost = 800_000.0,
            iconName = "bolt",
            description = "Quantum computing pulses yielding $10,000.00 per tap."
        ),
        ClickBooster(
            tier = 9,
            name = "Federal Reserve Mint",
            perClickAmount = 65_000.00,
            cost = 5_000_000.0,
            iconName = "account_balance",
            description = "Sovereign liquidity printer producing $65,000.00 per click."
        ),
        ClickBooster(
            tier = 10,
            name = "Global Reserve Bank",
            perClickAmount = 400_000.00,
            cost = 30_000_000.0,
            iconName = "language",
            description = "Worldwide financial clearing house generating $400,000.00 per tap."
        ),
        ClickBooster(
            tier = 11,
            name = "Galactic Central Matrix",
            perClickAmount = 2_500_000.00,
            cost = 200_000_000.0,
            iconName = "auto_awesome",
            description = "Ultimate financial singularity generating $2,500,000.00 per tap."
        )
    )

    fun getBoosterForTier(tier: Int): ClickBooster {
        return ALL_BOOSTERS.find { it.tier == tier } ?: ALL_BOOSTERS.first()
    }

    fun getNextBooster(currentTier: Int): ClickBooster? {
        return ALL_BOOSTERS.find { it.tier == currentTier + 1 }
    }
}
