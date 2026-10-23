package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.*
import com.example.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
  fun `formatPerTap formats exact numbers cleanly per tap`() {
    assertEquals("$25 per tap", Formatters.formatPerTap(25.0))
    assertEquals("$18 per tap", Formatters.formatPerTap(18.0))
    assertEquals("$1.50 per tap", Formatters.formatPerTap(1.50))
    assertEquals("$0.50 per tap", Formatters.formatPerTap(0.50))
    assertEquals("$1,800 per tap", Formatters.formatPerTap(1800.0))
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
  fun `forbes 250 catalog contains exactly 250 billionaires with parody names`() {
    val list = ForbesRichListCatalog.billionaires
    assertEquals(250, list.size)
    assertEquals(1, list.first().rank)
    assertEquals(250, list.last().rank)

    // Check specific parody names
    val topNames = list.take(15).map { it.name }
    assertTrue(topNames.contains("Felon Musk"))
    assertTrue(topNames.contains("Bernard Amount"))
    assertTrue(topNames.contains("Geoff Bezos"))
    assertTrue(topNames.contains("Mark Suckerberg"))
    assertTrue(topNames.contains("Larry Elephant"))
    assertTrue(topNames.contains("Warren Bucket"))
    assertTrue(topNames.contains("Gill Bates"))
    assertTrue(topNames.contains("Jensen Leather Jacket"))

    // Verify descending order
    for (i in 0 until list.size - 1) {
      assertTrue(list[i].netWorth >= list[i + 1].netWorth)
    }
  }

  @Test
  fun `ultimate collectibles catalog covers all four mandatory categories`() {
    val configs = UltimateCollectiblesCatalog.allConfigs
    val categories = configs.map { it.category }.toSet()
    assertTrue(categories.contains(CollectibleCategory.MANSIONS))
    assertTrue(categories.contains(CollectibleCategory.PRECIOUS_ASSETS))
    assertTrue(categories.contains(CollectibleCategory.VEHICLES))
    assertTrue(categories.contains(CollectibleCategory.RARITIES))

    val mansion = configs.find { it.id == "mansion_beverly_hills" }
    assertNotNull(mansion)
    assertEquals(45_000_000.0, mansion!!.cost, 0.01)
  }

  @Test
  fun `tax status state computes 2 point 5 percent tax correctly`() {
    val state = TaxStatusState(
      rollingWindowSeconds = 900,
      accumulatedTaxableIncome = 100_000.0,
      isWarningActive = true
    )
    assertEquals(2500.0, state.currentTaxDue, 0.01)
  }
}
