package com.zaafoo.preorder.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.adapters.BillStuffAdapter;
import com.zaafoo.preorder.models.Menu;

import java.util.ArrayList;

import io.paperdb.Paper;

public class CustomerBillView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bill_view);

        ListView lv=(ListView)findViewById(R.id.bill_list);
        Paper.init(this);
        ArrayList<Menu> menuList= Paper.book().read("bill_data",new ArrayList<Menu>());
        BillStuffAdapter adapter=new BillStuffAdapter(this,R.layout.bill_stuff,menuList);
        lv.setAdapter(adapter);
    }
}
