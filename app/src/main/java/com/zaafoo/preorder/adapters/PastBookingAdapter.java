package com.zaafoo.preorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.CustomerBillView;
import com.zaafoo.preorder.models.Past;

import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * Created by SUB on 3/30/2017.
 */

public class PastBookingAdapter extends RecyclerView.Adapter<PastBookingAdapter.ViewHolder> {


    ArrayList<Past> pastBookings;
    Context context;
    ArrayList<String> booking_data;

    public PastBookingAdapter(ArrayList<Past> pastBookings, Context context, ArrayList<String> booking_data) {
        this.pastBookings = pastBookings;
        this.context = context;
        this.booking_data=booking_data;
        Paper.init(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.rest_name.setText(pastBookings.get(position).getRestname());
        holder.total_amount.setText("Rs."+pastBookings.get(position).getTotal());
        holder.advance_amount.setText("Rs."+pastBookings.get(position).getAdvance());
        holder.bookingID.setText(pastBookings.get(position).getBooking_id());
        holder.date.setText(pastBookings.get(position).getDate());
        holder.time.setText(pastBookings.get(position).getTime());
        String status=pastBookings.get(position).getBooking_success();
        if(status.equalsIgnoreCase("true")){
            holder.bookingSuccess.setText("COMPLETED");
            holder.bookingSuccess.setBackgroundColor(Color.parseColor("#388E3C"));
            holder.paid.setVisibility(View.VISIBLE);
        }
        else{
            holder.bookingSuccess.setText("PENDING");
            holder.bookingSuccess.setBackgroundColor(Color.parseColor("#d32f2f"));
            holder.paid.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return pastBookings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rest_name;
        TextView total_amount;
        TextView advance_amount;
        TextView date;
        TextView time;
        TextView bookingID;
        TextView bookingSuccess;
        ImageView paid;

        public ViewHolder(View itemView) {
            super(itemView);
            rest_name=(TextView)itemView.findViewById(R.id.rest_name_past);
            total_amount=(TextView)itemView.findViewById(R.id.textView58);
            advance_amount=(TextView)itemView.findViewById(R.id.textView57);
            date=(TextView)itemView.findViewById(R.id.textView53);
            time=(TextView)itemView.findViewById(R.id.textView54);
            bookingID=(TextView)itemView.findViewById(R.id.textView55);
            bookingSuccess=(TextView)itemView.findViewById(R.id.textView59);
            paid=(ImageView)itemView.findViewById(R.id.imageView14);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Paper.book().write("bill_data",pastBookings.get(getLayoutPosition()).getMenu());
                    Paper.book().write("booking_id",pastBookings.get(getLayoutPosition()).getBooking_id());
                    Paper.book().write("transactions",booking_data.get(getLayoutPosition()));
                    context.startActivity(new Intent(context, CustomerBillView.class));
                }
            });

        }
    }
}
