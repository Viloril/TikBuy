package com.example.tikbuy;

public class ShoppingItem {
    private int id;
    private int listId;
    private String name;
    private int quantity;
    private String category;
    private boolean isPurchased;

    public ShoppingItem() {}

    public ShoppingItem(int listId, String name, int quantity, String category) {
        this.listId = listId;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.isPurchased = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isPurchased() { return isPurchased; }
    public void setPurchased(boolean purchased) { isPurchased = purchased; }
}