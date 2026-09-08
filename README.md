# PhoneMarketTracker V2

PhoneMarketTracker is a simple Android Studio Koala application for a small
phone-shop owner. It uses pure Java, editable Android XML layouts, one local
SQLite database, and MPAndroidChart 3.1.0.

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

Closing the day does not delete users, phone details, prices, or current stock.
The cart is held in memory and also resets when the application process closes.

## SQLite tables

- `users`: local authentication accounts
- `phones`: brand, model, cost price, selling price, and stock quantity
- `sales`: user, date, total cost, revenue, and profit or loss
- `sale_items`: phones and quantities included in each completed sale

The six starting phone records are represented in a multidimensional Java array
before they are inserted into SQLite.

## Build and test

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
```

Open the project folder in Android Studio Koala, allow Gradle to sync, choose an
Android emulator, and run the `app` configuration.
