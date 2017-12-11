package com.example.bohan.flyingpig.entity;

/**
 * Created by 18910931590163.com on 12/4/17.
 */

public class food_price {

    public double getBurger_price() {
        return burger_price;
    }

    public double getChicken_price() {
        return chicken_price;
    }

    public double getFrenchfries_price() {
        return frenchfries_price;
    }

    public double getOnionring_price() {
        return onionring_price;
    }

    double burger_price;//bu7.15 ch7.8 ff2.6 or3.25
    double chicken_price;
    double frenchfries_price;
    double onionring_price;
    public food_price(){
        this.burger_price=7.15;
        this.chicken_price=7.8;
        this.frenchfries_price=2.6;
        this.onionring_price=3.25;
    }
}
