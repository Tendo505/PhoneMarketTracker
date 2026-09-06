package com.example.phonemarkettracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/** Blank cart screen reserved for the team's later implementation. */
public class CartActivity extends Activity {

    // create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setUpNavigation();
    }

    private void setUpNavigation() {
        findViewById(R.id.navChart).setOnClickListener(view -> openChartScreen());
        findViewById(R.id.navProducts).setOnClickListener(view -> openProductMenu());
    }

    private void openChartScreen() {
        Intent chartIntent = new Intent(this, ChartActivity.class);
        startActivity(chartIntent);
    }

    private void openProductMenu() {
        Intent productMenuIntent = new Intent(this, ProductActivity.class);
        startActivity(productMenuIntent);
    }
}
