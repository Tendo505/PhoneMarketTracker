package com.example.phonemarkettracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends Activity implements CartAdapter.QuantityListener {

    private DatabaseSQL databaseSQL;
    private Object[][] availablePhones = new Object[0][0];
    private CartAdapter cartAdapter;
    private ListView listCart;
    private TextView textEmptyCart;
    private TextView textSelectedCount;
    private TextView textOriginalPrice;
    private TextView textSellingPrice;
    private TextView textProfit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        databaseSQL = new DatabaseSQL(this);
        listCart = findViewById(R.id.listCart);
        textEmptyCart = findViewById(R.id.textEmptyCart);
        textSelectedCount = findViewById(R.id.textSelectedCount);
        textOriginalPrice = findViewById(R.id.textOriginalPrice);
        textSellingPrice = findViewById(R.id.textSellingPrice);
        textProfit = findViewById(R.id.textProfit);

        cartAdapter = new CartAdapter(this, this);
        listCart.setAdapter(cartAdapter);

        findViewById(R.id.buttonAddPhone).setOnClickListener(view -> showAddPhoneDialog());
        findViewById(R.id.buttonClearCart).setOnClickListener(view -> confirmClearCart());
        findViewById(R.id.buttonCheckout).setOnClickListener(view -> confirmCheckout());
        findViewById(R.id.buttonMenu).setOnClickListener(view -> confirmLogout());
        findViewById(R.id.navHome).setOnClickListener(view ->
                Toast.makeText(this, "Home page will be connected by your team", Toast.LENGTH_SHORT).show());
        findViewById(R.id.navProducts).setOnClickListener(view ->
                Toast.makeText(this, "Products page will be connected by your team", Toast.LENGTH_SHORT).show());

        loadAvailablePhones();
        refreshCart();
    }

    private void loadAvailablePhones() {
        availablePhones = databaseSQL.getAvailablePhones();
    }

    private void showAddPhoneDialog() {
        loadAvailablePhones();
        if (availablePhones.length == 0) {
            Toast.makeText(this, "No phones are available", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(22);
        content.setPadding(padding, dp(8), padding, 0);

        Spinner phoneSpinner = new Spinner(this);
        List<String> labels = new ArrayList<>();
        for (Object[] phone : availablePhones) {
            labels.add(phone[1] + " " + phone[2] + " — "
                    + money((double) phone[4]) + " — Stock " + phone[5]);
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, labels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneSpinner.setAdapter(spinnerAdapter);
        content.addView(phoneSpinner, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(54)));

        EditText quantityInput = new EditText(this);
        quantityInput.setHint("Quantity");
        quantityInput.setText("1");
        quantityInput.setSelectAllOnFocus(true);
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams quantityParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(54));
        quantityParams.topMargin = dp(12);
        content.addView(quantityInput, quantityParams);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add phone to cart")
                .setView(content)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", null)
                .create();

        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    int quantity;
                    try {
                        quantity = Integer.parseInt(quantityInput.getText().toString().trim());
                    } catch (NumberFormatException exception) {
                        quantityInput.setError("Enter a whole number");
                        return;
                    }
                    if (quantity < 1) {
                        quantityInput.setError("Quantity must be at least 1");
                        return;
                    }
                    int position = phoneSpinner.getSelectedItemPosition();
                    if (addPhoneToCart(availablePhones[position], quantity)) {
                        dialog.dismiss();
                    }
                }));
        dialog.show();
    }

    private boolean addPhoneToCart(Object[] phone, int quantity) {
        int phoneId = (int) phone[0];
        int stock = (int) phone[5];
        int requestedTotal = SessionStore.getCartQuantity(phoneId) + quantity;
        if (requestedTotal > stock) {
            Toast.makeText(this, "Only " + stock + " unit(s) are available", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!SessionStore.addToCart(phone, quantity)) {
            Toast.makeText(this, "The cart is full", Toast.LENGTH_SHORT).show();
            return false;
        }
        refreshCart();
        return true;
    }

    @Override
    public void onDecrease(int position) {
        int current = (int) SessionStore.cartData[position][5];
        if (current <= 1) {
            SessionStore.removeCartItem(position);
        } else {
            SessionStore.updateCartQuantity(position, current - 1);
        }
        refreshCart();
    }

    @Override
    public void onIncrease(int position) {
        int phoneId = (int) SessionStore.cartData[position][0];
        int current = (int) SessionStore.cartData[position][5];
        int stock = findAvailableStock(phoneId);
        if (current >= stock) {
            Toast.makeText(this, "Only " + stock + " unit(s) are available", Toast.LENGTH_SHORT).show();
            return;
        }
        SessionStore.updateCartQuantity(position, current + 1);
        refreshCart();
    }

    private int findAvailableStock(int phoneId) {
        for (Object[] phone : availablePhones) {
            if ((int) phone[0] == phoneId) {
                return (int) phone[5];
            }
        }
        return 0;
    }

    private void refreshCart() {
        cartAdapter.notifyDataSetChanged();
        boolean isEmpty = SessionStore.cartCount == 0;
        listCart.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        textEmptyCart.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        String noun = SessionStore.cartCount == 1 ? "phone selected" : "phones selected";
        textSelectedCount.setText(SessionStore.cartCount + " " + noun);

        double cost = SessionStore.getCartCost();
        double revenue = SessionStore.getCartRevenue();
        double profit = SessionStore.getCartProfitLoss();
        textOriginalPrice.setText(money(cost));
        textSellingPrice.setText(money(revenue));
        textProfit.setText(profitText(profit));
        textProfit.setTextColor(Color.parseColor(profit < 0 ? "#DC2626" : "#16A34A"));
    }

    private void confirmClearCart() {
        if (SessionStore.cartCount == 0) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Clear cart?")
                .setMessage("All selected phones will be removed.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Clear", (dialog, which) -> {
                    SessionStore.clearCart();
                    refreshCart();
                })
                .show();
    }

    private void confirmCheckout() {
        if (SessionStore.cartCount == 0) {
            Toast.makeText(this, "Add at least one phone first", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Complete this sale?")
                .setMessage("Selling price: " + money(SessionStore.getCartRevenue())
                        + "\nProfit / Loss: " + profitText(SessionStore.getCartProfitLoss()))
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Complete sale", (dialog, which) -> completeCheckout())
                .show();
    }

    private void completeCheckout() {
        try {
            databaseSQL.checkout(SessionStore.cartData, SessionStore.cartCount);
            SessionStore.recordCompletedCart();
            SessionStore.clearCart();
            loadAvailablePhones();
            refreshCart();

            new AlertDialog.Builder(this)
                    .setTitle("Sale completed")
                    .setMessage("Session profit / loss: "
                            + profitText(SessionStore.getSessionProfitLoss())
                            + "\nMost sold phone: " + SessionStore.getMostSoldPhone())
                    .setPositiveButton("OK", null)
                    .show();
        } catch (IllegalStateException exception) {
            loadAvailablePhones();
            new AlertDialog.Builder(this)
                    .setTitle("Checkout failed")
                    .setMessage(exception.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("End session?")
                .setMessage("Cart and sales tracking will reset. Phone stock remains in SQLite.")
                .setNegativeButton("Stay", null)
                .setPositiveButton("Sign out", (dialog, which) -> {
                    SessionStore.resetSession();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        confirmLogout();
    }

    @Override
    protected void onDestroy() {
        databaseSQL.close();
        super.onDestroy();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private String money(double value) {
        return String.format(Locale.US, "RM %,.2f", value);
    }

    private String profitText(double value) {
        String sign = value < 0 ? "− " : "+ ";
        return sign + money(Math.abs(value));
    }
}
