package com.zaafoo.preorder.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.adapters.MenuExpandableAdapter;
import com.zaafoo.preorder.models.Cuisine;
import com.zaafoo.preorder.models.Menu;
import com.zaafoo.preorder.others.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

public class MenuActivity extends AppCompatActivity {

    String rest_data;
    JSONArray menu_array;
    JSONObject menu_object;
    ArrayList<Cuisine> cuisineList;
    ArrayList<String> cuisineNameList;
    ArrayList<Menu> menuList;
    MenuExpandableAdapter myadapter;
    ExpandableListView listView;
    ArrayList<Menu> menuInCart;
    Menu myMenu;
    Button skip_menu,goToCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent i=getIntent();
        rest_data=i.getExtras().getString("rest_data");
        listView=(ExpandableListView)findViewById(R.id.menu_expandable);
        skip_menu=(Button)findViewById(R.id.skip_menu);
        goToCart=(Button)findViewById(R.id.go_to_cart);
        cuisineList=new ArrayList<>();
        menuList=new ArrayList<>();
        cuisineNameList=new ArrayList<>();
        menuInCart=new ArrayList<>();
        Paper.init(this);
        setTitle("Menus");

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                myMenu= (Menu) parent.getExpandableListAdapter().getChild(groupPosition,childPosition);
                showAddToCartDialog();
                return true;
            }
        });

        skip_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MenuActivity.this,BillActivity.class);
                i.putExtra("rest_data",rest_data);
                startActivity(i);
                Paper.book().write("cart_items",new ArrayList<Menu>());
            }
        });

        goToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MenuActivity.this,CartActivity.class);
                i.putExtra("rest_data",rest_data);
                startActivity(i);
            }
        });
        parseMenudata(rest_data);
    }

    private void showAddToCartDialog() {

        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Zaafoo");
        alert.setMessage("Wana Add this Item To Cart?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSelectAmountDialog();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog=alert.create();
        dialog.show();
    }

    private void showSelectAmountDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.select_menu_amount, null);

        ArrayList<String> itemNo=new ArrayList<String>();
        for(int i=1;i<=10;i++){
            itemNo.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,itemNo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner=(Spinner)mView.findViewById(R.id.spinner4);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String amount=parent.getItemAtPosition(position).toString();
                myMenu.setAmount(amount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setView(mView);

        alert.setPositiveButton("Add To Cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                boolean present=false;
                ArrayList<Menu> xyz=new SessionManagement(MenuActivity.this).loadCartItems();
                for(Menu m:xyz){
                    if(m.getId().equalsIgnoreCase(myMenu.getId()))
                        present=true;
                }

                if(present){
                    Toast.makeText(MenuActivity.this,"Item Already Present In Cart",Toast.LENGTH_SHORT).show();
                }
                else{
                    new SessionManagement(MenuActivity.this).addCartItems(myMenu);
                    Toast.makeText(MenuActivity.this,"Item Added To Cart",Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=alert.create();
        dialog.show();
    }




    // Parse Menu Data
    private void parseMenudata(String rest_data) {
        try {
            JSONObject response=new JSONObject(rest_data);
            menu_array=response.getJSONArray("menus");

            // CUISINE LOOP
            for(int j=0;j<menu_array.length();j++){
                menu_object=menu_array.getJSONObject(j);
                if(!cuisineNameList.contains(menu_object.getString("cuisine")))
                    cuisineNameList.add(menu_object.getString("cuisine"));
            }

            for(String key:cuisineNameList)
            {
                for(int i=0;i<menu_array.length();i++){
                    menu_object=menu_array.getJSONObject(i);
                    if(menu_object.getString("cuisine").equalsIgnoreCase(key)) {
                        Menu m=new Menu();
                        String m_name = menu_object.getString("name");
                        String m_price = menu_object.getString("price");
                        String m_id = menu_object.getString("id");
                        m.setId(m_id);
                        m.setName(m_name);
                        m.setPrice(m_price);
                        menuList.add(m);

                    }
                }
                Cuisine c=new Cuisine();
                c.setName(key);
                c.setMenus(menuList);
                cuisineList.add(c);
                menuList=new ArrayList<>();

            }
            myadapter=new MenuExpandableAdapter(cuisineList,this);
            listView.setAdapter(myadapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Paper.book().write("cart_items",new ArrayList<Menu>());
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        MenuItem cart_item = menu.findItem(R.id.cart);
        cart_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i=new Intent(MenuActivity.this,CartActivity.class);
                i.putExtra("rest_data",rest_data);
                startActivity(i);
                return true;
            }
        });
        return true;
    }

}

