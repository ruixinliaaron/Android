package com.example.bohan.flyingpig.entity;

/**
 * Created by 18910931590163.com on 11/27/17.
 */

public class Customer {
    private String name;
    private String email;

    //default constructor:
    public Customer() {
    }

    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
