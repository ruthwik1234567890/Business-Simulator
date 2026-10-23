package com.example.util

import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.abs

object Formatters {
    private val exactCurrencyFormat = DecimalFormat("$#,##0.00")
    private val integerFormat = DecimalFormat("$#,##0")

    fun formatExactCurrency(amount: Double): String {
        return exactCurrencyFormat.format(amount)
    }

    fun formatPerTap(amount: Double): String {
        val formatted = when {
            amount >= 1_000_000.0 -> formatCurrency(amount)
            amount >= 1_000.0 -> integerFormat.format(amount)
            amount % 1.0 == 0.0 -> "$${amount.toInt()}"
            else -> exactCurrencyFormat.format(amount)
        }
        return "$formatted per tap"
    }

    fun formatCurrency(amount: Double): String {
        val absVal = abs(amount)
        val prefix = if (amount < 0) "-$" else "$"
        return when {
            absVal >= 1_000_000_000_000.0 -> {
                val valT = absVal / 1_000_000_000_000.0
                if (valT % 1.0 == 0.0) String.format(Locale.US, "%s%.0fT", prefix, valT)
                else String.format(Locale.US, "%s%.2fT", prefix, valT)
            }
            absVal >= 1_000_000_000.0 -> {
                val valB = absVal / 1_000_000_000.0
                if (valB % 1.0 == 0.0) String.format(Locale.US, "%s%.0fB", prefix, valB)
                else String.format(Locale.US, "%s%.2fB", prefix, valB)
            }
            absVal >= 1_000_000.0 -> {
                val valM = absVal / 1_000_000.0
                if (valM % 1.0 == 0.0) String.format(Locale.US, "%s%.0fM", prefix, valM)
                else String.format(Locale.US, "%s%.2fM", prefix, valM)
            }
            absVal >= 100_000.0 -> {
                val valK = absVal / 1_000.0
                if (valK % 1.0 == 0.0) String.format(Locale.US, "%s%.0fK", prefix, valK)
                else String.format(Locale.US, "%s%.1fK", prefix, valK)
            }
            absVal >= 1_000.0 -> integerFormat.format(amount)
            else -> exactCurrencyFormat.format(amount)
        }
    }

    fun formatNumber(amount: Double): String {
        val absVal = abs(amount)
        return when {
            absVal >= 1_000_000_000.0 -> String.format(Locale.US, "%.2fB", absVal / 1_000_000_000.0)
            absVal >= 1_000_000.0 -> String.format(Locale.US, "%.2fM", absVal / 1_000_000.0)
            absVal >= 1_000.0 -> String.format(Locale.US, "%.1fK", absVal / 1_000.0)
            else -> String.format(Locale.US, "%.2f", amount)
        }
    }

    fun formatPercent(percent: Double): String {
        val sign = if (percent > 0) "+" else ""
        return String.format(Locale.US, "%s%.2f%%", sign, percent)
    }
}
