package com.zaafoo.preorder.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.AboutRestaurant;
import com.zaafoo.preorder.activities.TableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantInfoFragment extends Fragment {


    TextView rest_name,rest_about,rest_address;
    Button goToBookTable;
    String rest_data;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_restaurant_info, container, false);
        rest_data= AboutRestaurant.giveRestDatatoFragments();
        rest_name=(TextView)v.findViewById(R.id.textView);
        rest_about=(TextView)v.findViewById(R.id.textView3);
        rest_address=(TextView)v.findViewById(R.id.textView5);
        goToBookTable=(Button)v.findViewById(R.id.button);
        goToBookTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),TableLayout.class);
                i.putExtra("rest_data",rest_data);
                getActivity().startActivity(i);
            }
        });
        populateRestInfo(rest_data);

        return v;
    }

    private void populateRestInfo(String rest_data) {

        try {
            JSONObject object=new JSONObject(rest_data);
            JSONArray array=object.getJSONArray("res_details");
            object=array.getJSONObject(0);
            String name=object.getString("name");
            String about=object.getString("description");
            String address=object.getString("street_address");
            rest_name.setText(name);
            rest_about.setText(about);
            rest_address.setText(address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
