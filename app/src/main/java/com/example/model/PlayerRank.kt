package com.example.model

data class PlayerRank(
    val title: String,
    val subtitle: String,
    val minPrestige: Int,
    val minNetWorth: Double,
    val badgeIcon: String
) {
    companion object {
        val ALL_RANKS = listOf(
            PlayerRank(
                title = "Apprentice Hustler",
                subtitle = "Starting capital operator learning the ropes.",
                minPrestige = 0,
                minNetWorth = 0.0,
                badgeIcon = "badge"
            ),
            PlayerRank(
                title = "Venture Associate",
                subtitle = "Scaling cash flow across diversified commercial shops.",
                minPrestige = 50,
                minNetWorth = 25_000.0,
                badgeIcon = "military_tech"
            ),
            PlayerRank(
                title = "Syndicate Principal",
                subtitle = "Active asset investor acquiring real estate and equities.",
                minPrestige = 800,
                minNetWorth = 250_000.0,
                badgeIcon = "workspace_premium"
            ),
            PlayerRank(
                title = "Wall Street Titan",
                subtitle = "Institutional market mover controlling tier-1 bank reserves.",
                minPrestige = 25_000,
                minNetWorth = 3_000_000.0,
                badgeIcon = "stars"
            ),
            PlayerRank(
                title = "Global Oligarch",
                subtitle = "Bespoke billionaire shaping global macroeconomic currents.",
                minPrestige = 150_000,
                minNetWorth = 50_000_000.0,
                badgeIcon = "diamond"
            )
        )

        fun calculateRank(prestige: Int, netWorth: Double): PlayerRank {
            return ALL_RANKS.lastOrNull { rank ->
                prestige >= rank.minPrestige || netWorth >= rank.minNetWorth
            } ?: ALL_RANKS.first()
        }
    }
}
