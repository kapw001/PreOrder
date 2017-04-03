package com.zaafoo.preorder.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.LoginActivity;
import com.zaafoo.preorder.activities.MainActivity;
import com.zaafoo.preorder.activities.SplashActivity;
import com.zaafoo.preorder.models.Restaurant;
import com.zaafoo.preorder.others.RestaurantSection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends Fragment {

    ArrayList<String> localityList;
    ArrayList<Restaurant> restaurantList;
    ArrayList<ArrayList<Restaurant>> allRestList;
    SectionedRecyclerViewAdapter sectionAdapter;

    String rest_list;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_restaurant, container, false);
        rest_list= MainActivity.returnRestList();
        localityList=new ArrayList<>();
        restaurantList=new ArrayList<>();
        allRestList=new ArrayList<>();
        parseJSONData(rest_list);
        sectionAdapter = new SectionedRecyclerViewAdapter();
        for(int i=0;i<localityList.size();i++){
            sectionAdapter.addSection(new RestaurantSection(localityList.get(i),allRestList.get(i),getActivity(),allRestList,localityList));
        }
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(sectionAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(sectionAdapter);
        return rootView;
    }

    public void parseJSONData(String rest_list){
        try {
            JSONObject top=new JSONObject(rest_list);
            JSONArray  array=top.getJSONArray("locationaandrest");
            JSONObject localityObject;
            for(int i=0;i<array.length();i++) {
                localityObject = array.getJSONObject(i);
                String locality_name = localityObject.getString("locality_name");
                localityList.add(locality_name);
                JSONArray array1 = localityObject.getJSONArray("rest");
                for (int j = 0; j < array1.length(); j++) {
                    JSONObject rest_object = array1.getJSONObject(j);
                    Restaurant r = new Restaurant();
                    r.setId(rest_object.getString("id"));
                    r.setName(rest_object.getString("rname"));
                    r.setAbout(rest_object.getString("description"));
                    r.setAddress(rest_object.getString("street_address"));
                    r.setImage_url(rest_object.getString("imagecontent"));
                    r.setRating(rest_object.getString("rating"));
                    restaurantList.add(r);
                }
                allRestList.add(restaurantList);
                restaurantList=new ArrayList<>();
            }

            } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
