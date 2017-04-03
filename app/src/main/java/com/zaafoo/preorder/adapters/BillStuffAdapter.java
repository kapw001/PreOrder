package com.zaafoo.preorder.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.models.Menu;

import java.util.ArrayList;

/**
 * Created by SUB on 3/30/2017.
 */

public class BillStuffAdapter extends ArrayAdapter<Menu> {

    ArrayList<Menu> menuList;
    Context con;

    public BillStuffAdapter(Context context, int resource,ArrayList<Menu> menuList) {
        super(context, resource);
        this.menuList=menuList;
        con=context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Menu m=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(con).inflate(R.layout.bill_stuff,null);
        }

        TextView name=(TextView)convertView.findViewById(R.id.textView51);
        TextView price=(TextView)convertView.findViewById(R.id.textView56);

        name.setText(m.getName());
        price.setText("Rs."+m.getPrice());
        return convertView;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Nullable
    @Override
    public Menu getItem(int position) {
        return menuList.get(position);
    }
}
