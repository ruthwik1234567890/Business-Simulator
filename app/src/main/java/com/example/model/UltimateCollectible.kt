package com.example.model

enum class CollectibleCategory(val displayName: String, val iconEmoji: String) {
    MANSIONS("Mansions", "🏰"),
    PRECIOUS_ASSETS("Precious Assets", "💎"),
    VEHICLES("Vehicles", "🏎️"),
    RARITIES("Rarities", "👑")
}

data class CollectibleConfig(
    val id: String,
    val name: String,
    val category: CollectibleCategory,
    val description: String,
    val cost: Double,
    val prestigePoints: Int,
    val iconEmoji: String,
    val statusTitle: String
)

data class CollectibleState(
    val config: CollectibleConfig,
    val isOwned: Boolean = false
) {
    val currentValuation: Double
        get() = if (isOwned) config.cost else 0.0
}

object UltimateCollectiblesCatalog {
    val allConfigs = listOf(
        // Category 1: Mansions (Personal residences, not for rental)
        CollectibleConfig(
            id = "mansion_beverly_hills",
            name = "Beverly Hills Hyper-Mansion",
            category = CollectibleCategory.MANSIONS,
            description = "35,000 sq ft private limestone compound with dual infinity pools and bowling alley.",
            cost = 45_000_000.0,
            prestigePoints = 50_000,
            iconEmoji = "🏡",
            statusTitle = "Hollywood Royalty"
        ),
        CollectibleConfig(
            id = "mansion_alpine_chateau",
            name = "Swiss Alpine Mega-Chateau",
            category = CollectibleCategory.MANSIONS,
            description = "Heated helipad, private ski funicular, panoramic Matterhorn glacier views.",
            cost = 120_000_000.0,
            prestigePoints = 150_000,
            iconEmoji = "🏔️",
            statusTitle = "Alpine Sovereign"
        ),
        CollectibleConfig(
            id = "mansion_riviera_penthouse",
            name = "French Riviera Sky Penthouse",
            category = CollectibleCategory.MANSIONS,
            description = "Three-story Monaco penthouse with rooftop harbor yacht anchorage and bulletproof glass.",
            cost = 280_000_000.0,
            prestigePoints = 400_000,
            iconEmoji = "🏰",
            statusTitle = "Monaco Titan"
        ),
        CollectibleConfig(
            id = "mansion_private_island",
            name = "Private Caribbean Atoll Island",
            category = CollectibleCategory.MANSIONS,
            description = "500-acre sovereign estate with private runway, submarine lagoon, and solar grid.",
            cost = 850_000_000.0,
            prestigePoints = 1_200_000,
            iconEmoji = "🏝️",
            statusTitle = "Island Overlord"
        ),

        // Category 2: Precious Assets
        CollectibleConfig(
            id = "asset_silver_bullion",
            name = "1,000 oz Fine Silver Bullion",
            category = CollectibleCategory.PRECIOUS_ASSETS,
            description = ".999 pure refined bullion bars stacked in fortified deep-bank deposit vault.",
            cost = 50_000.0,
            prestigePoints = 500,
            iconEmoji = "🪙",
            statusTitle = "Precious Metal Stacker"
        ),
        CollectibleConfig(
            id = "asset_gold_bar",
            name = "100 oz 24K Swiss Gold Bar",
            category = CollectibleCategory.PRECIOUS_ASSETS,
            description = "Serialized Zurich hallmark 999.9 fine investment gold cast bar.",
            cost = 250_000.0,
            prestigePoints = 2_500,
            iconEmoji = "🥇",
            statusTitle = "Gold Reserve Holder"
        ),
        CollectibleConfig(
            id = "asset_raw_diamond",
            name = "50-Carat Flawless Raw Diamond",
            category = CollectibleCategory.PRECIOUS_ASSETS,
            description = "Uncut D-flawless type IIa octahedral diamond recovered from Botswana mines.",
            cost = 3_500_000.0,
            prestigePoints = 45_000,
            iconEmoji = "💎",
            statusTitle = "Diamond Magnate"
        ),
        CollectibleConfig(
            id = "asset_imperial_ruby",
            name = "Burmese Pigeon Blood Ruby",
            category = CollectibleCategory.PRECIOUS_ASSETS,
            description = "Extremely rare untreated 18-carat crimson ruby certified by royal gemological institute.",
            cost = 12_000_000.0,
            prestigePoints = 160_000,
            iconEmoji = "♦️",
            statusTitle = "Imperial Gem Connoisseur"
        ),

        // Category 3: Vehicles
        CollectibleConfig(
            id = "vehicle_v12_supercar",
            name = "Italian Quad-Turbo V12 Hypercar",
            category = CollectibleCategory.VEHICLES,
            description = "Handcrafted carbon-titanium chassis, 1,500 horsepower, limited 1 of 5 world edition.",
            cost = 3_200_000.0,
            prestigePoints = 40_000,
            iconEmoji = "🏎️",
            statusTitle = "Hypercar Collector"
        ),
        CollectibleConfig(
            id = "vehicle_supersonic_jet",
            name = "Bespoke Supersonic Private Jetliner",
            category = CollectibleCategory.VEHICLES,
            description = "Mach 1.8 executive luxury cabin featuring master bedroom and conference suite.",
            cost = 95_000_000.0,
            prestigePoints = 130_000,
            iconEmoji = "✈️",
            statusTitle = "Jet-Set Elite"
        ),
        CollectibleConfig(
            id = "vehicle_monaco_megayacht",
            name = "300ft Monaco Tri-Deck Megayacht",
            category = CollectibleCategory.VEHICLES,
            description = "Equipped with tender garages, spa salon, dual helipads, and 24-person crew quarters.",
            cost = 180_000_000.0,
            prestigePoints = 250_000,
            iconEmoji = "🛥️",
            statusTitle = "Admiralty Commodore"
        ),
        CollectibleConfig(
            id = "vehicle_ocean_liner",
            name = "Private Luxury Ocean Liner",
            category = CollectibleCategory.VEHICLES,
            description = "Customized transoceanic flagship featuring private theaters, gardens, and ballroom.",
            cost = 650_000_000.0,
            prestigePoints = 900_000,
            iconEmoji = "🚢",
            statusTitle = "High Seas Emperor"
        ),

        // Category 4: Rarities
        CollectibleConfig(
            id = "rarity_caesar_coin",
            name = "Ancient Roman Caesar Gold Aureus",
            category = CollectibleCategory.RARITIES,
            description = "Minted in 44 BC under Julius Caesar, authenticated museum grade preservation.",
            cost = 1_800_000.0,
            prestigePoints = 22_000,
            iconEmoji = "🏺",
            statusTitle = "Antiquity Custodian"
        ),
        CollectibleConfig(
            id = "rarity_rex_fossil",
            name = "Complete T-Rex Fossil Skeleton",
            category = CollectibleCategory.RARITIES,
            description = "66-million-year-old apex predator skeleton mounted in private central gallery hall.",
            cost = 35_000_000.0,
            prestigePoints = 50_000,
            iconEmoji = "🦖",
            statusTitle = "Paleo Curator"
        ),
        CollectibleConfig(
            id = "rarity_renaissance_painting",
            name = "Renaissance Masterpiece Canvas",
            category = CollectibleCategory.RARITIES,
            description = "Oil on poplar panel from the Florentine golden age, climate-controlled vault display.",
            cost = 220_000_000.0,
            prestigePoints = 320_000,
            iconEmoji = "🎨",
            statusTitle = "Grand Master Patron"
        ),
        CollectibleConfig(
            id = "rarity_crown_jewels",
            name = "Imperial Monarch Royal Scepter & Crown",
            category = CollectibleCategory.RARITIES,
            description = "Historical sovereign regalia set with 2,800 cut diamonds and uncut royal sapphires.",
            cost = 500_000_000.0,
            prestigePoints = 750_000,
            iconEmoji = "👑",
            statusTitle = "Crown Sovereign"
        )
    )
}
