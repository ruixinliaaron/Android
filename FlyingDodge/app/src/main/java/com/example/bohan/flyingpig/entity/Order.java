package com.example.bohan.flyingpig.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 18910931590163.com on 11/19/17.
 */

public class Order {
    private int orderID;
    private List<OrderItem> orderItemList;
    private Customer customer;
    private Date creationDate;
    private String status;

    public Order() {
        orderItemList = new ArrayList<>();
        creationDate = new Date();
    }


    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrderID() {
        return orderID;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getStatus() {
        return status;
    }

}
