package com.zaafoo.preorder.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.models.Cart;

import java.util.ArrayList;

/**
 * Created by SUB on 3/30/2017.
 */
public class CartAdapter extends ArrayAdapter<Cart> {


    ArrayList<Cart> mycart;
    Context context;

    public CartAdapter(Context context, int resource, ArrayList<Cart> objects) {
        super(context, resource, objects);
        mycart = objects;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_row_view, parent, false);
        }

        Cart c = getItem(position);
        TextView name = (TextView) convertView.findViewById(R.id.from_name);
        TextView price = (TextView) convertView.findViewById(R.id.plist_price_text);
        TextView amount = (TextView) convertView.findViewById(R.id.plist_amount_text);
        ImageView image = (ImageView) convertView.findViewById(R.id.list_image);


        name.setText(c.getName());
        price.setText("Rs." + c.getPrice());
        amount.setText(c.getAmount() + " Plate");
        image.setImageResource(R.drawable.dining);


        return convertView;
    }

    @Nullable
    @Override
    public Cart getItem(int position) {
        return mycart.get(position);
    }
}
