package com.example.tikbuy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shopping_list.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_LISTS = "shopping_lists";
    private static final String COLUMN_LIST_ID = "id";
    private static final String COLUMN_LIST_NAME = "name";
    private static final String COLUMN_LIST_DESCRIPTION = "description";
    private static final String COLUMN_LIST_CREATED = "created_at";

    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ITEM_ID = "id";
    private static final String COLUMN_LIST_ID_FK = "list_id";
    private static final String COLUMN_ITEM_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PURCHASED = "purchased";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LISTS_TABLE = "CREATE TABLE " + TABLE_LISTS + "("
                + COLUMN_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LIST_NAME + " TEXT,"
                + COLUMN_LIST_DESCRIPTION + " TEXT,"
                + COLUMN_LIST_CREATED + " TEXT DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_LISTS_TABLE);

        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LIST_ID_FK + " INTEGER,"
                + COLUMN_ITEM_NAME + " TEXT,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_PURCHASED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COLUMN_LIST_ID_FK + ") REFERENCES "
                + TABLE_LISTS + "(" + COLUMN_LIST_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        onCreate(db);
    }

    public long addList(ShoppingList list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LIST_NAME, list.getName());
        values.put(COLUMN_LIST_DESCRIPTION, list.getDescription());
        long id = db.insert(TABLE_LISTS, null, values);
        db.close();
        return id;
    }

    public List<ShoppingList> getAllLists() {
        List<ShoppingList> listList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LISTS + " ORDER BY " + COLUMN_LIST_CREATED + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ShoppingList list = new ShoppingList();
                list.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIST_ID)));
                list.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_NAME)));
                list.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_DESCRIPTION)));
                list.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_CREATED)));
                listList.add(list);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listList;
    }

    public void updateList(ShoppingList list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LIST_NAME, list.getName());
        values.put(COLUMN_LIST_DESCRIPTION, list.getDescription());
        db.update(TABLE_LISTS, values, COLUMN_LIST_ID + " = ?",
                new String[]{String.valueOf(list.getId())});
        db.close();
    }

    public void deleteList(int listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS, COLUMN_LIST_ID + " = ?",
                new String[]{String.valueOf(listId)});
        db.close();
    }

    public int getItemCountForList(int listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_ITEMS
                + " WHERE " + COLUMN_LIST_ID_FK + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(listId)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public long addItem(ShoppingItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LIST_ID_FK, item.getListId());
        values.put(COLUMN_ITEM_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_PURCHASED, item.isPurchased() ? 1 : 0);
        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return id;
    }

    public List<ShoppingItem> getItemsForList(int listId) {
        List<ShoppingItem> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS
                + " WHERE " + COLUMN_LIST_ID_FK + " = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(listId)});

        if (cursor.moveToFirst()) {
            do {
                ShoppingItem item = new ShoppingItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID)));
                item.setListId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIST_ID_FK)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                item.setPurchased(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PURCHASED)) == 1);
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    public void updateItem(ShoppingItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_PURCHASED, item.isPurchased() ? 1 : 0);
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }

    public void deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(itemId)});
        db.close();
    }

    public void clearListItems(int listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_LIST_ID_FK + " = ?",
                new String[]{String.valueOf(listId)});
        db.close();
    }
}