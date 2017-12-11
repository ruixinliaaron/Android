package com.example.bohan.flyingpig.entity;

/**
 * Created by 18910931590163.com on 11/21/17.
 */

public class StoreInfo {
    protected String Id;
    protected String name;
    protected boolean isChoosed;

    public StoreInfo(String id, String name) {
        Id = id;
        this.name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean isChoosed) {
        this.isChoosed = isChoosed;
    }
}
