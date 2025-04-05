package com.example.thangamstore;

import java.util.List;

public class Order {
    private String orderId;

    private String address;
    private String phone;
    private String paymentMethod;
    private List<GroceryItem> cartItems;

    private String date;
    private String time;

    public Order() {}

    public Order(String orderId,String address, String phone, String paymentMethod, List<GroceryItem> cartItems, String date, String time) {
        this.orderId = orderId;
        this.address = address;
        this.phone = phone;
        this.paymentMethod = paymentMethod;
        this.cartItems = cartItems;
        this.date = date;
        this.time = time;

    }


    public String getDate(){
        return date;
    }
    public String getTime(){
        return time;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<GroceryItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<GroceryItem> cartItems) {
        this.cartItems = cartItems;
    }
}
