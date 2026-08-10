# PhoneMarketTracker Android — Login and Cart

This is the student's Login and Cart scope for Android Studio Koala. It uses
Java, editable XML layouts, SQLite, and multidimensional arrays. A matching
Sign Up screen supports creation of additional shop-owner accounts.

## Open and run

1. Open Android Studio Koala.
2. Select **Open** and choose the `PhoneMarketTracker` folder.
3. Allow the Gradle sync to finish.
4. Select an Android emulator or connected phone and press **Run**.

## Demo login

- Username: `admin`
- Password: `admin123`

## Main files

- `LoginActivity.java` — validates the owner's SQLite login.
- `SignUpActivity.java` — creates a new SQLite user with a hashed password.
- `CartActivity.java` — adds phones, calculates totals, checks out, and reduces stock.
- `CartAdapter.java` — displays the Figma-style phone cards and quantity controls.
- `DatabaseHelper.java` — SQLite tables, sample phone data, login, and stock transaction.
- `SessionStore.java` — multidimensional cart and session-sales arrays.
- `activity_login.xml` and `activity_cart.xml` — fully editable Android layouts.

The team's Menu and Product Information pages can later open `CartActivity` and
use the same `DatabaseHelper` and `SessionStore` classes.
