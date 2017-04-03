package com.zaafoo.preorder.models;

/**
 * Created by SUB on 3/30/2017.
 */

public class Menu  {

    String id;
    String name;
    String restaurant_name;
    String price;
    String food_timing;
    String desc;
    String menu_type;
    String amount;


    public Menu(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public Menu(){

    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMenu_type() {
        return menu_type;
    }

    public void setMenu_type(String menu_type) {
        this.menu_type = menu_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFood_timing() {
        return food_timing;
    }

    public void setFood_timing(String food_timing) {
        this.food_timing = food_timing;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
