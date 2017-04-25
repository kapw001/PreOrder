package com.zaafoo.preorder.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.adapters.PageAdapter;
import com.zaafoo.preorder.adapters.RestaurantPageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.paperdb.Paper;

public class AboutRestaurant extends AppCompatActivity {


    ImageView logo;
    static String rest_data;
    TextView discountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_restaurant);
        logo = (ImageView) findViewById(R.id.imageView3);
        discountView = (TextView) findViewById(R.id.discount_view);
        Intent i = getIntent();
        rest_data = i.getExtras().getString("rest_data");
        populateRestaurantPage();
        Paper.init(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.restautrant_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("INFO"));
        tabLayout.addTab(tabLayout.newTab().setText("MENU"));
        tabLayout.addTab(tabLayout.newTab().setText("REVIEWS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.restaurant_pager);
        final RestaurantPageAdapter adapter = new RestaurantPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Creating Offer Dialog Logic
        String shared=Paper.book().read("share","xyz");
        boolean isshared=shared.equalsIgnoreCase("true");
        if(!isshared)
        createOfferDialog();

    }

    private void createOfferDialog() {

        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("Warning");
        alert.setCancelable(false);
        alert.setMessage("Dear User,You have not shared us, due to which you will not be able to avail our discounts..!!");
        alert.setPositiveButton("Share Us", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(AboutRestaurant.this,ShareUsActivity.class));
            }
        });

        alert.setNegativeButton("No,Thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog=alert.create();
        dialog.show();


    }

    // Round Decimal Values
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void populateRestaurantPage() {

        try {
            JSONObject rest_object = new JSONObject(rest_data);
            JSONArray rest_details = rest_object.getJSONArray("res_details");
            rest_object = rest_details.getJSONObject(0);
            String image_url = rest_object.getString("im_url");
            Picasso.with(this).load("http://zaafoo.com/" + image_url).fit().into(logo);

            rest_object = new JSONObject(rest_data);
            rest_details = rest_object.getJSONArray("Discount");
            rest_object = rest_details.getJSONObject(0);
            String percentage = rest_object.getString("pc");
            double per = Double.parseDouble(percentage);
            per = per * 100;
            per = round(per, 2);
            percentage = String.valueOf(per);
            String discount = rest_object.getString("Discount_Price");
            discountView.setText("Get " + percentage + "% discount on an order of Rs." + discount + " & above.");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static String giveRestDatatoFragments() {

        if (rest_data != null)
            return rest_data;
        else
            return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDateAndTime();
    }

    private void setDateAndTime() {

        Calendar calander = Calendar.getInstance();
        calander.add(Calendar.MINUTE,30);
        if (calander.get(Calendar.MINUTE) <= 15) {
            calander.set(Calendar.MINUTE, 15);
        }
        else if(calander.get(Calendar.MINUTE) > 15 && calander.get(Calendar.MINUTE) <= 30)
            calander.set(Calendar.MINUTE, 30);
        else if(calander.get(Calendar.MINUTE) > 30 && calander.get(Calendar.MINUTE) <= 45)
            calander.set(Calendar.MINUTE, 45);
        else {
            calander.add(Calendar.HOUR_OF_DAY, 1);
            calander.clear(Calendar.MINUTE);
        }
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
        String x = simpledateformat.format(calander.getTime());
        Paper.book().write("date",x);
        simpledateformat=new SimpleDateFormat("dd");
        int k=Integer.valueOf(simpledateformat.format(calander.getTime()));
        Paper.book().write("day",k);
        simpledateformat = new SimpleDateFormat("HH:mm");
        x=simpledateformat.format(calander.getTime());
        Paper.book().write("time",x);
    }
}
