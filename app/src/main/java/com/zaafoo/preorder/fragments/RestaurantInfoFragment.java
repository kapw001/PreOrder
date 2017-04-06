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
                createInformationDialog();
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

    private void createInformationDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Info");
        builder.setMessage("1. Use Buttons On The Top To Manipulate Time,Date & Guests.\n" +
                "2. Please Book Between 9 a.m to 10 p.m to Avoid Booking Cancellation.\n" +
                "3. Reserved Tables Are In Black whereas Unreserved are in Blue\n" +
                "4. Book Tables & Food Atlest 30 minutes post current time\n" +
                "5. Default Number of Guests: 1\n");
        builder.setCancelable(false);
        builder.setNeutralButton("Ok,Let Me Book", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i=new Intent(getActivity(),TableLayout.class);
                i.putExtra("rest_data",rest_data);
                getActivity().startActivity(i);
            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
