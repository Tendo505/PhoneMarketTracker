package com.example.phonemarkettracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Locale;

public class CartAdapter extends BaseAdapter {

    public interface QuantityListener {
        void onDecrease(int position);
        void onIncrease(int position);
    }

    private final LayoutInflater inflater;
    private final QuantityListener listener;

    public CartAdapter(Context context, QuantityListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return SessionStore.cartCount;
    }

    @Override
    public Object getItem(int position) {
        return SessionStore.cartData[position];
    }

    @Override
    public long getItemId(int position) {
        return (int) SessionStore.cartData[position][0];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_cart_phone, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Object[] item = SessionStore.cartData[position];
        String brand = String.valueOf(item[1]);
        holder.brand.setText(brand.toUpperCase(Locale.US));
        holder.model.setText(String.valueOf(item[2]));
        holder.price.setText(money((double) item[4]));
        holder.quantity.setText(String.valueOf(item[5]));

        applyBrandColours(holder, brand);
        holder.minus.setOnClickListener(view -> listener.onDecrease(position));
        holder.plus.setOnClickListener(view -> listener.onIncrease(position));
        return convertView;
    }

    private void applyBrandColours(ViewHolder holder, String brand) {
        String lower = brand.toLowerCase(Locale.US);
        if (lower.contains("samsung")) {
            holder.tile.setBackgroundResource(R.drawable.figma_phone_tile_samsung);
            holder.body.setBackgroundResource(R.drawable.figma_phone_body_samsung);
            holder.brand.setTextColor(Color.parseColor("#00838F"));
        } else if (lower.contains("xiaomi") || lower.contains("oppo")) {
            holder.tile.setBackgroundResource(R.drawable.figma_phone_tile_xiaomi);
            holder.body.setBackgroundResource(R.drawable.figma_phone_body_xiaomi);
            holder.brand.setTextColor(Color.parseColor("#D97706"));
        } else {
            holder.tile.setBackgroundResource(R.drawable.figma_phone_tile_apple);
            holder.body.setBackgroundResource(R.drawable.figma_phone_body_apple);
            holder.brand.setTextColor(Color.parseColor("#3949AB"));
        }
    }

    private String money(double value) {
        return String.format(Locale.US, "RM %,.2f", value);
    }

    private static class ViewHolder {
        final View tile;
        final View body;
        final TextView brand;
        final TextView model;
        final TextView price;
        final TextView quantity;
        final TextView minus;
        final TextView plus;

        ViewHolder(View view) {
            tile = view.findViewById(R.id.phoneTile);
            body = view.findViewById(R.id.phoneBody);
            brand = view.findViewById(R.id.textBrand);
            model = view.findViewById(R.id.textModel);
            price = view.findViewById(R.id.textItemPrice);
            quantity = view.findViewById(R.id.textQuantity);
            minus = view.findViewById(R.id.buttonMinus);
            plus = view.findViewById(R.id.buttonPlus);
        }
    }
}
