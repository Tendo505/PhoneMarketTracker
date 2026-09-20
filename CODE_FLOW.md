# V2 code reading guide

There are 10 main Java files. Read the screen for input/output, AppProcesses for
the workflow, SalesCalculator for formulas, and DatabasePMT for storage.

## Files

| File | Responsibility |
| --- | --- |
| LoginActivity | Sign-in fields, error display and navigation |
| SignUpActivity | Registration fields, error display and navigation |
| ProductActivity | Phone cards and selection buttons |
| CartActivity | Cart display, quantity buttons and checkout confirmation |
| ChartActivity | Daily results and chart drawing |
| AppProcesses | Account validation, cart operations, session, checkout and daily tracking |
| SalesCalculator | Item/cart cost, revenue and gross profit/loss formulas |
| DatabasePMT | Tables, queries and transactional writes |
| Phone | One phone's details |
| CartItem | Selected phone and stock-bounded quantity |

Small daily result types live at the bottom of AppProcesses, not in separate
files. SalesCalculator.Totals holds financial results inside SalesCalculator.
These named values keep the code readable without using numbered array positions.

## Find the process

Open AppProcesses and follow the short section comments:

1. sign-in rules: validateLogin
2. registration rules: validateRegistration
3. cart selections and quantity: getItems, getTotalQuantity, addPhone, increaseQuantity, decreaseQuantity, clearCart, findItem
4. signed-in user: signIn, getUserId, signOut
5. checkout: completeSale
6. daily results: getTodaySalesSummary, getTodayPhoneSales, summarize
7. close day: closeDay
8. daily result data: DailySalesSummary and PhoneSalesRecord

CartItem keeps its quantity limits; it contains no financial formulas.
Account database reads/writes remain direct calls from the account screens;
input rules are in AppProcesses.

## Checkout

CartActivity.confirmSale → saveSale → AppProcesses.completeSale →
SalesCalculator → DatabasePMT.saveSale → refresh CartActivity.

The process validates input and calculates totals. The database checks live stock
and writes the sale, its items and stock changes inside one transaction.
The cart clears only after success. A failed write rolls back the transaction.

## Phones to chart

ChartActivity connects the XML view with findViewById(R.id.phoneSalesChart).
AppProcesses requests today's quantities from DatabasePMT.getPhoneSales.
The database joins phones, sales and sale_items and groups quantities by phone ID.

ChartActivity.displayPhoneSalesChart loops through those records:
each sold phone gets a BarEntry with x = index and y = sold quantity.
The phone label uses the same index. IndexAxisValueFormatter links those labels,
then setData and invalidate display the bars. Unsold phones are skipped.
Colours only affect appearance; they do not read or identify sales data.

## Formulas

- SalesCalculator.calculateItemCost: unit cost × quantity.
- SalesCalculator.calculateItemRevenue: unit selling price × quantity.
- SalesCalculator.calculateItemProfitLoss: item revenue − item cost.
- SalesCalculator.calculateTotalCost / calculateTotalRevenue: loop and sum item totals.
- SalesCalculator.calculateProfitLoss: cart revenue − cart cost.
- AppProcesses.getTotalQuantity: loop and count cart units.
- AppProcesses.summarize: count sold units and choose the highest quantity.
- DatabasePMT.getSalesTotals / getPhoneSales: SQL SUM for the requested date.
- DatabasePMT.reducePhoneStock: subtract sold quantity only when sufficient stock remains.

SQL sums and stock guards belong to the database operations. Screen code contains
display formatting, not financial formulas. A quantity tie keeps the first phone
in the database query's ordering.

## Preserved behaviour

The screen layouts, Java-built cards, chart styling and messages are unchanged.
The database name, schema version and stored data are unchanged.
Close Day deletes today's sales and clears the cart, but does not restore stock.
Sign-out clears the cart and signed-in user. Closing the app loses only in-memory
session/cart state, not completed sales. V1 was not changed.
