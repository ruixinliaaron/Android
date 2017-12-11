package com.example.bohan.flyingpig.entity;

/**
 * Created by bohan on 12/6/17.
 */

public class Chef {
    private String chefEmail;
    private String chefPassword;

    public Chef(){}

    public String getPassword() {
        return this.chefPassword;
    }
    public String getEmail() {
        return this.chefEmail;
    }

    public void setEmail(String email) {
        this.chefEmail = email;
    }
    public void setPassword(String password) {
        this.chefPassword = password;
    }
}
