package com.zaafoo.preorder.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.AboutRestaurant;
import com.zaafoo.preorder.adapters.ReviewAdapter;
import com.zaafoo.preorder.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RestaurantReviewFragment extends Fragment {


    ArrayList<Review> reviewArrayList;
    ListView reviewList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_restaurant_review, container, false);
        String rest_data= AboutRestaurant.giveRestDatatoFragments();
        reviewArrayList=new ArrayList<>();
        reviewList=(ListView)v.findViewById(R.id.review);
        try {
            JSONObject response=new JSONObject(rest_data);
            JSONObject object;
            JSONArray array = response.getJSONArray("feedbacks");
            for(int i=0;i<array.length();i++){
                Review r=new Review();
                object=array.getJSONObject(i);
                String user=object.getString("User");
                String title=object.getString("title");
                String desc=object.getString("description");
                String rating=object.getString("rating");
                r.setUser(user);
                r.setTitle(title);
                r.setDescription(desc);
                r.setRating(rating);
                reviewArrayList.add(r);
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
        ReviewAdapter adapter=new ReviewAdapter(getActivity(),R.layout.review_single_item,reviewArrayList);
        reviewList.setAdapter(adapter);

        return v;
    }

}
