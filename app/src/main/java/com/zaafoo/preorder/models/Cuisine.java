package com.zaafoo.preorder.models;

import java.util.ArrayList;

/**
 * Created by SUB on 3/30/2017.
 */

public class Cuisine {

    String name;
    ArrayList<Menu> menus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Menu> getMenus() {
        return menus;
    }

    public void setMenus(ArrayList<Menu> menus) {
        this.menus = menus;
    }
}
