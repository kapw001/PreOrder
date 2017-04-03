package com.zaafoo.preorder.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.models.Review;

import java.util.ArrayList;

/**
 * Created by SUB on 3/29/2017.
 */

public class ReviewAdapter extends ArrayAdapter<Review> {


    Context context;
    int resource;
    ArrayList<Review> objects;

    public ReviewAdapter(Context context, int resource, ArrayList<Review> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.review_single_item,null);
        }

        TextView user=(TextView)convertView.findViewById(R.id.textView7);
        RatingBar rating=(RatingBar) convertView.findViewById(R.id.ratingBar);
        TextView title=(TextView)convertView.findViewById(R.id.textView8);
        TextView desc=(TextView)convertView.findViewById(R.id.textView9);

        Review r=getItem(position);
        user.setText(r.getUser().toUpperCase());
        rating.setRating(Float.parseFloat(r.getRating()));
        title.setText(r.getTitle().toUpperCase());
        desc.setText(r.getDescription());

        return convertView;
    }

    @Nullable
    @Override
    public Review getItem(int position) {
        return objects.get(position);
    }
}