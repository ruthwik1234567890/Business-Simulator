package com.example.model

import kotlin.math.pow

data class ManagerConfig(
    val name: String,
    val title: String,
    val cost: Double,
    val description: String,
    val avatarIcon: String
)

data class MilestoneConfig(
    val level: Int,
    val multiplier: Double,
    val label: String
)

data class EnterpriseConfig(
    val id: String,
    val name: String,
    val description: String,
    val baseCost: Double,
    val baseHourlyIncome: Double,
    val costMultiplier: Double,
    val cycleSeconds: Float,
    val manager: ManagerConfig,
    val milestones: List<MilestoneConfig>,
    val imageResName: String,
    val isBank: Boolean = false
) {
    fun getUpgradeCost(currentLevel: Int): Double {
        return baseCost * costMultiplier.pow(currentLevel.toDouble())
    }

    fun getCostForLevels(currentLevel: Int, levelsToAdd: Int): Double {
        var total = 0.0
        for (i in 0 until levelsToAdd) {
            total += baseCost * costMultiplier.pow((currentLevel + i).toDouble())
        }
        return total
    }

    fun getMaxAffordableLevels(currentLevel: Int, currentCash: Double): Pair<Int, Double> {
        var count = 0
        var totalCost = 0.0
        while (true) {
            val nextCost = baseCost * costMultiplier.pow((currentLevel + count).toDouble())
            if (totalCost + nextCost <= currentCash) {
                totalCost += nextCost
                count++
                if (count >= 1000) break
            } else {
                break
            }
        }
        return Pair(count, totalCost)
    }

    fun getMilestoneMultiplier(level: Int): Double {
        var mult = 1.0
        for (m in milestones) {
            if (level >= m.level) {
                mult *= m.multiplier
            }
        }
        return mult
    }

    fun getHourlyIncome(level: Int): Double {
        if (level <= 0) return 0.0
        val base = baseHourlyIncome * level
        return base * getMilestoneMultiplier(level)
    }
}

data class EnterpriseState(
    val config: EnterpriseConfig,
    val level: Int = 0,
    val isManagerHired: Boolean = false,
    val cycleProgress: Float = 0f
) {
    val upgradeCost: Double
        get() = config.getUpgradeCost(level)

    val currentHourlyIncome: Double
        get() = config.getHourlyIncome(level)

    val nextMilestone: MilestoneConfig?
        get() = config.milestones.firstOrNull { it.level > level }
}

object EnterpriseCatalog {
    val COFFEE_SHOP = EnterpriseConfig(
        id = "coffee_shop",
        name = "Artisan Coffee Roastery",
        description = "High-margin espresso bar and bean subscription network.",
        baseCost = 1_000.0,
        baseHourlyIncome = 50.0,
        costMultiplier = 1.5,
        cycleSeconds = 3.0f,
        manager = ManagerConfig(
            name = "Marco Rossi",
            title = "Master Barista & Ops Lead",
            cost = 5_000.0,
            description = "Automates roastery batches and speeds up service.",
            avatarIcon = "coffee"
        ),
        milestones = listOf(
            MilestoneConfig(10, 2.0, "2x Speed (10 Levels)"),
            MilestoneConfig(25, 2.0, "2x Yield (25 Levels)"),
            MilestoneConfig(50, 4.0, "4x Revenue (50 Levels)"),
            MilestoneConfig(100, 10.0, "10x Franchise (100 Levels)")
        ),
        imageResName = "img_coffee_shop"
    )

    val TAXI_FLEET = EnterpriseConfig(
        id = "taxi_fleet",
        name = "Metropolitan Taxi & Rideshare",
        description = "Hybrid electric commercial urban transportation network.",
        baseCost = 10_000.0,
        baseHourlyIncome = 600.0,
        costMultiplier = 1.5,
        cycleSeconds = 6.0f,
        manager = ManagerConfig(
            name = "Elena Vance",
            title = "Fleet Logistics Director",
            cost = 45_000.0,
            description = "AI dispatch routing for non-stop passenger pickups.",
            avatarIcon = "local_taxi"
        ),
        milestones = listOf(
            MilestoneConfig(10, 2.0, "2x Speed (10 Levels)"),
            MilestoneConfig(25, 2.0, "2x Yield (25 Levels)"),
            MilestoneConfig(50, 4.0, "4x Revenue (50 Levels)"),
            MilestoneConfig(100, 10.0, "10x Monopoly (100 Levels)")
        ),
        imageResName = "img_coffee_shop"
    )

    val SOFTWARE_STUDIO = EnterpriseConfig(
        id = "software_studio",
        name = "Apex SaaS Engine",
        description = "Enterprise cloud workflow automation and AI API platform.",
        baseCost = 100_000.0,
        baseHourlyIncome = 7_000.0,
        costMultiplier = 1.6,
        cycleSeconds = 12.0f,
        manager = ManagerConfig(
            name = "Dr. Aris Thorne",
            title = "Chief Technology Officer",
            cost = 400_000.0,
            description = "Continuous deployment pipelines delivering recurring ARR.",
            avatarIcon = "terminal"
        ),
        milestones = listOf(
            MilestoneConfig(10, 2.0, "2x Speed (10 Levels)"),
            MilestoneConfig(25, 2.5, "2.5x Yield (25 Levels)"),
            MilestoneConfig(50, 4.0, "4x Scale (50 Levels)"),
            MilestoneConfig(100, 10.0, "10x Cloud King (100 Levels)")
        ),
        imageResName = "img_software_studio"
    )

    val COMMERCIAL_BANK = EnterpriseConfig(
        id = "commercial_bank",
        name = "Wall Street Commercial Bank",
        description = "Tier-1 investment institution generating compound yields on deposits.",
        baseCost = 1_000_000.0,
        baseHourlyIncome = 85_000.0,
        costMultiplier = 1.7,
        cycleSeconds = 24.0f,
        manager = ManagerConfig(
            name = "Sterling Sterling",
            title = "Managing Director & Chief Risk Officer",
            cost = 3_500_000.0,
            description = "Automates capital compounding and high-yield reserve lending.",
            avatarIcon = "account_balance"
        ),
        milestones = listOf(
            MilestoneConfig(5, 2.0, "2x Lending Yield (5 Levels)"),
            MilestoneConfig(10, 3.0, "3x Reserves (10 Levels)"),
            MilestoneConfig(25, 5.0, "5x Sovereign Tier (25 Levels)")
        ),
        imageResName = "img_commercial_bank",
        isBank = true
    )

    val allConfigs = listOf(COFFEE_SHOP, TAXI_FLEET, SOFTWARE_STUDIO, COMMERCIAL_BANK)
}
