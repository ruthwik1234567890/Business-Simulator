package com.example.model

data class TaxStatusState(
    val rollingWindowSeconds: Int = 0,             // Counts 0 to 1200 seconds (20 mins)
    val accumulatedTaxableIncome: Double = 0.0,    // Realized profits, rental rents, business revenues in this window
    val taxRatePercent: Double = 2.5,              // Standard 2.5% tax
    val isWarningActive: Boolean = false,          // True between 15m (900s) and 20m (1200s)
    val warningRemainingSeconds: Int = 300,        // 5-minute countdown (300 down to 0)
    val lastPaidTaxAmount: Double = 0.0,
    val totalTaxesPaidAllTime: Double = 0.0,
    val lastLiquidationSummary: LiquidationSummary? = null
) {
    val currentTaxDue: Double
        get() = accumulatedTaxableIncome * (taxRatePercent / 100.0)
}

data class LiquidationSummary(
    val timestamp: Long,
    val totalDebtDemanded: Double,
    val liquidatedAssetsNames: List<String>,
    val totalValueLiquidated: Double,
    val refundPaidToPocket: Double,
    val revokedIncomeAmount: Double
)
