package com.example.model

data class ForbesBillionaire(
    val rank: Int,
    val name: String,
    val netWorth: Double,
    val company: String,
    val country: String,
    val avatarEmoji: String,
    val isPlayer: Boolean = false
)

object ForbesRichListCatalog {
    // 250 rich list entries with instant 1:1 parody names
    private val coreParodies = listOf(
        Triple("Felon Musk", "X-la Space Dynamics", 248_000_000_000.0),
        Triple("Bernard Amount", "LVMH Luxury Conglomerate", 215_000_000_000.0),
        Triple("Geoff Bezos", "Prime Mega-Delivery Corp", 198_000_000_000.0),
        Triple("Mark Suckerberg", "Meta-Face Social Grid", 178_000_000_000.0),
        Triple("Larry Elephant", "Oracle Cloud Data Towers", 154_000_000_000.0),
        Triple("Warren Bucket", "Berkshire Cash Hoard", 136_000_000_000.0),
        Triple("Gill Bates", "Micro-Soft OS Windows", 128_000_000_000.0),
        Triple("Steve Bawler", "LA Hoops & Office Cloud", 121_000_000_000.0),
        Triple("Jensen Leather Jacket", "N-Vidia AI Silicon GPU", 115_000_000_000.0),
        Triple("Sergey Grin", "Googler Search & Android", 112_000_000_000.0),
        Triple("Larry Sage", "Alphabet Moonshot Labs", 109_000_000_000.0),
        Triple("Michael Broomberg", "Wall Street Data Terminals", 106_000_000_000.0),
        Triple("Carlos Trim", "América Telco Monopoly", 98_000_000_000.0),
        Triple("Moolah Ambani", "Reliance Petro & Jio Fiber", 96_000_000_000.0),
        Triple("Amancio Or-Tiger", "Zara Fast-Fashion Empires", 92_000_000_000.0),
        Triple("Gotham Adani", "Adani Ports & Green Power", 88_000_000_000.0),
        Triple("Michael Shell", "Dell Server Racks & PCs", 85_000_000_000.0),
        Triple("Francoise Bettercourt", "L'Oreal Cosmetics Vault", 82_000_000_000.0),
        Triple("Jim Walton", "Wal-Market Mega Hyperstores", 78_000_000_000.0),
        Triple("Bob Walton", "Wal-Market Global Supply", 77_000_000_000.0),
        Triple("Malice Walton", "Wal-Market Crystal Bridges", 76_000_000_000.0),
        Triple("Song Shanshan", "Nongfu Spring Pure Water", 65_000_000_000.0),
        Triple("Rollin Huang", "PDD Bargain App Syndicate", 62_000_000_000.0),
        Triple("Charles Coke", "Koch Refining & Chemicals", 58_000_000_000.0),
        Triple("Julia Coke", "Koch Industrial Holdings", 58_000_000_000.0),
        Triple("Ken Citadel Griffin", "Citadel Algo Market Makers", 54_000_000_000.0),
        Triple("Dieter Black-Schwarz", "Lidl Supermarket Empire", 49_000_000_000.0),
        Triple("Stephen Blackstone-Man", "Blackstone Private Equity", 46_000_000_000.0),
        Triple("Jacqueline Candy-Mars", "Mars Chocolate & Petcare", 44_000_000_000.0),
        Triple("John Red-Mars", "Mars Snickers & M&M Vault", 44_000_000_000.0),
        Triple("Phil Shoeknight", "Nike Swoosh Global Footwear", 42_000_000_000.0),
        Triple("Miriam Casino-Adelson", "Venetian Sands Casino Resorts", 39_000_000_000.0),
        Triple("Thomas Peterfree", "Interactive Electronic Brokers", 37_000_000_000.0),
        Triple("Len Blaster-nik", "Access Warner Music Group", 35_000_000_000.0),
        Triple("Francois Gucci-Pinault", "Kering Balenciaga & Gucci", 33_000_000_000.0),
        Triple("Tadashi Uniqlo-Yanai", "Fast Retailing Tokyo", 32_000_000_000.0),
        Triple("Eric Swift-Schmidt", "Schmidt Ocean & Alphabet", 31_000_000_000.0),
        Triple("Masayoshi Softbank-Son", "SoftBank Vision Tech Fund", 29_000_000_000.0),
        Triple("Brian Airbnb-Chesky", "Airbnb Global Vacation Rentals", 28_000_000_000.0),
        Triple("Pavel Telegram-Durov", "Telegram Encrypted Cloud", 26_000_000_000.0),
        Triple("Peter PayPal-Thiel", "Palantir & Founders Fund", 25_000_000_000.0),
        Triple("Reid Link-man", "LinkedIn Professional Network", 24_000_000_000.0),
        Triple("Jack Alibaba-Ma", "Alibaba Taobao & Cloud", 23_000_000_000.0),
        Triple("Real Pony-Ma", "Tencent WeChat & Games", 22_500_000_000.0),
        Triple("Richard Virgin-Branson", "Virgin Galactic & Orbit", 21_000_000_000.0),
        Triple("Rupert Fox-Murdoch", "News Corp & Sky Broadcasts", 20_000_000_000.0),
        Triple("Sam OpenAI-Altman", "Synthetic Intelligence Labs", 19_500_000_000.0),
        Triple("Tim Apple-Cook", "Infinite Loop Hardware", 18_800_000_000.0),
        Triple("Satya Cloud-Nadella", "Redmond Server Hyperscale", 17_900_000_000.0),
        Triple("Sundar Search-Pichai", "Mountain View Quantum Lab", 17_200_000_000.0)
    )

    private val countries = listOf("🇺🇸 USA", "🇫🇷 France", "🇨🇳 China", "🇮🇳 India", "🇩🇪 Germany", "🇯🇵 Japan", "🇬🇧 UK", "🇨🇦 Canada", "🇨🇭 Switzerland", "🇦🇺 Australia", "🇸🇬 Singapore")
    private val emojis = listOf("🎩", "💎", "👑", "🚀", "⚡", "🕶️", "💼", "🛥️", "🏰", "🪙", "🔥", "🏎️")

    private val industries = listOf(
        "Quantum Microchips", "Supermarket Empire", "Private Aerospace", "Deep Ocean Drilling",
        "Autonomous Robotics", "Pharmaceutical Labs", "Renewable Energy Grid", "Hedge Fund Capital",
        "Commercial Aviation", "SaaS Infrastructure", "Luxury Diamond Mines", "Global Shipping Fleet",
        "Cybersecurity Defense", "Satellite Internet", "Venture Incubators", "Biotech Longevity"
    )

    private val parodySurnames = listOf(
        "Vander-Cash", "Rocke-Feller", "Gold-Sachs", "Roth-Shield", "Carnegie-Steel", "Astor-Plaza",
        "Morgan-Chase", "Sterling-Silver", "Prestige-King", "Coin-Hoarder", "Penny-Pincher", "Billion-Pound",
        "Vault-Keeper", "Cash-Cannon", "Diamond-Hands", "Dividend-Baron", "Yield-Farmer", "Crypto-Whale"
    )

    private val firstNames = listOf(
        "Alexander", "Maximilian", "Victoria", "Cornelius", "Sebastian", "Eleanor", "Archibald",
        "Genevieve", "Theodore", "Bartholomew", "Cassandra", "Montgomery", "Winifred", "Reginald",
        "Penelope", "Sterling", "Augustus", "Beatrice", "Dominic", "Anastasia", "Harrison"
    )

    val billionaires: List<ForbesBillionaire> by lazy {
        val list = mutableListOf<ForbesBillionaire>()

        // 1. Add top curated recognizable parodies
        coreParodies.forEachIndexed { index, (name, company, worth) ->
            list.add(
                ForbesBillionaire(
                    rank = index + 1,
                    name = name,
                    netWorth = worth,
                    company = company,
                    country = countries[index % countries.size],
                    avatarEmoji = emojis[index % emojis.size]
                )
            )
        }

        // 2. Procedurally generate descending down to rank 250
        var currentWorth = 16_500_000_000.0
        val startIndex = coreParodies.size
        for (i in startIndex until 250) {
            val rank = i + 1
            // Smooth natural decay curve from 16.5B down to 3.5B
            currentWorth *= 0.9922
            val fn = firstNames[i % firstNames.size]
            val sn = parodySurnames[i % parodySurnames.size]
            val ind = industries[i % industries.size]
            val comp = "$sn $ind Group"

            list.add(
                ForbesBillionaire(
                    rank = rank,
                    name = "$fn $sn",
                    netWorth = currentWorth,
                    company = comp,
                    country = countries[(i * 3) % countries.size],
                    avatarEmoji = emojis[i % emojis.size]
                )
            )
        }

        list
    }

    // Minimum net worth to qualify for the Forbes 250
    val qualificationThreshold: Double
        get() = billionaires.last().netWorth // Rank 250's net worth (~3.5B)
}
