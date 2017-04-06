package com.zaafoo.preorder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.Toast;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.adapters.TimeSlotAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.paperdb.Paper;

public class TimeSlotActivity extends AppCompatActivity {

    RecyclerView timeSlots;
    ArrayList timeSlotsList;
    TimeSlotAdapter adapter;
    String rest_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slot);

        Intent i=getIntent();
        rest_data=i.getExtras().getString("rest_data");

        Paper.init(this);
        timeSlots=(RecyclerView)findViewById(R.id.time_list);
        timeSlotsList=new ArrayList();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdfDate= new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime= new SimpleDateFormat("HH:mm");
        String xyz= Paper.book().read("date");
        setTitle(Html.fromHtml("<small>Booking Slots For Date : "+xyz+"</small>"));

        try {
            Date userDate = sdfDate.parse(xyz);
            Date sysDate=calendar.getTime();

            // check if the selected date is today
            if(!userDate.after(sysDate)){
                String current=sdfTime.format(calendar.getTime());
                Date currentTime=sdfTime.parse(current);
                Date minTime=sdfTime.parse("09:30");
                if(currentTime.after(minTime)) {
                    calendar.add(Calendar.MINUTE,30);
                    prepareTimeSlots(sdfTime.format(calendar.getTime()), "21:30");
                }
                else
                    prepareTimeSlots("9:30","21:30");
            }

            else
                prepareTimeSlots("9:30","21:30");
        } catch (ParseException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        adapter=new TimeSlotAdapter(timeSlotsList,this,rest_data);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        timeSlots.setLayoutManager(mLayoutManager);
        timeSlots.setItemAnimator(new DefaultItemAnimator());
        timeSlots.setHasFixedSize(true);
        timeSlots.setAdapter(adapter);

    }

    private void prepareTimeSlots(String startTime,String endTime) {

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
        try {
            calendar.setTime(sdf.parse(startTime));
            if (calendar.get(Calendar.MINUTE) <= 15) {
                calendar.set(Calendar.MINUTE, 15);
            }
            else if(calendar.get(Calendar.MINUTE) > 15 && calendar.get(Calendar.MINUTE) <= 30)
                calendar.set(Calendar.MINUTE, 30);
            else if(calendar.get(Calendar.MINUTE) > 30 && calendar.get(Calendar.MINUTE) <= 45)
                calendar.set(Calendar.MINUTE, 45);
            else {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                calendar.clear(Calendar.MINUTE);
            }

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(sdf.parse(endTime));

            while(endCalendar.after(calendar)){
                String slotTime=sdf.format(calendar.getTime());
                timeSlotsList.add(slotTime);
                calendar.add(Calendar.MINUTE,15);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}

