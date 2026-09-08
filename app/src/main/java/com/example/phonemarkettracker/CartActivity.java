package com.example.phonemarkettracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/** Displays the current cart, calculates totals, and saves completed sales. */
public class CartActivity extends Activity {

    private DatabasePMT databasePMT;
    private LinearLayout cartItemContainer;
    private LinearLayout cartSummaryCard;
    private TextView emptyCartText;
    private TextView cartCountText;
    private TextView totalCostText;
    private TextView totalRevenueText;
    private TextView profitLossText;
    private Button completeSaleButton;

    // create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        databasePMT = new DatabasePMT(this);
        connectViews();
        setUpActions();
    }

    // display output
    @Override
    protected void onResume() {
        super.onResume();
        displayCart();
    }

    // read
    private void connectViews() {
        cartItemContainer = findViewById(R.id.cartItemContainer);
        cartSummaryCard = findViewById(R.id.cartSummaryCard);
        emptyCartText = findViewById(R.id.textEmptyCart);
        cartCountText = findViewById(R.id.textCartCount);
        totalCostText = findViewById(R.id.textTotalCost);
        totalRevenueText = findViewById(R.id.textTotalRevenue);
        profitLossText = findViewById(R.id.textProfitLoss);
        completeSaleButton = findViewById(R.id.buttonCompleteSale);
    }

    private void setUpActions() {
        findViewById(R.id.buttonClearCart).setOnClickListener(view -> clearCart());
        completeSaleButton.setOnClickListener(view -> confirmSale());
        findViewById(R.id.navChart).setOnClickListener(view -> openChartScreen());
        findViewById(R.id.navProducts).setOnClickListener(view -> openProductMenu());
    }

    // display output
    private void displayCart() {
        List<CartItem> cartItems = CartManager.getItems();
        boolean cartEmpty = cartItems.isEmpty();

        cartItemContainer.removeAllViews();
        emptyCartText.setVisibility(cartEmpty ? View.VISIBLE : View.GONE);
        cartSummaryCard.setVisibility(cartEmpty ? View.GONE : View.VISIBLE);
        completeSaleButton.setEnabled(!cartEmpty);

        String countMessage = getResources().getQuantityString(
                R.plurals.cart_item_count,
                CartManager.getTotalQuantity(),
                CartManager.getTotalQuantity()
        );
        cartCountText.setText(countMessage);

        for (CartItem cartItem : cartItems) {
            cartItemContainer.addView(createCartItemCard(cartItem));
        }

        displayCalculatedTotals(cartItems);
    }

    // create
    private View createCartItemCard(CartItem cartItem) {
        LinearLayout itemCard = new LinearLayout(this);
        itemCard.setOrientation(LinearLayout.VERTICAL);
        itemCard.setPadding(dp(16), dp(14), dp(16), dp(14));
        itemCard.setBackgroundResource(R.drawable.figma_card);
        itemCard.setElevation(dp(2));

        LinearLayout.LayoutParams cardLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardLayout.topMargin = dp(12);
        itemCard.setLayoutParams(cardLayout);

        TextView phoneName = createText(
                cartItem.getPhone().getDisplayName(),
                16,
                R.color.text_primary,
                true
        );
        itemCard.addView(phoneName);

        TextView priceDetails = createText(
                "Selling " + formatMoney(cartItem.getPhone().getSellingPrice()) +
                        "  •  Stock " + cartItem.getPhone().getStockQuantity(),
                12,
                R.color.text_secondary,
                false
        );
        itemCard.addView(priceDetails);
        itemCard.addView(createQuantityRow(cartItem));
        return itemCard;
    }

    // create
    private LinearLayout createQuantityRow(CartItem cartItem) {
        LinearLayout quantityRow = new LinearLayout(this);
        quantityRow.setGravity(Gravity.CENTER_VERTICAL);
        quantityRow.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams rowLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowLayout.topMargin = dp(10);
        quantityRow.setLayoutParams(rowLayout);

        TextView decreaseButton = createQuantityButton("−");
        TextView quantityText = createText(
                String.valueOf(cartItem.getQuantity()),
                14,
                R.color.text_primary,
                true
        );
        quantityText.setGravity(Gravity.CENTER);
        quantityText.setLayoutParams(new LinearLayout.LayoutParams(dp(42), dp(38)));
        TextView increaseButton = createQuantityButton("+");

        TextView lineTotal = createText(
                formatMoney(cartItem.calculateRevenueTotal()),
                15,
                R.color.navy_light,
                true
        );
        LinearLayout.LayoutParams totalLayout = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        totalLayout.setMarginStart(dp(12));
        lineTotal.setGravity(Gravity.END);
        lineTotal.setLayoutParams(totalLayout);

        decreaseButton.setOnClickListener(view -> {
            CartManager.decreaseQuantity(cartItem.getPhone().getPhoneId());
            displayCart();
        });
        increaseButton.setOnClickListener(view -> {
            boolean quantityIncreased = CartManager.increaseQuantity(
                    cartItem.getPhone().getPhoneId()
            );

            if (!quantityIncreased) {
                Toast.makeText(this, "Maximum available stock reached", Toast.LENGTH_SHORT).show();
            }

            displayCart();
        });

        quantityRow.addView(decreaseButton);
        quantityRow.addView(quantityText);
        quantityRow.addView(increaseButton);
        quantityRow.addView(lineTotal);
        return quantityRow;
    }

    // create
    private TextView createQuantityButton(String label) {
        TextView quantityButton = createText(label, 18, R.color.navy_light, true);
        quantityButton.setGravity(Gravity.CENTER);
        quantityButton.setBackgroundResource(R.drawable.figma_quantity_control);
        quantityButton.setLayoutParams(new LinearLayout.LayoutParams(dp(38), dp(38)));
        return quantityButton;
    }

    // create
    private TextView createText(
            String text,
            int textSize,
            int colorResource,
            boolean bold
    ) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(getColor(colorResource));
        textView.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        return textView;
    }

    // calculate result
    private void displayCalculatedTotals(List<CartItem> cartItems) {
        double totalCost = SalesCalculator.calculateTotalCost(cartItems);
        double totalRevenue = SalesCalculator.calculateTotalRevenue(cartItems);
        double profitLoss = SalesCalculator.calculateProfitLoss(cartItems);

        totalCostText.setText(formatMoney(totalCost));
        totalRevenueText.setText(formatMoney(totalRevenue));
        profitLossText.setText(formatSignedMoney(profitLoss));
        profitLossText.setTextColor(getColor(
                profitLoss >= 0 ? R.color.profit : R.color.loss
        ));
    }

    // create
    private void confirmSale() {
        List<CartItem> cartItems = CartManager.getItems();

        if (cartItems.isEmpty()) {
            return;
        }

        String message =
                "Revenue: " + formatMoney(SalesCalculator.calculateTotalRevenue(cartItems)) +
                        "\nProfit / Loss: " +
                        formatSignedMoney(SalesCalculator.calculateProfitLoss(cartItems));

        new AlertDialog.Builder(this)
                .setTitle("Complete sale?")
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Complete", (dialog, which) -> saveSale(cartItems))
                .show();
    }

    // create
    private void saveSale(List<CartItem> cartItems) {
        boolean saleCompleted = databasePMT.completeSale(
                UserSession.getUserId(),
                cartItems
        );

        if (!saleCompleted) {
            Toast.makeText(
                    this,
                    "Sale could not be completed. Check the available stock.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        CartManager.clear();
        displayCart();
        Toast.makeText(this, "Sale completed and saved", Toast.LENGTH_SHORT).show();
    }

    // delete
    private void clearCart() {
        CartManager.clear();
        displayCart();
    }

    // display output
    private String formatMoney(double amount) {
        return String.format(Locale.US, "RM %,.2f", amount);
    }

    // display output
    private String formatSignedMoney(double amount) {
        String sign = amount >= 0 ? "+" : "−";
        return sign + formatMoney(Math.abs(amount));
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void openChartScreen() {
        startActivity(new Intent(this, ChartActivity.class));
    }

    private void openProductMenu() {
        startActivity(new Intent(this, ProductActivity.class));
    }
}
