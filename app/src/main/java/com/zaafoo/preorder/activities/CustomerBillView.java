package com.zaafoo.preorder.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.adapters.BillStuffAdapter;
import com.zaafoo.preorder.models.Menu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

public class CustomerBillView extends AppCompatActivity {

    Button cancel,viewTables;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bill_view);

        cancel=(Button)findViewById(R.id.cancel_booking);
        viewTables=(Button)findViewById(R.id.go_to_tables);
        ListView lv=(ListView)findViewById(R.id.bill_list);
        Paper.init(this);
        ArrayList<Menu> menuList= Paper.book().read("bill_data",new ArrayList<Menu>());
        BillStuffAdapter adapter=new BillStuffAdapter(this,R.layout.bill_stuff,menuList);
        lv.setAdapter(adapter);
        setTitle("Booking Details");

        final String booking_id=Paper.book().read("booking_id");
        final String token=Paper.book().read("token");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    cancelUserBooking(booking_id,token);
            }
        });
        viewTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerBillView.this,CustomerTableView.class));
            }
        });
    }

    private void cancelUserBooking(String booking_id, String token) {

        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Cancelling..");
        pd.setCancelable(false);
        pd.show();
        AndroidNetworking.post("http://zaafoo.com/bookingcancelrest/")
                .addBodyParameter("bid", booking_id)
                .addHeaders("Authorization","Token "+token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pd.dismiss();
                        try {
                            String status=response.getString("Status");
                            if(status.equalsIgnoreCase("failed"))
                                Toast.makeText(CustomerBillView.this,"Can't Be Cancelled",Toast.LENGTH_LONG).show();
                            else if(status.equalsIgnoreCase("success"))
                                Toast.makeText(CustomerBillView.this,"Successfully Cancelled",Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            pd.dismiss();
                            Toast.makeText(CustomerBillView.this,e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                        Toast.makeText(CustomerBillView.this,"Already Cancelled",Toast.LENGTH_LONG).show();
                    }
                });
    }
}
