package com.example.tikbuy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<ShoppingList> listList;
    private OnListClickListener listener;
    private DatabaseHelper dbHelper;

    public interface OnListClickListener {
        void onListClick(int position);
        void onListLongClick(int position);
    }

    public void setOnListClickListener(OnListClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView itemCountTextView;
        public TextView progressTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.list_name);
            descriptionTextView = itemView.findViewById(R.id.list_description);
            itemCountTextView = itemView.findViewById(R.id.list_item_count);
            progressTextView = itemView.findViewById(R.id.list_progress_text);
        }
    }

    public ListAdapter(List<ShoppingList> lists, DatabaseHelper dbHelper) {
        this.listList = lists;
        this.dbHelper = dbHelper;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingList list = listList.get(position);
        holder.nameTextView.setText(list.getName());
        holder.descriptionTextView.setText(list.getDescription());

        int totalItems = dbHelper.getItemCountForList(list.getId());
        List<ShoppingItem> items = dbHelper.getItemsForList(list.getId());
        int purchasedCount = 0;
        for (ShoppingItem item : items) {
            if (item.isPurchased()) {
                purchasedCount++;
            }
        }

        holder.itemCountTextView.setText(totalItems + " товаров");
        holder.progressTextView.setText(purchasedCount + "/" + totalItems + " куплено");

        if (totalItems > 0 && purchasedCount == totalItems) {
            holder.itemCountTextView.setBackgroundColor(
                    holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_light)
            );
            holder.itemCountTextView.setTextColor(
                    holder.itemView.getContext().getResources().getColor(android.R.color.white)
            );
        } else {
            holder.itemCountTextView.setBackgroundColor(
                    holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_light)
            );
            holder.itemCountTextView.setTextColor(
                    holder.itemView.getContext().getResources().getColor(android.R.color.black)
            );
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onListLongClick(position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listList.size();
    }
}