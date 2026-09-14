package com.example.phonemarkettracker;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder> {

    public interface OnPhoneActionListener {
        void onQuantityChanged(Phone phone, int newQuantity);
        void onSelectPhone(Phone phone, int quantity);
    }

    private final List<Phone> phoneList;
    private final OnPhoneActionListener listener;

    public PhoneAdapter(List<Phone> phoneList, OnPhoneActionListener listener) {
        this.phoneList = phoneList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_phone, parent, false);
        return new PhoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneViewHolder holder, int position) {
        Phone phone = phoneList.get(position);

        holder.tvBrandModel.setText(phone.getBrand() + " " + phone.getModel());
        holder.tvCostPrice.setText(String.format(Locale.US,
                "Cost: RM %.2f", phone.getCostPrice()));
        holder.tvSellingPrice.setText(String.format(Locale.US,
                "Price: RM %.2f", phone.getSellingPrice()));
        holder.tvStock.setText("Stock: " + phone.getStockQuantity());

        // detach watcher before setting text to avoid loops
        if (holder.quantityWatcher != null) {
            holder.etQuantity.removeTextChangedListener(holder.quantityWatcher);
        }

        holder.etQuantity.setText(String.valueOf(holder.currentQuantity));

        holder.quantityWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int qty;
                try {
                    qty = Integer.parseInt(s.toString().trim());
                } catch (NumberFormatException e) {
                    qty = 0;
                }
                if (qty < 0) qty = 0;
                if (qty > phone.getStockQuantity()) {
                    qty = phone.getStockQuantity();
                    holder.etQuantity.setText(String.valueOf(qty));
                    holder.etQuantity.setSelection(holder.etQuantity.getText().length());
                    return;
                }
                holder.currentQuantity = qty;
                listener.onQuantityChanged(phone, qty);
            }
        };
        holder.etQuantity.addTextChangedListener(holder.quantityWatcher);

        holder.btnMinus.setOnClickListener(v -> {
            if (holder.currentQuantity > 0) {
                holder.currentQuantity--;
                holder.etQuantity.setText(String.valueOf(holder.currentQuantity));
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            if (holder.currentQuantity < phone.getStockQuantity()) {
                holder.currentQuantity++;
                holder.etQuantity.setText(String.valueOf(holder.currentQuantity));
            }
        });

        holder.btnSelect.setOnClickListener(v ->
                listener.onSelectPhone(phone, holder.currentQuantity));
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    static class PhoneViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandModel, tvCostPrice, tvSellingPrice, tvStock;
        EditText etQuantity;
        Button btnMinus, btnPlus, btnSelect;
        int currentQuantity = 0;
        TextWatcher quantityWatcher;

        PhoneViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandModel   = itemView.findViewById(R.id.tvBrandModel);
            tvCostPrice    = itemView.findViewById(R.id.tvCostPrice);
            tvSellingPrice = itemView.findViewById(R.id.tvSellingPrice);
            tvStock        = itemView.findViewById(R.id.tvStock);
            etQuantity     = itemView.findViewById(R.id.etQuantity);
            btnMinus       = itemView.findViewById(R.id.btnMinus);
            btnPlus        = itemView.findViewById(R.id.btnPlus);
            btnSelect      = itemView.findViewById(R.id.btnSelect);
        }
    }
}
