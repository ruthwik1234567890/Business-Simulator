package com.example.model

data class RealEstateConfig(
    val id: String,
    val name: String,
    val description: String,
    val cost: Double,
    val hourlyRent: Double,
    val iconName: String
)

data class RealEstateState(
    val config: RealEstateConfig,
    val ownedCount: Int = 0
) {
    val totalHourlyRent: Double
        get() = config.hourlyRent * ownedCount

    val totalValuation: Double
        get() = config.cost * ownedCount
}

object RealEstateCatalog {
    val SUBURBAN_HOUSE = RealEstateConfig(
        id = "suburban_house",
        name = "Suburban Residential Villa",
        description = "Turnkey single-family residential rental home in prime metro district.",
        cost = 50_000.0,
        hourlyRent = 200.0,
        iconName = "home"
    )

    val COMMERCIAL_PLAZA = RealEstateConfig(
        id = "commercial_plaza",
        name = "Metropolitan Commercial Plaza",
        description = "Prime retail strip with triple-net corporate leases.",
        cost = 500_000.0,
        hourlyRent = 2_500.0,
        iconName = "storefront"
    )

    val LUXURY_SKYSCRAPER = RealEstateConfig(
        id = "luxury_skyscraper",
        name = "Manhattan Luxury Skyscraper",
        description = "Iconic 60-story high-rise with penthouse suites and Grade-A office leases.",
        cost = 5_000_000.0,
        hourlyRent = 30_000.0,
        iconName = "apartment"
    )

    val allConfigs = listOf(SUBURBAN_HOUSE, COMMERCIAL_PLAZA, LUXURY_SKYSCRAPER)
}
