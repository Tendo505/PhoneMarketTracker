package com.example.phonemarkettracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class CartActivity extends Activity implements CartAdapter.CartListener {

    private RecyclerView recyclerCart;
    private TextView tvCartTotal, tvCartEmpty;
    private Button btnCompleteSale;
    private CartAdapter adapter;
    private DatabasePMT databasePMT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        databasePMT = new DatabasePMT(this);

        recyclerCart    = findViewById(R.id.recyclerCart);
        tvCartTotal     = findViewById(R.id.tvCartTotal);
        tvCartEmpty     = findViewById(R.id.tvCartEmpty);
        btnCompleteSale = findViewById(R.id.btnCompleteSale);

        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(CartManager.get().getItems(), this);
        recyclerCart.setAdapter(adapter);

        refreshUi();

        btnCompleteSale.setOnClickListener(v -> completeSale());

        findViewById(R.id.navChart).setOnClickListener(v -> {
            startActivity(new Intent(this, ChartActivity.class));
        });
        findViewById(R.id.navProducts).setOnClickListener(v -> {
            startActivity(new Intent(this, ProductActivity.class));
        });
    }

    private void refreshUi() {
        adapter.notifyDataSetChanged();
        tvCartTotal.setText(String.format(Locale.US,
                "Total: RM %.2f", CartManager.get().getTotalSellingPrice()));
        tvCartEmpty.setVisibility(
                CartManager.get().isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCartChanged() { refreshUi(); }

    // 5. Complete Sale
    private void completeSale() {
        if (CartManager.get().isEmpty()) {
            Toast.makeText(this, "Cart is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        long saleId = databasePMT.completeSale(CartManager.get().getItems());
        if (saleId > 0) {
            Toast.makeText(this,
                    "Sale completed (ID " + saleId + ").",
                    Toast.LENGTH_SHORT).show();
            CartManager.get().clear();
            refreshUi();
        } else {
            Toast.makeText(this,
                    "Sale failed. Please check stock.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (databasePMT != null) databasePMT.close();
        super.onDestroy();
    }
}