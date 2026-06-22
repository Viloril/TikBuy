package com.example.tikbuy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private List<ShoppingList> shoppingLists;
    private DatabaseHelper dbHelper;
    private Button addListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        shoppingLists = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        addListButton = findViewById(R.id.add_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListAdapter(shoppingLists, dbHelper);
        recyclerView.setAdapter(adapter);

        loadLists();

        addListButton.setOnClickListener(v -> showAddListDialog());

        adapter.setOnListClickListener(new ListAdapter.OnListClickListener() {
            @Override
            public void onListClick(int position) {
                ShoppingList list = shoppingLists.get(position);
                Intent intent = new Intent(MainActivity.this, ListDetailActivity.class);
                intent.putExtra("list_id", list.getId());
                intent.putExtra("list_name", list.getName());
                startActivity(intent);
            }

            @Override
            public void onListLongClick(int position) {
                showListOptionsDialog(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLists();
    }

    private void loadLists() {
        shoppingLists.clear();
        shoppingLists.addAll(dbHelper.getAllLists());
        adapter.notifyDataSetChanged();

        if (shoppingLists.isEmpty()) {
            Toast.makeText(this, "Создайте первый список покупок!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_list, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.edit_list_name);
        EditText descriptionEditText = dialogView.findViewById(R.id.edit_list_description);

        builder.setTitle("Новый список покупок")
                .setPositiveButton("Создать", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();

                    if (name.isEmpty()) {
                        nameEditText.setError("Введите название списка");
                        return;
                    }

                    ShoppingList list = new ShoppingList(name, description);
                    dbHelper.addList(list);
                    loadLists();
                    Toast.makeText(this, "Список создан", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null);

        builder.create().show();
    }

    private void showListOptionsDialog(int position) {
        ShoppingList list = shoppingLists.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] options = {"Редактировать", "Удалить список", "Очистить товары"};

        builder.setTitle(list.getName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showEditListDialog(position);
                            break;
                        case 1:
                            confirmDeleteList(position);
                            break;
                        case 2:
                            confirmClearList(position);
                            break;
                    }
                })
                .setNegativeButton("Отмена", null);

        builder.create().show();
    }

    private void showEditListDialog(int position) {
        ShoppingList list = shoppingLists.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_list, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.edit_list_name);
        EditText descriptionEditText = dialogView.findViewById(R.id.edit_list_description);

        nameEditText.setText(list.getName());
        descriptionEditText.setText(list.getDescription());

        builder.setTitle("Редактировать список")
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();

                    if (name.isEmpty()) {
                        nameEditText.setError("Введите название списка");
                        return;
                    }

                    list.setName(name);
                    list.setDescription(description);
                    dbHelper.updateList(list);
                    loadLists();
                    Toast.makeText(this, "Список обновлен", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null);

        builder.create().show();
    }

    private void confirmDeleteList(int position) {
        ShoppingList list = shoppingLists.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить список?")
                .setMessage("Все товары в этом списке будут удалены. Продолжить?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    dbHelper.deleteList(list.getId());
                    loadLists();
                    Toast.makeText(this, "Список удален", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null);
        builder.create().show();
    }

    private void confirmClearList(int position) {
        ShoppingList list = shoppingLists.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Очистить список?")
                .setMessage("Все товары в этом списке будут удалены. Продолжить?")
                .setPositiveButton("Очистить", (dialog, which) -> {
                    dbHelper.clearListItems(list.getId());
                    Toast.makeText(this, "Список очищен", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null);
        builder.create().show();
    }
}