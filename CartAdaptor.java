package com.example.phonemarkettracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartListener {
        void onCartChanged();
    }

    private final List<CartItem> items;
    private final CartListener listener;

    public CartAdapter(List<CartItem> items, CartListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder h, int position) {
        CartItem item = items.get(position);
        Phone phone = item.getPhone();

        h.tvCartBrand.setText(phone.getBrand().toUpperCase(Locale.US));
        h.tvCartModel.setText(phone.getModel());
        h.tvCartUnit.setText(String.format(Locale.US,
                "Unit: RM %.2f • Stock: %d",
                phone.getSellingPrice(), phone.getStockQuantity()));
        h.tvCartLine.setText(String.format(Locale.US,
                "Subtotal: RM %.2f", item.getLineTotal()));
        h.tvCartQty.setText(String.valueOf(item.getQuantity()));

        h.btnCartMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(h.getAdapterPosition());
                listener.onCartChanged();
            } else {
                // quantity 1 → minus removes
                CartManager.get().remove(phone.getId());
                notifyDataSetChanged();
                listener.onCartChanged();
            }
        });

        h.btnCartPlus.setOnClickListener(v -> {
            if (item.getQuantity() + 1 > phone.getStockQuantity()) {
                Toast.makeText(v.getContext(),
                        "Only " + phone.getStockQuantity() + " in stock.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(h.getAdapterPosition());
            listener.onCartChanged();
        });

        h.btnCartRemove.setOnClickListener(v -> {
            CartManager.get().remove(phone.getId());
            notifyDataSetChanged();
            listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvCartBrand, tvCartModel, tvCartUnit, tvCartLine, tvCartQty, btnCartRemove;
        Button btnCartMinus, btnCartPlus;

        CartViewHolder(@NonNull View v) {
            super(v);
            tvCartBrand   = v.findViewById(R.id.tvCartBrand);
            tvCartModel   = v.findViewById(R.id.tvCartModel);
            tvCartUnit    = v.findViewById(R.id.tvCartUnit);
            tvCartLine    = v.findViewById(R.id.tvCartLine);
            tvCartQty     = v.findViewById(R.id.tvCartQty);
            btnCartMinus  = v.findViewById(R.id.btnCartMinus);
            btnCartPlus   = v.findViewById(R.id.btnCartPlus);
            btnCartRemove = v.findViewById(R.id.btnCartRemove);
        }
    }
}
