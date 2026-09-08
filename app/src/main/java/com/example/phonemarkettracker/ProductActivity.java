package com.example.phonemarkettracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/** Displays phones stored in SQLite and adds available units to the current cart. */
public class ProductActivity extends Activity {

    private DatabasePMT databasePMT;
    private LinearLayout productContainer;
    private TextView productCountText;

    // create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        databasePMT = new DatabasePMT(this);
        connectViews();
        setUpNavigation();
    }

    // display output
    @Override
    protected void onResume() {
        super.onResume();
        displayAvailablePhones();
    }

    // read
    private void connectViews() {
        productContainer = findViewById(R.id.productContainer);
        productCountText = findViewById(R.id.textProductCount);
    }

    private void setUpNavigation() {
        findViewById(R.id.navChart).setOnClickListener(view -> openChartScreen());
        findViewById(R.id.navCart).setOnClickListener(view -> openCartScreen());
        findViewById(R.id.buttonSignOut).setOnClickListener(view -> signOut());
    }

    // read
    private void displayAvailablePhones() {
        List<Phone> phones = databasePMT.getAllPhones();
        productContainer.removeAllViews();

        for (Phone phone : phones) {
            productContainer.addView(createPhoneCard(phone));
        }

        String countMessage = getResources().getQuantityString(
                R.plurals.phone_count,
                phones.size(),
                phones.size()
        );
        productCountText.setText(
                countMessage + " available  •  " +
                        CartManager.getTotalQuantity() + " in cart"
        );
    }

    // create
    private View createPhoneCard(Phone phone) {
        LinearLayout phoneCard = new LinearLayout(this);
        phoneCard.setOrientation(LinearLayout.HORIZONTAL);
        phoneCard.setGravity(Gravity.CENTER_VERTICAL);
        phoneCard.setPadding(dp(14), dp(12), dp(12), dp(12));
        phoneCard.setBackgroundResource(R.drawable.figma_card);
        phoneCard.setElevation(dp(2));

        LinearLayout.LayoutParams cardLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(132)
        );
        cardLayout.topMargin = dp(12);
        phoneCard.setLayoutParams(cardLayout);

        phoneCard.addView(createPhoneTile(phone));
        phoneCard.addView(createPhoneDetails(phone));
        phoneCard.addView(createAddButton(phone));
        return phoneCard;
    }

    // create
    private FrameLayout createPhoneTile(Phone phone) {
        FrameLayout phoneTile = new FrameLayout(this);
        phoneTile.setBackground(createRoundedBackground(
                getBrandTileColor(phone.getBrand()),
                14
        ));
        phoneTile.setLayoutParams(new LinearLayout.LayoutParams(dp(62), dp(82)));

        View phoneBody = new View(this);
        phoneBody.setBackground(createRoundedBackground(
                getBrandColor(phone.getBrand()),
                5
        ));
        phoneTile.addView(phoneBody, new FrameLayout.LayoutParams(
                dp(28),
                dp(58),
                Gravity.CENTER
        ));
        phoneTile.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        return phoneTile;
    }

    // create
    private LinearLayout createPhoneDetails(Phone phone) {
        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams detailsLayout = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        detailsLayout.setMarginStart(dp(14));
        detailsLayout.setMarginEnd(dp(8));
        details.setLayoutParams(detailsLayout);

        details.addView(createLabel(
                phone.getBrand().toUpperCase(Locale.ROOT),
                10,
                getBrandColor(phone.getBrand()),
                true
        ));
        details.addView(createLabel(phone.getModel(), 16, R.color.text_primary, true));
        details.addView(createLabel(
                "Cost  " + formatMoney(phone.getCostPrice()),
                12,
                R.color.text_secondary,
                false
        ));

        int stockColor = phone.getStockQuantity() > 0
                ? R.color.profit
                : R.color.loss;
        details.addView(createLabel(
                "Selling  " + formatMoney(phone.getSellingPrice()) +
                        "  •  Stock " + phone.getStockQuantity(),
                12,
                stockColor,
                true
        ));
        return details;
    }

    // create
    private Button createAddButton(Phone phone) {
        Button addButton = new Button(this);
        addButton.setText(phone.getStockQuantity() > 0 ? "ADD" : "SOLD\nOUT");
        addButton.setTextColor(Color.WHITE);
        addButton.setTextSize(10);
        addButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        addButton.setAllCaps(false);
        addButton.setPadding(0, 0, 0, 0);
        addButton.setBackgroundResource(R.drawable.figma_primary_button);
        addButton.setEnabled(phone.getStockQuantity() > 0);
        addButton.setLayoutParams(new LinearLayout.LayoutParams(dp(58), dp(46)));
        addButton.setOnClickListener(view -> addPhoneToCart(phone));
        return addButton;
    }

    // create
    private TextView createLabel(
            String text,
            int textSize,
            int colorResource,
            boolean bold
    ) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(getColor(colorResource));
        label.setTextSize(textSize);
        label.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        return label;
    }

    // create
    private GradientDrawable createRoundedBackground(int colorResource, int radiusDp) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(getColor(colorResource));
        background.setCornerRadius(dp(radiusDp));
        return background;
    }

    // create
    private void addPhoneToCart(Phone phone) {
        boolean phoneAdded = CartManager.addPhone(phone);

        if (!phoneAdded) {
            Toast.makeText(
                    this,
                    "No more stock available for " + phone.getModel(),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        productCountText.setText(
                databasePMT.getAllPhones().size() + " phones available  •  " +
                        CartManager.getTotalQuantity() + " in cart"
        );
        Toast.makeText(this, phone.getModel() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    // read
    private int getBrandColor(String brand) {
        String normalizedBrand = brand.toLowerCase(Locale.ROOT);

        if (normalizedBrand.contains("samsung")) {
            return R.color.samsung_phone;
        }

        if (normalizedBrand.contains("xiaomi")) {
            return R.color.xiaomi_phone;
        }

        if (normalizedBrand.contains("oppo")) {
            return R.color.profit;
        }

        return R.color.apple_phone;
    }

    // read
    private int getBrandTileColor(String brand) {
        String normalizedBrand = brand.toLowerCase(Locale.ROOT);

        if (normalizedBrand.contains("samsung")) {
            return R.color.samsung_tile;
        }

        if (normalizedBrand.contains("xiaomi")) {
            return R.color.xiaomi_tile;
        }

        if (normalizedBrand.contains("oppo")) {
            return R.color.icon_circle;
        }

        return R.color.apple_tile;
    }

    // display output
    private String formatMoney(double amount) {
        return String.format(Locale.US, "RM %,.0f", amount);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void openChartScreen() {
        startActivity(new Intent(this, ChartActivity.class));
    }

    private void openCartScreen() {
        startActivity(new Intent(this, CartActivity.class));
    }

    // delete current session
    private void signOut() {
        CartManager.clear();
        UserSession.signOut();

        Intent signInIntent = new Intent(this, LoginActivity.class);
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signInIntent);
        finish();
    }
}
