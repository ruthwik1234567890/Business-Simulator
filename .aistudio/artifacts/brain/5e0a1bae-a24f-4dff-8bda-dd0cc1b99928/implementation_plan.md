# Money Tycoon - Major Expansion & "My Space" Suite

A feature update and UX architecture expansion introducing a reliable offline/idle earnings system, replacing "Rewards" with a dedicated full-screen "My Space" destination, adding the Tax Office with forced asset liquidation, the Forbes 250 Rich List with dynamic player induction, the Ultimate Collectibles Marketplace, streamlined multiplier upgrade indicators, and a universal "Total Money" cockpit indicator redirecting directly to spendable pocket cash.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> - **Universal Total Money Cockpit**: In all pages, the former "Total Wealth" card is replaced with "Total Money". It exclusively displays the player's spendable pocket cash (`uiState.cash`) and clicking it immediately redirects to the "Tap Cash" page.
> - **Streamlined Tap Multiplier**: A single thin row displaying `{current money}/{money needed to access new multiplier}` and `{multiplier you get if you spend the required amount}` with one-tap unlock.
> - **Forbes 250 Player Induction**: When the player's total net worth exceeds the 250th richest billionaire, the app prompts for the Player's Tycoon Name (on first qualification) and triggers an animated grand celebration ceremony showcasing their world ranking.
> - **Tax Alert UX**: A sticky alert banner appears at minute 15 with a real-time 5-minute countdown and a one-tap "Pay Tax" action directly on the main dashboard and inside My Space.
> - **Tax Auditor**: Fully eradicated per user instruction.

---

## 1. Overview & Core Concept

Money Tycoon evolves from a single-dashboard clicker into a financial simulation with persistent idle mechanics and executive status progression:
- **Offline & Idle Continuity**: Players earn passive revenue from businesses, rental real estate, and banking yields while away, captured cleanly via Android `SharedPreferences` timestamps and presented via a "Welcome Back" collection popup upon resume.
- **"My Space" Private Office**: Replacing the old "Rewards" tab with a full-screen executive suite housing the player's high-roller interests.
- **IRS Tax Office & Liquidation Cascade**: Rolling 15-minute taxable income evaluation on realized stock profits, rental checks, and business profits. Unpaid taxes past the 20-minute mark initiate a liquidation cascade starting from the lowest-value assets (Crypto → Businesses → Real Estate → Luxury Collectibles).
- **Forbes 250 Rich List**: A living leaderboard populated with 250 parody billionaires (e.g., Felon Musk, Gill Bates, Geoff Bezos, Mark Suckerberg, Warren Bucket, Bernard Amount, Larry Elephant, Jensen Leather Jacket) where the player competes to climb from outsider to the #1 spot.
- **Ultimate Collectibles Marketplace**: Categorized luxury acquisition across Mansions (personal estates), Precious Assets (gold bullion, raw diamonds, rubies), Exotic Vehicles (supercars, megayachts, private jets), and Historic Rarities (fine art, artifacts).
- **Universal Total Money Header**: Quick-tap navigation card in the cockpit bar displaying current pocket cash that redirects to the primary cash generation screen.

---

## 2. Technical Architecture & Changes

1. **Top Dashboard Bar (`TopDashboardBar.kt`)**:
   - Replaced "TOTAL WEALTH" card with "TOTAL MONEY" card.
   - Formatted to display `uiState.cash` (pocket money).
   - Card includes a forward arrow affordance and an `onNavigateToPocket()` click handler.
   - Removed old net-worth breakdown accordion.
2. **Main Navigation (`MainTycoonScreen.kt`)**:
   - Wired `onNavigateToPocket` to invoke `viewModel.selectTab(TycoonTab.EARNINGS)`, immediately jumping to the active cash generation screen.
3. **Dialogs & Sub-screens (`AscensionDialog.kt`, `EarningsScreen.kt`)**:
   - Replaced "Total Wealth" requirement strings with "Total Money".
   - Streamlined Tap Cash multiplier row to show `Money: {cash} / {cost}`.
