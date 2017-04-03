package com.zaafoo.preorder.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.adapters.CartAdapter;
import com.zaafoo.preorder.models.Cart;
import com.zaafoo.preorder.models.Menu;
import com.zaafoo.preorder.models.Table;
import com.zaafoo.preorder.others.SessionManagement;

import java.util.ArrayList;
import java.util.Iterator;

import io.paperdb.Paper;

public class CartActivity extends AppCompatActivity {


    Cart c;
    ArrayList<Menu> menuItems;
    ArrayList<Cart> mycart,cartList;
    ListView lv;
    CartAdapter adapter;
    int pos;
    String rest_data;
    TextView tables,persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        lv=(ListView)findViewById(R.id.cart_list);
        Button payment=(Button)findViewById(R.id.payment);
        tables=(TextView)findViewById(R.id.no_of_tables);
        persons=(TextView)findViewById(R.id.no_of_persons);
        mycart=new ArrayList<>();
        Intent intent=getIntent();
        rest_data=intent.getExtras().getString("rest_data");
        menuItems=new SessionManagement(this).loadCartItems();
        int noOfTables=0;

        ArrayList<Table> myTables=new SessionManagement(this).loadTables();
        for(Table t:myTables){
            if(t.isSelected()) {
                noOfTables++;
            }
        }
        int per=Paper.book().read("persons");
        persons.setText(per+"");
        tables.setText(String.valueOf(noOfTables));

        for(int i=0;i<menuItems.size();i++){
            c=new Cart();
            c.setName(menuItems.get(i).getName());
            c.setPrice(menuItems.get(i).getPrice());
            c.setAmount(menuItems.get(i).getAmount());
            c.setId(menuItems.get(i).getId());
            mycart.add(c);
        }

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i=new Intent(CartActivity.this,BillActivity.class);
                    i.putExtra("rest_data",rest_data);
                    startActivity(i);
                    finish();
            }
        });


        if(mycart!=null) {

            adapter = new CartAdapter(this, R.layout.product_row_view, mycart);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(CartActivity.this);
                    alert.setCancelable(false);
                    alert.setTitle("Remove Item");
                    alert.setMessage("Do You Want To Remove This Item ?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pos=position;
                            updateCartData();


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
            });
        }
        else
            Toast.makeText(CartActivity.this,"Sorry..Cart Is Empty",Toast.LENGTH_SHORT).show();
    }



    private void updateCartData() {

        Cart item=(Cart)lv.getItemAtPosition(pos);
        removeMenuItem(item);
        mycart.remove(item);
        adapter.notifyDataSetChanged();

    }


    private void removeMenuItem(Cart item) {

        Iterator<Menu> it=menuItems.iterator();
        while(it.hasNext()){

            if(it.next().getId().equalsIgnoreCase(item.getId())){
                it.remove();
                Paper.book().write("cart_items",menuItems);
            }

        }
    }

}
