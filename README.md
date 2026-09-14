# PhoneMarketTracker V2

PhoneMarketTracker is a simple Android Studio Koala application for a small
phone-shop owner. It uses pure Java, editable Android XML layouts, one local
SQLite database, and MPAndroidChart 3.1.0.

The completed V2 implementation is maintained on the `v2` branch. Select that
branch when reviewing this version on GitHub.

## Quick start

1. Open this repository's root folder in Android Studio and select branch `v2`.
2. Install SDK 34, use a Java 17 Gradle JDK, and allow Gradle to sync.
3. Run the `app` configuration on an Android API 26+ emulator or device.
4. Register your own account through Sign Up, then sign in; there is no seeded
   demo account.
5. Complete a sale before opening the chart to demonstrate persisted sales data.

The app works offline. Accounts, inventory and completed sales are local to the
installation; there is no server or cloud synchronisation. The signed-in session
and unfinished cart are held in memory.

## Android project structure

```text
app/src/
├── main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/phonemarkettracker/
│   │   ├── LoginActivity.java / SignUpActivity.java
│   │   ├── ProductActivity.java / CartActivity.java / ChartActivity.java
│   │   ├── Phone.java / CartItem.java / CartManager.java
│   │   ├── SalesCalculator.java
│   │   ├── DatabasePMT.java
│   │   ├── DailySalesSummary.java / PhoneSalesRecord.java
│   │   └── UserSession.java
│   └── res/
│       ├── layout/       # Android screen XML
│       ├── drawable/     # Icons and backgrounds
│       └── values/       # Strings, colours and other values
└── test/java/com/example/phonemarkettracker/  # Local unit tests
```

Application Java files belong in the main Java source folder and use the
`com.example.phonemarkettracker` package. Layouts belong in `app/src/main/res/layout`.
Public Java class names must match their filenames; layout resource filenames
must use lowercase letters, digits and underscores. Activities must be declared
in the manifest and connected to the appropriate layouts and view IDs.

Files left in Downloads, IDE scratch space, or an unconfigured folder are not
automatically included in this app's build. Moving a draft into `src/main` alone
does not fix syntax errors, duplicate classes, missing resources or integration.

## External draft submissions

The six externally supplied drafts reviewed on 13 September 2026 were
`Sales (1).java`, `Sales_Item (1).java`, `scratch_1.java`, `scratch_2.java`,
`p1 (1).xml` and `p2 (1).xml`. They are not included in this checkout's app source.

They attempt two screens, item-price multiplication and sales-table helpers,
but are not a runnable integrated module as supplied:

- Both database drafts declare `DBHandler`; both screen drafts declare
  `MainActivity`. Their public class names do not match the supplied filenames.
- Java syntax errors, undefined variables and invalid Android layout elements
  prevent direct use.
- The helpers specify separate `Sales_DB` and `Sale_itemsDB` databases rather
  than extending the existing `DatabasePMT` schema. Required quantity/user
  fields and relationship handling are incomplete.
- The drafts do not implement the complete checkout, profit/loss, stock-update,
  daily ranking, chart or Close Day workflow.

These observations describe the supplied files, not their author's intent or
effort. The working V2 modules below should be used for the application demo.

## Completed screens

- Sign In and Sign Up with SQLite authentication
- Product Menu loaded from the `phones` table
- Sales Cart with quantity controls and calculated totals
- Sales Overview with a live most-sold bar chart

## V2 sales workflow

1. Sign in or create a local account.
2. Add available phones from Product Menu.
3. Adjust quantities in Sales Cart.
4. Review original cost, selling total, and profit or loss.
5. Complete the sale to store it and reduce phone stock.
6. Open Sales Overview to view today's totals and most-sold phone.
7. Choose **Close Day** to clear today's tracking and the temporary cart.

Checkout asks for confirmation. A successful checkout clears the cart; a failed
checkout leaves it available for correction. Closing the day also asks for
confirmation because it deletes today's completed-sale records.

Closing the day does not delete users, phone details, prices, or current stock.
The cart is held in memory and also resets when the application process closes.

## SQLite tables

- `users`: local authentication accounts
- `phones`: brand, model, cost price, selling price, and stock quantity
- `sales`: user, date, total cost, revenue, and profit or loss
- `sale_items`: phones and quantities included in each completed sale

The six starting phone records are represented in a multidimensional Java array
before they are inserted into SQLite.

All four tables live in `phonemarkettracker.db`, managed by `DatabasePMT`
(schema version 5). Relationships are enforced with foreign keys:

```text
users  1 ── many sales       (sales.user_id)
sales  1 ── many sale_items  (sale_items.sale_id)
phones 1 ── many sale_items  (sale_items.phone_id)
```

`sales` stores the transaction summary; `sale_items` stores its individual
product lines. Unit cost and selling price are copied into each sale item to
preserve the values used at checkout. Deleting a sale cascades to its item rows,
but does not delete products or restore stock.

## Calculations and their code locations

| Calculation | Formula or behaviour | Implementation |
|---|---|---|
| Item cost | Unit cost × quantity | `CartItem.calculateCostTotal()` |
| Item revenue | Unit selling price × quantity | `CartItem.calculateRevenueTotal()` |
| Item gross profit/loss | Item revenue − item cost | `CartItem.calculateProfitLoss()` |
| Cart cost | Sum of item costs | `SalesCalculator.calculateTotalCost()` |
| Cart revenue | Sum of item revenues | `SalesCalculator.calculateTotalRevenue()` |
| Cart gross profit/loss | Cart revenue − cart cost | `SalesCalculator.calculateProfitLoss()` |
| Cart units | Sum of selected quantities | `CartManager.getTotalQuantity()` |
| Remaining stock | Current stock − completed-sale quantity | `DatabasePMT.reducePhoneStock()` |
| Daily financial totals | Sum today's stored transaction costs, revenues and profits/losses | `DatabasePMT.getTodaySalesSummary()` |
| Units sold per product | Sum today's sale-item quantities grouped by phone ID | `DatabasePMT.getTodayPhoneSales()` |
| Daily units and top product | Sum quantities and select the highest quantity | `DatabasePMT.getTodaySalesSummary()` |
| Chart bars | Quantity sold determines bar height; zero-sales products are skipped | `ChartActivity.displayPhoneSalesChart()` |

`DatabasePMT.completeSale()` validates current stock, stores the sale and its
items, and deducts stock inside one database transaction. A failure rolls back
the transaction. Adding to the cart does not deduct persistent stock.

Daily reports cover all users' completed sales for the device's current local
date. Equal top quantities retain the first result in the query's model-name
ordering. Close Day explicitly deletes today's sales and clears the in-memory
cart; simply closing the app does not delete completed sales.

## Prototype limitations

- Passwords are stored as plaintext; this is not production-safe authentication.
- Monetary values use Java `double` and SQLite `REAL`, which can introduce
  floating-point rounding. Integer sen would be an alternative for exact money.
- Profit/loss is gross: rent, salaries, tax and other operating expenses are not
  included in these calculations.
- The unfinished cart is in memory, not persistent database storage.
- Close Day deletes records rather than archiving them. Stock is not restored.
- The upgrade path from versions below 4 recreates the users table and can lose
  existing accounts.

## Build and test

The project targets SDK 34, requires Android API 26 or newer, and uses Java 17.
The commands below run checks; their presence is not a claim that a fresh build
or test run has passed.

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
```

Open the project folder in Android Studio Koala, allow Gradle to sync, choose an
Android emulator, and run the `app` configuration.

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.
Unit-test results are generated under `app/build/test-results/testDebugUnitTest`.
The current local unit tests cover multi-item cost/revenue/profit calculations
and quantity limits. They do not establish that database transactions, login,
screen navigation or daily reset have been tested on a device.

### Manual demo checklist

Verification on 13 September 2026: both local unit tests passed and the debug
APK was generated. The combined check did not fully pass: lint analysis failed
under the machine's Java 25.0.2 runtime. Re-run with the documented Java 17
Gradle JDK before claiming a clean lint result. No device demo was performed
during this documentation update.

- Register and sign in; verify invalid login is rejected.
- Add multiple products; verify quantities cannot exceed stock.
- Check item totals and cart totals against a hand calculation.
- Cancel checkout once and confirm that no sale is saved or stock deducted.
- Complete checkout; verify the cart clears and inventory decreases.
- Check today's quantity, gross profit/loss, top model and chart.
- Restart the app, sign in again, and confirm completed sales still exist.
- Confirm Close Day; verify today's chart/totals clear without restoring stock.

## Code review notes

Calculation comments are short Bahasa Malaysia labels beside the relevant logic.
Use the method names in the calculation table to locate code; adding comments
changes line numbers, so older presentation line references may no longer match.
Keep application code in `src/main`, tests in `src/test`, and external draft
submissions outside the runnable module until they compile and are integrated.
