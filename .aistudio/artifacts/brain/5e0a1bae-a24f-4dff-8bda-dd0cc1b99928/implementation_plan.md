# Money Tycoon - Major Expansion & "My Space" Suite

A feature update and UX architecture expansion introducing a reliable offline/idle earnings system, replacing "Rewards" with a dedicated full-screen "My Space" destination, and adding the Tax Office with forced asset liquidation, the Forbes 250 Rich List with dynamic player induction, the Ultimate Collectibles Marketplace, and the high-stakes Tax Auditor dilemma.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following parameters were confirmed and synthesized directly from Phase 1 clarifications:
> - **Forbes 250 Player Induction**: When the player's total net worth exceeds the 250th richest billionaire, the app prompts for the Player's Tycoon Name (on first qualification) and triggers an animated grand celebration ceremony showcasing their world ranking.
> - **Tax Auditor Risk Tuning**: Balanced risk profile featuring a 50% chance of clearing the offshore audit (+50% income bonus boost for 5 minutes) vs. a 50% chance of failing (20% penalty fine). Clean disclosure yields a safe, guaranteed +5% stability bonus.
> - **Tax Alert UX**: A sticky alert banner appears at minute 15 with a real-time 5-minute countdown and a one-tap "Pay Tax" action directly on the main dashboard and inside My Space.

---

## 1. Overview & Core Concept

Money Tycoon evolves from a single-dashboard clicker into a financial simulation with persistent idle mechanics and executive status progression:
- **Offline & Idle Continuity**: Players earn passive revenue from businesses, rental real estate, and banking yields while away, captured cleanly via Android `SharedPreferences` timestamps and presented via a "Welcome Back" collection popup upon resume.
- **"My Space" Private Office**: Replacing the old "Rewards" tab with a full-screen executive suite housing the player's high-roller interests.
- **IRS Tax Office & Liquidation Cascade**: Rolling 15-minute taxable income evaluation on realized stock profits, rental checks, and business profits. Unpaid taxes past the 20-minute mark initiate a liquidation cascade starting from the lowest-value assets (Crypto → Businesses → Real Estate → Luxury Collectibles).
- **Forbes 250 Rich List**: A living leaderboard populated with 250 parody billionaires (e.g., Felon Musk, Gill Bates, Geoff Bezos, Mark Suckerberg, Warren Bucket, Bernard Amount, Larry Elephant, Jensen Leather Jacket) where the player competes to climb from outsider to the #1 spot.
- **Ultimate Collectibles Marketplace**: Categorized luxury acquisition across Mansions (personal estates), Precious Assets (gold bullion, raw diamonds, rubies), Exotic Vehicles (supercars, megayachts, private jets), and Historic Rarities (fine art, artifacts).
- **Tax Auditor Events**: Randomized high-stakes audits presenting the classic tycoon dilemma: transparent declaration vs. offshore asset shielding.

---

## 2. User Experience & Visual Design

### Visual Identity & Theme
- **Background**: Deep obsidian dark `#0A0E1A` (`SoftDarkBg`) with elevated container surfaces `#121829` (`SoftCardBg`) and hairline borders `#1E293B`.
- **Primary Accent**: Vibrant 24K Gold `#FFC700` (`BrightGold`) and Champagne `#FDE047` for wealth counters, rankings, and prestige trophies.
- **Status Badges**: Emerald Green `#00E676` (`VibrantGreen`) for cash yields and positive returns; Crimson Warning `#EF4444` for tax notices and liquidation timers.
- **Typography Pairings**: JetBrains Mono / Roboto Mono for numeric counters and financial percentages; Nunito / Quicksand for readable body text and titles.

### Key User Flows

```
┌────────────────────────────────────────────────────────────────────────┐
│ App Launch / Resume                                                    │
│ └─► Check SharedPreferences 'last_active_timestamp'                    │
│      └─► Show "Welcome Back" Popup (+Total Cash Earned)                │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ Main Tycoon Dashboard                                                  │
│ ├─► Sticky Tax Alert Banner (Active during 15-20 min window)           │
│ └─► Bottom Navigation: [Businesses] [Stocks] [Tap Cash] [Houses]       │
│                                                   [My Space] ◄── NEW   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ (Taps "My Space")
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ Full-Screen "My Space" Executive Suite (with Back Button)              │
│ ├─► Header: Net Worth, Forbes Rank (#250 / Unranked), Tax Status       │
│ ├─► Hub Navigation Cards:                                              │
│ │   ├── 1. Tax Office & Liquidation Status                             │
│ │   ├── 2. Forbes 250 Rich List & Celebration Podium                   │
│ │   ├── 3. Ultimate Collectibles Marketplace (Mansions/Vehicles/Gems)  │
│ │   └── 4. Tax Auditor Hotline & Compliance Records                    │
│ └─► Back Button returns seamlessly to Dashboard                        │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Key Product Decisions & Trade-Offs

- **SharedPreferences for Idle Timing**:
  - *Chosen Approach*: Record `last_active_timestamp` on `onPause`/`onStop` and evaluate upon `onResume`.
  - *Why*: Immune to in-memory state teardown, lightweight, zero async initialization lag on process startup.
- **Full-Screen Destination for "My Space"**:
  - *Chosen Approach*: Rendered as a full-screen Compose layout overlay or dedicated screen with top App Bar and Android system back handling (`BackHandler`).
  - *Why*: Accommodates the deep sub-screens (Forbes 250 with 250 entries, Collectibles multi-category gallery, and Tax Office logs) without crowding bottom bar sheets.
- **Cascade Liquidation Algorithm**:
  - *Chosen Approach*: Calculates outstanding debt, sorts owned assets ascending by market valuation within each tier (Crypto first, then Enterprise levels, then Real Estate units, then Luxury Collectibles), selling units until the tax bill is zeroed. Excess liquidation cash is deposited into "My Pocket Cash".
  - *Why*: Strictly complies with user specifications while giving players transparency into what is at risk if they ignore the countdown.

---

## 4. Technical Architecture & Data Strategy

### Component Hierarchy

```
┌─────────────────────────────────────────────────────────────────┐
│ MainActivity (Lifecycle: onPause/onResume SharedPreferences)    │
│  └── MainTycoonScreen                                           │
│       ├── TopDashboardBar & NewsTickerBar                       │
│       ├── StickyTaxWarningBanner (15m-20m countdown + Pay CTA)  │
│       ├── Active Tab Content (Businesses / Stocks / Tap / Houses│
│       ├── BottomNavigationBar (Businesses, Stocks, Tap, Houses, │
│       │                        My Space)                        │
│       ├── OfflineWelcomeDialog                                  │
│       └── TaxAuditorDialog                                      │
│                                                                 │
│  (When My Space Active: Full Screen View with BackHandler)       │
│  └── MySpaceScreen                                              │
│       ├── MySpaceTopBar (Back to Dashboard, Player Identity)    │
│       ├── ForbesRichListSection (250 Parody Billionaires +       │
│       │                          Player Rank + Celebration)     │
│       ├── TaxOfficeSection (Tax Due, Audit Log, Liquidation Map)│
│       └── CollectiblesMarketSection (Mansions, Bullion, Jets)   │
└─────────────────────────────────────────────────────────────────┘
```

### Data Model & State Additions
1. **SharedPreferences**:
   - `last_active_timestamp` (Long): System time millis on app pause.
2. **Tax Management State**:
   - `rollingTaxCycleSeconds` (Int): Counts up to 900s (15 min warning) and 1200s (20 min penalty).
   - `taxableIncomeAccumulated` (Double): Realized stock profits, rental rents, and business income earned during the active 15m window.
   - `isTaxWarningActive` (Boolean): True between 15:00 and 20:00.
   - `taxTimeRemainingSeconds` (Int): Countdown from 300 down to 0.
3. **Forbes 250 Parody Roster**:
   - Pre-computed 250-item list with recognizable parody names (Felon Musk, Gill Bates, Geoff Bezos, Mark Suckerberg, Warren Bucket, Bernard Amount, Larry Elephant, Jensen Leather Jacket, etc.), avatars, industry tags, and net worth descending from $260 Billion down to ~$3.5 Billion.
   - Player dynamic rank calculation and one-time induction celebration dialog asking for the player's moniker.
4. **Ultimate Collectibles Catalog**:
   - 4 Categories: `MANSION` (Beverly Hills Estate, Alpine Chateau, Penthouse, Island), `PRECIOUS_ASSET` (Gold Bars, Silver Bullion, Raw Diamonds, Imperial Rubies), `VEHICLE` (V12 Supercar, Monaco Megayacht, Ocean Liner, Supersonic Jetliner), `RARITY` (Ancient Roman Coin, Renaissance Masterpiece, Crown Jewels).
5. **Tax Auditor Event**:
   - Random trigger every 8-12 minutes or via Tax Office.
   - 2 choices: "Disclose Cleanly" (+5% boost 3m) or "Risk Offshore Audit" (50% chance +50% boost 5m vs 50% chance 20% fine).

---

## 5. Execution Steps
1. **Preferences & Lifecycle**: Wire `SharedPreferences` in `MainActivity` / `TycoonViewModel` for reliable offline calculation.
2. **Tax Engine & Cascade Liquidation**: Implement rolling 15m timer, 2.5% tax calculation, 5m warning banner, and automatic liquidation cascade.
3. **Navigation & My Space Screen**: Update navigation bar replacing "Rewards" with "My Space", creating full-screen `MySpaceScreen` with top bar and back navigation.
4. **Forbes 250 & Player Induction**: Implement the 250-rich list with name prompt and induction celebration.
5. **Collectibles Marketplace**: Implement multi-category personal status items.
6. **Tax Auditor Event & Verification**: Add auditor popup event, test compilation, and verify all UI components.
