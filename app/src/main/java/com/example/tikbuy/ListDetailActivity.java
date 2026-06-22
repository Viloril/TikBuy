package com.example.tikbuy;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ListDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShoppingAdapter adapter;
    private List<ShoppingItem> shoppingItems;
    private DatabaseHelper dbHelper;
    private Button addItemButton;
    private Button deleteListButton;
    private TextView listTitle;
    private TextView listSubtitle;
    private int listId;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        dbHelper = new DatabaseHelper(this);

        listId = getIntent().getIntExtra("list_id", -1);
        listName = getIntent().getStringExtra("list_name");

        if (listId == -1) {
            finish();
            return;
        }

        shoppingItems = new ArrayList<>();

        listTitle = findViewById(R.id.list_title);
        listSubtitle = findViewById(R.id.list_subtitle);
        recyclerView = findViewById(R.id.recycler_view);
        addItemButton = findViewById(R.id.add_button);
        deleteListButton = findViewById(R.id.delete_list_button);

        listTitle.setText(listName);

        int totalItems = dbHelper.getItemCountForList(listId);
        if (listSubtitle != null) {
            listSubtitle.setText("Товаров в списке: " + totalItems);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShoppingAdapter(shoppingItems);
        recyclerView.setAdapter(adapter);

        loadItems();

        addItemButton.setOnClickListener(v -> showAddItemDialog());

        deleteListButton.setOnClickListener(v -> confirmDeleteList());

        adapter.setOnItemClickListener(new ShoppingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showEditItemDialog(position);
            }

            @Override
            public void onCheckBoxClick(int position, boolean isChecked) {
                ShoppingItem item = shoppingItems.get(position);
                item.setPurchased(isChecked);
                dbHelper.updateItem(item);
                loadItems();
                updateDeleteButtonText();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
        updateDeleteButtonText();
    }

    private void loadItems() {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(this);
        }
        shoppingItems.clear();
        shoppingItems.addAll(dbHelper.getItemsForList(listId));
        adapter.notifyDataSetChanged();
        updateDeleteButtonText();

        if (listSubtitle != null) {
            listSubtitle.setText("Товаров в списке: " + shoppingItems.size());
        }
    }

    private void updateDeleteButtonText() {
        int purchasedCount = 0;
        for (ShoppingItem item : shoppingItems) {
            if (item.isPurchased()) {
                purchasedCount++;
            }
        }

        if (shoppingItems.size() > 0 && purchasedCount == shoppingItems.size()) {
            deleteListButton.setText("Завершить закупку");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                deleteListButton.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                );
            } else {
                deleteListButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            }
        } else {
            deleteListButton.setText("Удалить список");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                deleteListButton.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                );
            } else {
                deleteListButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            }
        }
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.edit_name);
        EditText quantityEditText = dialogView.findViewById(R.id.edit_quantity);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        builder.setTitle("Добавить товар в " + listName)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    if (name.isEmpty()) {
                        nameEditText.setError("Введите название товара");
                        return;
                    }

                    String quantityStr = quantityEditText.getText().toString().trim();
                    if (quantityStr.isEmpty()) {
                        quantityEditText.setError("Введите количество");
                        return;
                    }

                    int quantity = Integer.parseInt(quantityStr);
                    String category = categorySpinner.getSelectedItem().toString();

                    ShoppingItem item = new ShoppingItem(listId, name, quantity, category);
                    dbHelper.addItem(item);
                    loadItems();
                    Toast.makeText(this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null);

        builder.create().show();
    }

    private void showEditItemDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.edit_name);
        EditText quantityEditText = dialogView.findViewById(R.id.edit_quantity);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        ShoppingItem item = shoppingItems.get(position);
        nameEditText.setText(item.getName());
        quantityEditText.setText(String.valueOf(item.getQuantity()));

        String[] categories = getResources().getStringArray(R.array.categories);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(item.getCategory())) {
                categorySpinner.setSelection(i);
                break;
            }
        }

        builder.setTitle("Редактировать товар")
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    if (name.isEmpty()) {
                        nameEditText.setError("Введите название товара");
                        return;
                    }

                    String quantityStr = quantityEditText.getText().toString().trim();
                    if (quantityStr.isEmpty()) {
                        quantityEditText.setError("Введите количество");
                        return;
                    }

                    item.setName(name);
                    item.setQuantity(Integer.parseInt(quantityStr));
                    item.setCategory(categorySpinner.getSelectedItem().toString());
                    dbHelper.updateItem(item);
                    loadItems();
                    Toast.makeText(this, "Товар обновлен", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Удалить", (dialog, which) -> {
                    dbHelper.deleteItem(item.getId());
                    loadItems();
                    Toast.makeText(this, "Товар удален", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null);

        builder.create().show();
    }

    private void confirmDeleteList() {
        int purchasedCount = 0;
        for (ShoppingItem item : shoppingItems) {
            if (item.isPurchased()) {
                purchasedCount++;
            }
        }

        String title;
        String message;
        String positiveButtonText;

        if (shoppingItems.size() > 0 && purchasedCount == shoppingItems.size()) {
            title = "Завершить закупку?";
            message = "Все товары в списке куплены! Хотите завершить закупку и удалить список?";
            positiveButtonText = "Завершить";
        } else {
            title = "Удалить список?";
            message = "Вы уверены, что хотите удалить список \"" + listName + "\"?\nВсе товары будут удалены.";
            positiveButtonText = "Удалить";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, (dialog, which) -> {
                    dbHelper.deleteList(listId);
                    Toast.makeText(this, "Список удален", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 300);
                })
                .setNegativeButton("Отмена", null);

        if (!(shoppingItems.size() > 0 && purchasedCount == shoppingItems.size())) {
            builder.setNeutralButton("Очистить товары", (dialog, which) -> {
                dbHelper.clearListItems(listId);
                loadItems();
                Toast.makeText(this, "Товары очищены", Toast.LENGTH_SHORT).show();
            });
        }

        builder.create().show();
    }

    public void goBack(View view) {
        finish();
    }
}