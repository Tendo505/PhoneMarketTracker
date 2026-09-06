# PhoneMarketTracker Android Front End

PhoneMarketTracker is a simple Android Studio Koala interface built with pure
Java and editable XML layouts. This version intentionally contains no database,
authentication backend, stock updates, checkout logic, or persistent storage.

## Included screens

- Sign In
- Sign Up
- Product Menu with static preview cards
- Sales Overview using MPAndroidChart
- Blank Cart screen reserved for later development

The buttons provide navigation and basic form validation so the complete UI can
be demonstrated in an emulator without a backend.

## Chart library

The Sales Overview uses MPAndroidChart `v3.1.0`:

```gradle
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

JitPack is already included in `settings.gradle`.

## Open and run

1. Open Android Studio Koala.
2. Select **Open** and choose the `PhoneMarketTracker` folder.
3. Wait for Gradle sync to complete.
4. Choose an emulator and press **Run**.

On Sign In, enter any non-empty email and password to open the Product Menu.
This is only front-end behaviour; the values are not authenticated or saved.
