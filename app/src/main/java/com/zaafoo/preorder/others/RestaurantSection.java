package com.zaafoo.preorder.others;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.squareup.picasso.Picasso;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.AboutRestaurant;
import com.zaafoo.preorder.models.Restaurant;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import io.paperdb.Paper;

/**
 * Created by SUB on 3/29/2017.
 */

public class RestaurantSection extends StatelessSection {

    String title;
    ArrayList<Restaurant> restaurantList;
    Context context;
    ArrayList<ArrayList<Restaurant>> allRestList;
    ArrayList<String> localityList;
    int layoutpos=0;
    ProgressDialog pd;
    public RestaurantSection(String title, ArrayList<Restaurant> restaurantList, Context context, ArrayList<ArrayList<Restaurant>> allRestList, ArrayList<String> localityList) {
        super(R.layout.section_ex5_header, R.layout.restaurant_single_view);
        this.title=title;
        this.restaurantList=restaurantList;
        this.context=context;
        this.allRestList=allRestList;
        this.localityList=localityList;
    }



    @Override
    public int getContentItemsTotal() {
        return restaurantList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        itemHolder.rest_name.setText(restaurantList.get(position).getName());
        itemHolder.rest_address.setText(restaurantList.get(position).getAddress());
        Picasso.with(context).load("http://zaafoo.com/"+restaurantList.get(position).getImage_url()).into(itemHolder.rest_logo);
        String rate=restaurantList.get(position).getRating();
        if(rate.equals("null"))
            itemHolder.rest_rating.setRating(0);
        else
            itemHolder.rest_rating.setRating(Float.parseFloat(rate));
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        headerHolder.locality.setText(title);
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView locality;

        public HeaderViewHolder(View view) {
            super(view);
            locality = (TextView) view.findViewById(R.id.textView10);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView rest_logo;
        TextView rest_name;
        TextView rest_address;
        RatingBar rest_rating;


        public ItemViewHolder(View view) {
            super(view);
            rest_logo = (ImageView) view.findViewById(R.id.imageView);
            rest_name = (TextView) view.findViewById(R.id.rest_name);
            rest_address = (TextView) view.findViewById(R.id.rest_address);
            rest_rating=(RatingBar)view.findViewById(R.id.ratingBar2);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int titlepos;
                    for(String t:localityList){
                        if(title.equalsIgnoreCase(t))
                        {
                            titlepos=localityList.indexOf(title);
                            layoutpos=getLayoutPosition();
                            layoutpos-=(titlepos+1);
                            for(int i=0;i<titlepos;i++){
                                layoutpos-=allRestList.get(i).size();
                            }
                        }
                    }
                            getRestaurantInfo(restaurantList.get(layoutpos).getId());
                            Paper.book().write("restid",restaurantList.get(layoutpos).getId());

                }
            });
        }

    }

    public void getRestaurantInfo(String restid){

        pd=new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Loading Restaurant.. ");
        pd.show();
        AndroidNetworking.post("http://zaafoo.com/cusresview/")
                .addBodyParameter("restid", restid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Intent i=new Intent(context, AboutRestaurant.class);
                        i.putExtra("rest_data",response);
                        context.startActivity(i);
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                    }
                });
    }

}

