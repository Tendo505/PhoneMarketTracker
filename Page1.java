package com.example.phonemarkettracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements PhoneAdapter.OnPhoneActionListener {

    private RecyclerView recyclerPhones;
    private TextView tvTotal;
    private Button toAccessory, toCart;

    private DatabasePMT databasePMT;
    private PhoneAdapter adapter;
    private final List<Phone> phoneList = new ArrayList<>();

    // phoneId -> selected quantity
    private final Map<Integer, Integer> selectedQuantities = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerPhones = findViewById(R.id.recyclerPhones);
        tvTotal        = findViewById(R.id.tvTotal);
        toAccessory    = findViewById(R.id.ToAccessory);
        toCart         = findViewById(R.id.ToCart);

        databasePMT = new DatabasePMT(this);

        recyclerPhones.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PhoneAdapter(phoneList, this);
        recyclerPhones.setAdapter(adapter);

        loadPhones();

        toAccessory.setOnClickListener(v ->
                Toast.makeText(this, "Accessories page", Toast.LENGTH_SHORT).show());

        toCart.setOnClickListener(v ->
                Toast.makeText(this, "Cart page", Toast.LENGTH_SHORT).show());
    }

    private void loadPhones() {
        phoneList.clear();
        phoneList.addAll(databasePMT.getAllPhones());
        adapter.notifyDataSetChanged();
        updateTotal();
    }

    private void updateTotal() {
        double total = 0.0;
        for (Phone phone : phoneList) {
            Integer qty = selectedQuantities.get(phone.getId());
            if (qty != null && qty > 0) {
                total += qty * phone.getSellingPrice();
            }
        }
        tvTotal.setText(String.format(Locale.US, "Total: RM %.2f", total));
    }

    @Override
    public void onQuantityChanged(Phone phone, int newQuantity) {
        if (newQuantity <= 0) {
            selectedQuantities.remove(phone.getId());
        } else {
            selectedQuantities.put(phone.getId(), newQuantity);
        }
        updateTotal();
    }

    @Override
    public void onSelectPhone(Phone phone, int quantity) {
        if (quantity <= 0) {
            Toast.makeText(this,
                    "Please enter a quantity greater than 0.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = databasePMT.reduceStock(phone.getId(), quantity);
        if (success) {
            Toast.makeText(this,
                    quantity + " x " + phone.getBrand() + " " + phone.getModel()
                            + " added to cart.",
                    Toast.LENGTH_SHORT).show();

            selectedQuantities.remove(phone.getId());
            loadPhones(); // refresh stock + reset quantities
        } else {
            Toast.makeText(this,
                    "Not enough stock available.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (databasePMT != null) {
            databasePMT.close();
        }
        super.onDestroy();
    }
}