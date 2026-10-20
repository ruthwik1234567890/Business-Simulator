package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ClickBoosterCatalog
import com.example.model.EnterpriseCatalog
import com.example.model.PlayerRank
import com.example.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Wall Street Tycoon", appName)
  }

  @Test
  fun `currency formatter formats numbers correctly`() {
    assertEquals("$10,000", Formatters.formatCurrency(10_000.0))
    assertEquals("$1.25M", Formatters.formatCurrency(1_250_000.0))
    assertEquals("$500K", Formatters.formatCurrency(500_000.0))
    assertEquals("$12,345", Formatters.formatCurrency(12_345.0))
  }

  @Test
  fun `enterprise cost scaling operates correctly`() {
    val coffee = EnterpriseCatalog.COFFEE_SHOP
    assertEquals(1000.0, coffee.getUpgradeCost(0), 0.01)
    assertEquals(1500.0, coffee.getUpgradeCost(1), 0.01)
    assertEquals(50.0, coffee.getHourlyIncome(1), 0.01)
    assertEquals(100.0, coffee.getHourlyIncome(2), 0.01)
  }

  @Test
  fun `player rank progression scales with prestige and net worth`() {
    val starterRank = PlayerRank.calculateRank(0, 10_000.0)
    assertEquals("Apprentice Hustler", starterRank.title)

    val topRank = PlayerRank.calculateRank(160_000, 60_000_000.0)
    assertEquals("Global Oligarch", topRank.title)
  }

  @Test
  fun `milestone multipliers boost income exponentially`() {
    val coffee = EnterpriseCatalog.COFFEE_SHOP
    assertEquals(2.0, coffee.getMilestoneMultiplier(10), 0.01)
    assertEquals(4.0, coffee.getMilestoneMultiplier(25), 0.01)
  }

  @Test
  fun `bulk buy calculates multiple levels correctly`() {
    val coffee = EnterpriseCatalog.COFFEE_SHOP
    val singleCost = coffee.getUpgradeCost(0)
    val bulkCost = coffee.getCostForLevels(0, 3)
    assertTrue(bulkCost > singleCost)
  }

  @Test
  fun `click booster catalog starts at 50 cents and scales properly`() {
    val tier1 = ClickBoosterCatalog.getBoosterForTier(1)
    assertEquals(0.50, tier1.perClickAmount, 0.001)
    assertEquals(0.0, tier1.cost, 0.001)

    val tier2 = ClickBoosterCatalog.getBoosterForTier(2)
    assertEquals(1.50, tier2.perClickAmount, 0.001)
    assertEquals(25.0, tier2.cost, 0.001)

    val next = ClickBoosterCatalog.getNextBooster(1)
    assertEquals(2, next?.tier)
  }
}
