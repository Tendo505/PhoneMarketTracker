# V2 code reading guide

There are 13 main Java files. Each Activity owns its screen's input, processing
flow and output. Shared helpers contain only shared responsibilities.

## Class structure

| Class | Responsibility |
| --- | --- |
| LoginActivity | Read and validate login input, query accounts, sign in and display errors |
| SignUpActivity | Validate registration, check duplicates and create accounts |
| ProductActivity | Read phones, create cards, handle ADD and display its result |
| CartActivity | Read/display the cart, handle quantities, calculate totals and save checkout |
| ChartActivity | Read today's sales, summarize units/top model, display chart and reset |
| CartManager | Shared cart, item lookup and stock-bounded quantity changes |
| UserSession | Current signed-in user ID |
| SalesCalculator | Six money formulas and nested Totals holder |
| DatabasePMT | Schema, queries, stock validation and transactional writes |
| Phone / CartItem | Phone details and selected quantity |
| DailySalesSummary / PhoneSalesRecord | Plain reporting data holders |

## Responsibility mapping

| Previous responsibility | Current method or class |
| --- | --- |
| Login validation | LoginActivity.validateLogin |
| Registration validation | SignUpActivity.validateInputAndReturnToSignIn |
| Cart operations and unit count | CartManager |
| Signed-in user | UserSession |
| Checkout validation, totals and save | CartActivity.saveSale |
| Today's database reads | ChartActivity.displayDailySales |
| Quantity sum and most-sold model | ChartActivity.summarizeDailySales |
| Close Day and cart reset | ChartActivity.resetDailyTracking |
| Nested reporting holders | DailySalesSummary and PhoneSalesRecord |

## Product selection

displayAvailablePhones reads List<Phone> from DatabasePMT. A loop creates one
card per phone. Each ADD listener passes its Phone to addPhoneToCart.
CartManager.addPhone merges by phone ID and checks stock. Six records produce
six cards, not six hardcoded selection branches. ADD changes only the cart.

## Checkout

CartActivity.confirmSale calls saveSale after confirmation. saveSale checks
UserSession and the cart, obtains SalesCalculator totals and calls
DatabasePMT.saveSale. The database checks live stock and writes the sale,
items and stock changes in one transaction. Only success clears the cart.
Failure rolls back the transaction and keeps the cart.

## Phones to chart

ChartActivity.displayDailySales gets the local date and calls
DatabasePMT.getSalesTotals and getPhoneSales, then summarizeDailySales.
SQLite joins phones, sales and sale_items and groups quantities by phone ID.
The summary loop sums units and selects the highest, keeping the first on a tie.

findViewById(R.id.phoneSalesChart) links Java to activity_chart.xml.
displayPhoneSalesChart creates BarEntry values: x is the index and y is quantity.
The label at the same index identifies the model. IndexAxisValueFormatter sets
labels; setData and invalidate display the bars. Unsold phones are skipped.
Colours affect appearance only.

## Formulas

- SalesCalculator: item cost, item revenue, item profit/loss, cart cost, cart revenue and cart profit/loss.
- CartManager.getTotalQuantity: sum selected quantities.
- ChartActivity.summarizeDailySales: sum sold units and choose the highest.
- DatabasePMT.getSalesTotals / getPhoneSales: SQL SUM for the supplied date.
- DatabasePMT.reducePhoneStock: subtract quantity only when sufficient stock remains.

## Preserved behaviour and verification

XML, IDs, schema, initial phone data and money formulas are unchanged.
Close Day deletes today's sales and clears the cart without restoring stock.
Sign-out clears cart and session. App closure retains completed SQLite records.
Existing chart colour comments and local login-layout edits are preserved.

21 September 2026: debug build and all 10 unit tests passed after this refactor.
The removed general process class has no remaining references in app/src.
Full device regression remains a manual check; see the README demo checklist.
