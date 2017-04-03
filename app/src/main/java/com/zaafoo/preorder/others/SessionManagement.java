package com.zaafoo.preorder.others;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.zaafoo.preorder.models.Menu;
import com.zaafoo.preorder.models.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by SUB on 3/30/2017.
 */


public class SessionManagement {

    Context context;
    SharedPreferences sp;

    public SessionManagement(Context context) {
        this.context=context;
    }

    public void addCartItems(Menu m){
        ArrayList<Menu> menus=loadCartItems();
        if(loadCartItems().isEmpty()){
            menus=new ArrayList<>();
            menus.add(m);
        }
        else
            menus.add(m);


        storeCartItems(menus);
    }


    public ArrayList<Menu> loadCartItems(){
        ArrayList<Menu> menus= Paper.book().read("cart_items",new ArrayList<Menu>());
        return menus;
    }



    public void storeCartItems(ArrayList<Menu> x){

        Paper.book().write("cart_items", x);
    }

    public void removeCartItem(Menu m){
        ArrayList<Menu> menus=loadCartItems();
        menus.remove(m);
        storeCartItems(menus);
    }

    public void removeAllCartItems(){
        Paper.book().write("cart_items", new ArrayList<Menu>());
    }


    public void insertTableList(ArrayList<Table> tableList){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String tables=gson.toJson(tableList);
        editor.putString("tableList",tables);
        editor.commit();

    }

    public ArrayList<Table> loadTables(){
        sp=context.getSharedPreferences("myPref",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        List<Table> tableList;
        if(sp.contains("tableList")){
            String json=sp.getString("tableList",null);
            Gson gson = new Gson();
            Table[] mytables=gson.fromJson(json,Table[].class);
            tableList= Arrays.asList(mytables);
            tableList=new ArrayList<>(tableList);
        }
        else
            return new ArrayList<Table>();
        return (ArrayList)tableList;

    }
}
