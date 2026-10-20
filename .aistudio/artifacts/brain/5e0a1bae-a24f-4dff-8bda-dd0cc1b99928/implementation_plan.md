# Wall Street Tycoon: Business & Financial Empire

A modern, high-fidelity Business & Entrepreneurship Tycoon game built with Kotlin and Jetpack Compose. Players start with $10,000 cash, scaling their capital across commercial enterprises, dynamic financial markets (stocks and crypto), income-generating real estate portfolios, and luxury prestige assets, wrapped in a specialized Bloomberg Terminal slate-and-amber aesthetic with full offline earnings calculation.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following directions were confirmed during initial planning and will guide implementation:
>
> - **Visual Aesthetic**: Wall Street Bloomberg Terminal aesthetic with deep slate/charcoal surfaces (`#0D1117`, `#161B22`), rich amber/gold accents (`#FFB800`, `#F59E0B`), electric terminal green (`#10B981`) for gains, crimson (`#EF4444`) for losses, and high-density financial metrics.
> - **Economic Events**: Dynamic breaking news ticker and economic events (e.g., "Tech Sector Bull Run", "Regulatory Crackdown", "Interest Rate Cuts") affecting specific stocks, crypto, and enterprise yields in real time.
> - **Offline Idle Income**: Automatic delta calculation based on `System.currentTimeMillis()` recorded at app exit vs app launch, presenting a terminal "Welcome Back / Overnight Yield Report" modal summarizing offline gains.

---

### 1. Overview & Core Concept

- **What It Does**: Simulates an entrepreneurial and financial investment career. Players manage cash flow, allocate capital across four distinct asset classes (Enterprises, Financial Markets, Real Estate, and Luxury/Status), and react to live ticker movements and market headlines to maximize Net Worth.
- **Target Audience / Persona**: Mobile strategy and simulation fans, finance/business enthusiasts who enjoy satisfying incremental progression, portfolio management, and terminal-style market trading.
- **Key Value**: Combines incremental clicker satisfaction with realistic portfolio mathematics, real-time market trading mechanics, and offline progress persistence.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **First Launch & Initial Capital**:
   - Player starts with $10,000 in cash.
   - Top Header displays Cash Balance, Total Net Worth, and Hourly Passive Cash Flow, accompanied by a scrolling/banner Breaking News Ticker.
2. **Tab 1: Enterprises**:
   - Browse starting ventures (Coffee Shop, Taxi Fleet, Software Studio) and the pinnacle Commercial Bank ($1M unlock).
   - Tap "Acquire" or "Upgrade Level" with instant dynamic recalculation of income and 1.5x/1.6x exponential cost scaling.
   - Commercial Bank earns compound interest yields on liquid player cash deposits.
3. **Tab 2: Financial Markets (Stocks & Crypto)**:
   - High-density ticker view: Stocks (TechCorp, EnergyX, RetailPlus) update with stable growth curves; Crypto (BitCoin, EtherX, MemeCoin) fluctuates sharply every 3 seconds.
   - Interactive Trading Dialog / Sheet: Buy & Sell controls with quick quantity buttons (1, 10, 100, Max) and current portfolio average cost basis.
   - Price trend badges with green/red sparkline indicators.
4. **Tab 3: Real Estate Portfolio**:
   - Suburban House ($50K / $200/hr), Commercial Plaza ($500K / $2,500/hr), and Luxury Skyscraper ($5M / $30,000/hr).
   - Purchase individual deeds to stack stable, recession-resistant rental cash flows and appreciate total net worth.
5. **Tab 4: Luxury & Lifestyle**:
   - Status symbols (Sports Car $80K, Private Jet $2M, Superyacht $15M) that elevate player Social Rank from "Aspiring Hustler" to "Global Oligarch".
6. **Return to App (Offline Progress)**:
   - Compares current time with `lastSavedTimestamp`.
   - Displays a "Market Close & Overnight Earnings" report sheet detailing offline income accrued while away, credited immediately to the player balance.

#### Visual Identity & Theme
- **Color Palette**:
  - `Background`: Deep Charcoal Terminal `#0D1117`
  - `Surface / Cards`: Slate Carbon `#161B22` with border `#30363D`
  - `Primary / Terminal Amber`: Vibrant Golden Amber `#FFB800` / `#D97706`
  - `Terminal Text`: Monospace / crisp metrics `#F3F4F6` with muted labels `#9CA3AF`
  - `Bullish (Up)`: Matrix Green `#10B981`
  - `Bearish (Down)`: Bloomberg Crimson `#EF4444`
- **Typography & Formatting**: Clean tabular figures for values; currency formatted with standard abbreviations (`$12,450`, `$245.5K`, `$1.85M`, `$3.40B`).
- **Interactive Feedback**: Haptic-inspired subtle flashes on price ticker shifts, animated count-ups for large cash claims, and smooth progress bars on upgrade thresholds.

---

### 3. Key Product Decisions & Trade-Offs

#### Decision 1: Game Loop Engine & Market Ticker
- **Approach**: Dedicated Kotlin coroutine loop inside `GameViewModel` running two synchronized cadence intervals:
  - 1-second interval: Generates passive income ticks (`hourlyIncome / 3600`) and adds to cash balance.
  - 3-second interval: Updates financial market prices with Brownian motion volatility (higher sigma for crypto, lower for blue-chip stocks) and evaluates random economic news events.
- **Trade-Off**: Coroutine-based timers in ViewModel ensure no UI thread stalling, and automatically pause during lifecycle backgrounding while preserving timestamp state.

#### Decision 2: State Persistence with Room
- **Approach**: Room SQLite database with a single-row `GameStateEntity` (cash, timestamp, stats) paired with relational or JSON-serialized lists of active holdings (`HoldingEntity` for enterprises, stocks/crypto units, real estate counts, and luxury unlocks).
- **Trade-Off**: Full Room integration provides crash resistance, ACID reliability, and automatic background saving every 5 seconds and on pause, perfectly enabling offline yield calculations.

#### Decision 3: Offline Progress Architecture
- **Approach**: Formula: `elapsedSeconds = (currentTime - lastSavedTimestamp) / 1000`. Capped at a realistic tycoon window (e.g., max 24 hours of idle time). Total offline gain = `hourlyIncome * (elapsedSeconds / 3600)`.
- **Trade-Off**: Provides an engaging reward loop without allowing infinite inflation from year-long system clock jumps.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                      Jetpack Compose UI                     │
├─────────────────────────────────────────────────────────────┤
│  Top Dashboard Bar: Cash, Net Worth, Hourly Passive Yield   │
│  News Ticker Bar: Live Macro Headlines & Market Sentiment   │
├───────────────────┬───────────────────────┬─────────────────┤
│ Tab 1: Enterprises│ Tab 2: Financial Mkts │ Tab 3: Real     │
│ - Coffee Shop     │ - Blue Chip Stocks    │   Estate Deeds  │
│ - Taxi Fleet      │ - Crypto Assets       │ Tab 4: Luxury   │
│ - Software Studio │ - Buy/Sell Sheet      │   Prestige Rank │
│ - Commercial Bank │ - Price Sparklines    │                 │
└───────────────────┴───────────────────────┴─────────────────┘
                               ▲
                               │ StateFlow<GameUiState>
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    TycoonGameViewModel                      │
│ - 1s Coroutine Income Tick                                  │
│ - 3s Market Price Volatility & Random Event Engine          │
│ - Purchase / Upgrade / Trade Business Rules                 │
│ - Offline Progress Computation & Dialog State               │
└─────────────────────────────────────────────────────────────┘
                               ▲
                               │ Flow / Suspend
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   Room Database Layer                       │
│ - TycoonDatabase (KSP compiled)                             │
│ - GameStateDao (Save/Load Cash, LastActiveTimestamp)        │
│ - HoldingsDao (Enterprises, Stock Shares, Real Estate, Lux) │
└─────────────────────────────────────────────────────────────┘
```

#### Core Entities & State
1. **`GameStateEntity`**:
   - `id: Int = 1`
   - `cash: Double = 10000.0`
   - `lastActiveTimestamp: Long`
   - `totalEarnedAllTime: Double`
   - `bankDepositedCash: Double`
2. **`HoldingEntity`**:
   - `assetId: String` (e.g., `coffee_shop`, `tech_corp`, `suburban_house`, `sports_car`)
   - `category: String` (`ENTERPRISE`, `STOCK`, `CRYPTO`, `REAL_ESTATE`, `LUXURY`)
   - `levelOrCount: Int`
   - `averageCostBasis: Double`
3. **`MarketAsset` (Runtime Dynamic State)**:
   - `id`, `name`, `symbol`, `currentPrice`, `previousPrice`, `history: List<Double>`, `volatility`, `isCrypto`
4. **`EconomicEvent`**:
   - `headline: String`, `affectedAssetId: String?`, `multiplier: Double`, `durationSeconds: Int`

#### Interactive Component & State Mapping
- **Enterprises Tab**: Displays level badges, current $/hr generated, and upgrade cost. Disables button if `cash < nextUpgradeCost`. Commercial Bank displays liquid deposit/withdraw controls with 5% APY yield.
- **Market Tab**: Shows live ticker cards with pulsing green/red indicators. Tapping an asset opens a bottom sheet with a chart sparkline, current shares owned, average cost basis, profit/loss tracker, and quantity selectors.
- **Real Estate Tab**: Displays properties with rental yield rates and total owned counters.
- **Luxury Tab**: Shows prestigious collectibles that award Prestige points, unlocking executive titles ("Wall Street Raider", "Titan of Industry").
- **Welcome Back Modal**: Shows total elapsed time, calculated offline yield, and a "Collect Earnings" action button.
