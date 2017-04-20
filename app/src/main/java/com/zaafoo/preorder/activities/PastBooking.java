package com.zaafoo.preorder.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.adapters.PastBookingAdapter;
import com.zaafoo.preorder.models.Menu;
import com.zaafoo.preorder.models.Past;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.paperdb.Paper;

public class PastBooking extends AppCompatActivity {

    ArrayList<Past> bookings;
    RecyclerView recyclerView;
    ProgressDialog pd;
    String restaurant_name;
    ArrayList<String> booking_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_booking);
        Paper.init(this);
        bookings=new ArrayList<>();
        booking_data=new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.booking_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setTitle("Bookings");
        getPastBookings();

    }


    public void getPastBookings() {

        String token= Paper.book().read("token");
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading Past Bookings");
        pd.setTitle("Zaafoo");
        pd.show();
        AndroidNetworking.post("http://zaafoo.com/mytransactionsview/")
                .addHeaders("Authorization","Token "+token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray array=response.getJSONArray("bookings");
                            JSONObject xyz;
                            for(int k=0;k<array.length();k++){
                                xyz=array.getJSONObject(k);
                                booking_data.add(xyz.toString());
                            }
                            for(int i=0;i<array.length();i++){
                                JSONObject obj=array.getJSONObject(i);
                                JSONArray arr1=obj.getJSONArray("bill");
                                JSONObject obj2=arr1.getJSONObject(0);
                                String booking_success=obj2.getString("booking_success");
                                String dateTime=obj2.getString("start_time");
                                int z=0;
                                String date=null;
                                String time=null;
                                SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss");
                                Date d;
                                Calendar c=Calendar.getInstance();
                                for(String x:dateTime.split("T")){
                                    if(z==0) {
                                        date = x;
                                        z++;
                                    }
                                    else {
                                        time = x;
                                        time=time.replace("Z","");
                                        d=sdf.parse(time);
                                        c.setTime(d);
                                        c.add(Calendar.HOUR_OF_DAY,5);
                                        c.add(Calendar.MINUTE,30);
                                        String m=sdf.format(c.getTime());
                                        time=m;

                                    }

                                }
                                // Get Menu Code
                                JSONObject menuObject=null;
                                JSONArray menu_array=obj2.getJSONArray("bill");
                                ArrayList<Menu> myMenu=new ArrayList<Menu>();

                                for(int k=0;k<menu_array.length();k++){
                                    Menu m=new Menu();
                                    menuObject=menu_array.getJSONObject(k);
                                    String menu_name=menuObject.getString("name");
                                    String price=menuObject.getString("total");
                                    Log.e("menu+price",menu_name+" "+price);
                                    m.setName(menu_name);
                                    m.setPrice(price);
                                    myMenu.add(m);
                                }

                                String restid=obj.getString("restaurant");
                                String restname=obj.getString("restaurant_name");
                                String total=obj.getString("total");
                                String advance=obj.getString("advance");
                                String bookingId=obj.getString("id");
                                String bookingCancel=obj.getString("cancelled");

                                Past p=new Past();
                                p.setRestid(restid);
                                p.setRestname(restname);
                                p.setTotal(total);
                                p.setAdvance(advance);
                                p.setBooking_id(bookingId);
                                p.setDate(date);
                                p.setTime(time);
                                p.setBooking_success(booking_success);
                                p.setMenu(myMenu);
                                p.setBooking_cancelled(bookingCancel);
                                bookings.add(p);
                            }

                            RecyclerView.Adapter adapter = new PastBookingAdapter(bookings,PastBooking.this,booking_data);
                            recyclerView.setAdapter(adapter);
                            pd.dismiss();
                        } catch (JSONException e) {
                            Toast.makeText(PastBooking.this,e.toString(),Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        } catch (ParseException e) {
                            Toast.makeText(PastBooking.this,e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("networking error",error.getErrorDetail());
                        pd.dismiss();
                    }
                });
    }

}
