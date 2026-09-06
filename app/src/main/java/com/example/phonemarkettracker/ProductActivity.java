package com.example.phonemarkettracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/** Static front-end product menu. Product cards contain preview-only values. */
public class ProductActivity extends Activity {

    // create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        setUpNavigation();
    }

    private void setUpNavigation() {
        findViewById(R.id.navChart).setOnClickListener(view -> openChartScreen());
        findViewById(R.id.navCart).setOnClickListener(view -> openCartScreen());
        findViewById(R.id.buttonSignOut).setOnClickListener(view -> signOut());
    }

    private void openChartScreen() {
        Intent chartIntent = new Intent(this, ChartActivity.class);
        startActivity(chartIntent);
    }

    private void openCartScreen() {
        Intent cartIntent = new Intent(this, CartActivity.class);
        startActivity(cartIntent);
    }

    // delete current screen from the signed-in flow
    private void signOut() {
        Intent signInIntent = new Intent(this, LoginActivity.class);
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signInIntent);
        finish();
    }
}
