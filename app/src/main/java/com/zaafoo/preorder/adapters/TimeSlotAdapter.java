package com.zaafoo.preorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.TableLayout;
import com.zaafoo.preorder.activities.TimeSlotActivity;

import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * Created by SUB on 4/6/2017.
 */

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    ArrayList<String> timeSlotList;
    Context context;
    String rest_data;

    public TimeSlotAdapter(ArrayList<String> timeSlotList, Context context, String rest_data) {
        this.timeSlotList = timeSlotList;
        this.context = context;
        this.rest_data=rest_data;
    }

    @Override
    public TimeSlotAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.time_slot_single, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TimeSlotAdapter.ViewHolder holder, int position) {
        holder.timeButton.setText(timeSlotList.get(position));
    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Button timeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            timeButton=(Button)itemView.findViewById(R.id.button2);
            timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String time=timeSlotList.get(getAdapterPosition());
                    Paper.book().write("time",time);
                    Intent i=new Intent(context, TableLayout.class);
                    i.putExtra("rest_data",rest_data);
                    context.startActivity(i);
                    ((TimeSlotActivity)context).finish();
                }
            });
        }
    }
}