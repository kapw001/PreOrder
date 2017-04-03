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
import com.zaafoo.preorder.models.Bill;

import java.util.ArrayList;

/**
 * Created by SUB on 3/30/2017.
 */

public class BillListAdapter extends ArrayAdapter<Bill> {

    ArrayList<Bill> bill_data;
    Context con;
    public BillListAdapter(Context context, int resource, ArrayList<Bill> objects) {
        super(context, resource, objects);
        bill_data=objects;
        con=context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(con).inflate(R.layout.bill_final_list,null);
        }
        TextView name=(TextView)convertView.findViewById(R.id.textView62);
        TextView quantity=(TextView)convertView.findViewById(R.id.textView63);
        TextView price=(TextView)convertView.findViewById(R.id.textView65);
        TextView total=(TextView)convertView.findViewById(R.id.textView66);

        Bill b=getItem(position);
        name.setText(b.getName());
        quantity.setText(b.getQuantity());
        price.setText(b.getPrice());
        total.setText(b.getTotal());

        return convertView;
    }

    @Override
    public int getCount() {
        return bill_data.size();
    }

    @Nullable
    @Override
    public Bill getItem(int position) {
        return bill_data.get(position);
    }
}
