package com.example.bohan.flyingpig.entity;

/**
 * Created by 18910931590163.com on 11/27/17.
 */

public class OrderItem {
    private String productName;
    private int quantity;
    private double actualPrice;

    //default constructor:
    public OrderItem() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }
}
