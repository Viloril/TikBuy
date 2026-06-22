package com.example.tikbuy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {
    private List<ShoppingItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onCheckBoxClick(int position, boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView quantityTextView;
        public TextView categoryTextView;
        public CheckBox purchasedCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            categoryTextView = itemView.findViewById(R.id.item_category);
            purchasedCheckBox = itemView.findViewById(R.id.item_purchased);
        }
    }

    public ShoppingAdapter(List<ShoppingItem> items) {
        this.itemList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingItem item = itemList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.categoryTextView.setText(item.getCategory());
        holder.purchasedCheckBox.setChecked(item.isPurchased());

        if (item.isPurchased()) {
            holder.nameTextView.setAlpha(0.5f);
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags()
                    | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.nameTextView.setAlpha(1.0f);
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags()
                    & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });

        holder.purchasedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onCheckBoxClick(position, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}