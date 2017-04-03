package com.zaafoo.preorder.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.appnext.ads.fullscreen.FullScreenVideo;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.adapters.PageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {


    HashMap<String, String> cities;
    JSONObject city;
    ArrayList<String> cityList;
    static String rest_list;
    TabLayout tabLayout;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cities = new HashMap<>();
        cityList = new ArrayList<>();
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("RESTAURANT"));
        tabLayout.addTab(tabLayout.newTab().setText("OFFERS"));
        tabLayout.addTab(tabLayout.newTab().setText("ACCOUNT"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setDateAndTime();
    }

    private void setDateAndTime() {

        Calendar calander = Calendar.getInstance();
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

    private void loadTabs() {


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        String cityJson = SplashActivity.returnCityData();
        try {
            JSONArray response = new JSONArray(cityJson);
            for (int i = 0; i < response.length(); i++) {
                city = response.getJSONObject(i);
                String id = city.getString("id");
                String c = city.getString("city_name");
                cities.put(id, c);
            }
            for (String key : cities.keySet()) {
                cityList.add(cities.get(key).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.my_spinner, cityList);
        adapter.setDropDownViewResource(R.layout.my_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String city = parent.getItemAtPosition(position).toString();
                for (String key : cities.keySet()) {
                    if (((cities.get(key).toString()).equalsIgnoreCase(city))) {
                        getRestaurantList(key);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void getRestaurantList(String cityid) {

        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading Restaurant.. ");
        pd.show();
        AndroidNetworking.post("http://zaafoo.com/restcity/")
                .addBodyParameter("cityid", cityid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        rest_list = response;
                        loadTabs();
                        pd.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                    }
                });

    }

    public static String returnRestList(){
        return rest_list;
    }
}


