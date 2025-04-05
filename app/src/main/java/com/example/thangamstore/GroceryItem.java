package com.example.thangamstore;


public class GroceryItem {
    private String name;
    private String price;
    private int imageResId;
    private int id;
    private int stock;


    private int quantity;

    public GroceryItem(String name, String price, int imageResId, int id , int quantity, int stock) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.id = id;
        this.quantity=quantity;
        this.stock= stock;
    }


    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getId() {
        return id;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }


    public void setAddedToCart(boolean newState) {

    }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }

}
