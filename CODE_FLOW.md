# V2 code reading guide

The methods are grouped for reading. Moving a method in a file does not change
when it runs: Android lifecycle events and method calls determine execution.
Existing method names, method bodies, layouts and calculations are preserved.

## Read the screens in this order

1. **LoginActivity**: screen setup → input readers → validation and sign-in → output → navigation.
2. **SignUpActivity**: screen setup → input readers → validation and registration → output → navigation.
3. **ProductActivity**: setup → read and display phones → ADD action → card construction → styling → navigation.
4. **CartActivity**: setup → display cart and totals → confirm/save/clear → quantity controls → formatting → navigation.
5. **ChartActivity**: setup → daily results and chart → reset → formatting → styling → navigation.

`onCreate()` sets up a screen. Where present, `onResume()` refreshes its data.
Button listeners call the action methods when the user taps a button.
Input, process and output sometimes share a method; the section labels describe
the main responsibility rather than claiming every method does only one thing.

## Follow the main processes

| Process | Follow these calls |
| --- | --- |
| Register | SignUpActivity.validateInputAndReturnToSignIn → DatabasePMT.emailExists / addUser |
| Sign in | LoginActivity.validateInputAndOpenProductMenu → DatabasePMT.getUserId → UserSession.signIn |
| Display phones | ProductActivity.displayAvailablePhones → DatabasePMT.getAllPhones → loop → createPhoneCard |
| Build one card | createPhoneCard → createPhoneTile / createPhoneDetails / createAddButton |
| Select a phone | ProductActivity.addPhoneToCart → CartManager.addPhone |
| Adjust quantity | CartActivity.createQuantityRow listeners → CartManager → CartItem |
| Cart totals | CartActivity.displayCalculatedTotals → SalesCalculator → CartItem |
| Checkout | CartActivity.confirmSale → saveSale → DatabasePMT.completeSale |
| Save transaction | completeSale → validateAvailableStock → save sale → loop: insertSaleItem + reducePhoneStock |
| Daily results | ChartActivity.displayDailySales → DatabasePMT.getTodaySalesSummary / getTodayPhoneSales |
| Draw chart | ChartActivity.displayPhoneSalesChart → loop: create bar entries → setData → invalidate |
| Close Day | ChartActivity.confirmDailyReset → resetDailyTracking → DatabasePMT.resetTodaySales |
| Sign out | ProductActivity.signOut → CartManager.clear + UserSession.signOut |

## Supporting classes

- **DatabasePMT**: SQLite setup, accounts, phones, checkout, daily totals and reset.
- **CartManager**: the shared in-memory cart, quantity changes and item searches.
- **CartItem**: one selected phone, its quantity and its item totals.
- **SalesCalculator**: whole-cart cost, revenue and gross profit/loss.
- **Phone**: one phone record with prices and stock.
- **DailySalesSummary**: carries daily totals and the top-selling phone result.
- **PhoneSalesRecord**: carries a phone name and its sold quantity.
- **UserSession**: remembers the current user ID in memory.

## Calculations and loops

- Item cost = cost price × quantity.
- Item revenue = selling price × quantity.
- Gross profit/loss = revenue − cost.
- SalesCalculator loops through cart items to sum cost and revenue.
- CartManager loops through cart items to count selected units.
- DatabasePMT uses SQL SUM for daily financial totals and quantities per phone.
- Its summary loop counts sold units and selects the highest-selling phone.
- Remaining stock = current stock − quantity sold after successful checkout.

`phones` is the whole list; `phone` is one item in a for-each loop. If the query
returns six records, the loop creates six cards using the same method. It is not
hardcoded to repeat six times. There are still 13 explicit Java loops in V2.

## Interface versus processing

XML defines screen layouts. Product and cart cards are also built in Java.
Methods grouped under styling change appearance, not sales data:
`getBrandColor()` selects a colour for the phone body and brand label;
`getBrandTileColor()` selects the surrounding tile's background colour.
Both return colour resource IDs; neither reads the database.

The cart contains estimates until checkout succeeds. Close Day deletes today's
sales and linked items, but does not restore stock. Closing the app is not a reset.
