package com.zaafoo.preorder.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.models.FloorText;
import com.zaafoo.preorder.models.Table;
import com.zaafoo.preorder.others.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import io.paperdb.Paper;

public class TableLayout extends AppCompatActivity {

    Button b;
    String rest_data;
    LinearLayout ll;
    Bitmap bg;
    Canvas canvas;
    int screenWidth,screenHeight;
    ArrayList<Table> tableList;
    Paint pinkpaint, bluepaint, blackpaint, whitepaint;
    int leftPersons,noOfPersons;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String restid;
    ArrayList<String> bookedTableNos;
    ProgressDialog pd;
    Calendar c;
    ArrayList<FloorText> myFloorText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_layout);
        setTitle("Book Table");
        Paper.init(this);
        restid=Paper.book().read("restid");
        bookedTableNos=new ArrayList<>();
        myFloorText=new ArrayList<>();

        c=Calendar.getInstance();
        String currentTime=Paper.book().read("time");
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfDate=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date current=sdf.parse(currentTime);
            Date minTime=sdf.parse("09:30");
            Date maxTime=sdf.parse("21:30");
            if(current.before(minTime)){
                Paper.book().write("time","09:30");
            }
            else if(current.after(maxTime)) {
                Paper.book().write("time","09:30");
                c.add(Calendar.DAY_OF_MONTH, 1);
                String d=sdfDate.format(c.getTime());
                Paper.book().write("date",d);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        loadActivity(1);

        new LovelyInfoDialog(this)
                .setTopColorRes(R.color.colorPrimaryDark)
                .setNotShowAgainOptionEnabled(0)
                .setNotShowAgainOptionChecked(true)
                .setTitle("Info")
                .setCancelable(false)
                .setMessage("1. Use Buttons On The Top To Manipulate Time,Date & Guests.\n" +
                        "2. Reserved Tables Are In Black whereas Unreserved are in Blue\n" +
                        "3. Default Number of Guests: 1\n" +
                        "4. In Case Guest Fails To Attend The Booking,Table shall remain booked " +
                        "for 15 mins(Table Booking) & for 90 mins(Table Booking + PerOrder)")
                .show();
    }



    private void loadActivity(int person) {

        String time=Paper.book().read("time");
        String date=Paper.book().read("date");

        setTitle(Html.fromHtml("<small>Date : "+date+", Time : "+time+"</small>"));

        noOfPersons=person;
        leftPersons=noOfPersons;
        Intent i=getIntent();
        rest_data=i.getExtras().getString("rest_data");
        ll = (LinearLayout)findViewById(R.id.table_layout);
        tableList=new ArrayList<>();
        initialiseColors();
        // Find Linear Layout Height & Width
        ll.post(new Runnable() {
            @Override
            public void run() {
                tableList=getTableLayoutData(rest_data);
                screenHeight=ll.getHeight();screenWidth=ll.getWidth();
                getBookedTableData();
            }
        });


        // Touch Code
        ll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                bg = Bitmap.createBitmap(screenWidth,screenHeight, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bg);
                drawFloorPlan(canvas);
                drawFloorText(canvas);
                Rect rect;
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    int x = (int) (event.getX());
                    int y = (int) (event.getY());
                    for (Table t : tableList) {
                        rect = t.getR();
                        if (rect.contains(x,y)) {
                            if (t.isBooked())
                                canvas.drawRect(rect, blackpaint);
                            else if (t.isSelected()) {
                                canvas.drawRect(rect, bluepaint);
                                t.setSelected(false);
                                leftPersons = leftPersons + t.getNoOfPerson();
                                Toast.makeText(TableLayout.this,leftPersons+"",Toast.LENGTH_SHORT).show();

                            } else {
                                canvas.drawRect(rect, pinkpaint);
                                t.setSelected(true);
                                leftPersons = leftPersons - t.getNoOfPerson();
                                if(leftPersons>0)
                                    Toast.makeText(TableLayout.this,leftPersons+"",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(TableLayout.this,"All Persons Got Tables",Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            if (t.isBooked())
                                canvas.drawRect(rect, blackpaint);
                            else if (t.isSelected())
                                canvas.drawRect(rect, pinkpaint);
                            else
                                canvas.drawRect(rect, bluepaint);
                        }
                        canvas.drawText(String.valueOf(t.getNoOfPerson()),(int)(t.getX()*(screenWidth/7)),(int)(t.getY()*(screenWidth/7)),whitepaint);
                        if(leftPersons<0)
                            leftPersons=0;
                    }
                    ll.setBackgroundDrawable(new BitmapDrawable(bg));
                    if(leftPersons<=0)
                        tryAgain();
                }
                return true;
            }
        });

    }

    private void drawInitialLayout() {

        double x, y;
        int left;
        int right;
        int top;
        int bottom;
        Rect rectangle;

        int squareSide = ((screenWidth/7)/4);

        if (!tableList.isEmpty()) {

            bg = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bg);
            drawFloorPlan(canvas);
            drawFloorText(canvas);
            for (Table t : tableList) {

                x = t.getX();
                y = t.getY();
                left = (int) (x * (screenWidth/7) - squareSide);
                right = (int) (x * (screenWidth/7) + squareSide);
                top = (int) (y * (screenWidth/7) - squareSide);
                bottom = (int) (y * (screenWidth/7) + squareSide);

                rectangle = new Rect(left, top, right, bottom);
                t.setR(rectangle);

                if (t.isBooked())
                    canvas.drawRect(rectangle, blackpaint);
                else
                    canvas.drawRect(rectangle, bluepaint);

                canvas.drawText(String.valueOf(t.getNoOfPerson()), (int) (t.getX() * (screenWidth/7)), (int) (t.getY() * (screenWidth/7)), whitepaint);

            }

            ll.setBackgroundDrawable(new BitmapDrawable(bg));
        }

    }


    private ArrayList<Table> getTableLayoutData(String rest_data) {

        JSONArray table_array;
        JSONObject table_object,obj;
        String floorPlan;
        Table table;
        ArrayList<Table> tableList=new ArrayList<>();
        try {

            obj=new JSONObject(rest_data);
            floorPlan=obj.getString("flp");
            table_array=obj.getJSONArray("tabls");
            // Table Layout Loop
            for(int i=0;i<table_array.length();i++){
                table=new Table();
                table_object=table_array.getJSONObject(i);
                // Set Table Id
                String id=table_object.getString("id");
                table.setId(id);
                // Set Table x-coordinate
                String x=table_object.getString("x");
                table.setX(Double.parseDouble(x));
                // Set Table y-coordinate
                String y=table_object.getString("y");
                table.setY(Double.parseDouble(y));
                // Set No Of Persons can sit on a table
                String noOfPersons=table_object.getString("personstosit");
                table.setNoOfPerson(Integer.parseInt(noOfPersons));

                tableList.add(table);
            }

            JSONArray floorText=obj.getJSONArray("floortext");
            FloorText floor;
            JSONObject floorObject;
            // FLoor Text Loop
            for(int j=0;j<floorText.length();j++){
                floor=new FloorText();
                floorObject=floorText.getJSONObject(j);
                String x=floorObject.getString("x");
                String y=floorObject.getString("y");
                String text=floorObject.getString("txt");
                String angle=floorObject.getString("degree");
                floor.setX(x);
                floor.setY(y);
                floor.setText(text);
                floor.setAngle(angle);

                myFloorText.add(floor);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tableList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.table_time_date_person, menu);
        MenuItem person_item = menu.findItem(R.id.persons);
        MenuItem date_item = menu.findItem(R.id.date);
        person_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectPersons();
                clearTables();
                return true;
            }
        });

        date_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(TableLayout.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                Paper.book().write("day", dayOfMonth);
                                Paper.book().write("date", date);
                                bookedTableNos = new ArrayList<String>();
                                Intent i=new Intent(TableLayout.this,TimeSlotActivity.class);
                                i.putExtra("rest_data",rest_data);
                                startActivity(i);
                                finish();
                            }
                        }, mYear, mMonth, mDay);
                DatePicker picker = datePickerDialog.getDatePicker();
                picker.setMinDate(c.getTimeInMillis() - 1000);
                picker.setMaxDate((c.getTimeInMillis()-1000)+(1000*60*60*24*10));
                picker.setCalendarViewShown(false);
                datePickerDialog.show();

                return true;
            }
        });

                return true;
    }


    // Initialise Paint Colors
    private void initialiseColors(){
        pinkpaint = new Paint();
        pinkpaint.setColor(Color.parseColor("#e1336f"));
        pinkpaint.setTextSize(25);

        blackpaint = new Paint();
        blackpaint.setColor(Color.parseColor("#000000"));
        blackpaint.setStrokeWidth(2);

        whitepaint = new Paint();
        whitepaint.setColor(Color.parseColor("#FFFFFF"));
        whitepaint.setTextSize(15);
        whitepaint.setTextAlign(Paint.Align.CENTER);

        bluepaint = new Paint();
        bluepaint.setColor(Color.parseColor("#0099cc"));
    }

    private void clearTables() {
        for(Table t:tableList)
            t.setSelected(false);
        leftPersons=noOfPersons;
        drawInitialLayout();
    }

    // Try Again Code
    private void tryAgain() {

        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("Zaafoo Select Table");
        alert.setCancelable(false);
        alert.setMessage("Do you want to continue with selected tables or reselect tables again?");
        alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Paper.book().write("persons",noOfPersons);
                new SessionManagement(TableLayout.this).insertTableList(tableList);
                Intent i=new Intent(TableLayout.this,MenuActivity.class);
                i.putExtra("rest_data",rest_data);
                clearTables();
                startActivity(i);
            }
        });
        alert.setNegativeButton("Reselect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearTables();
                dialog.dismiss();
            }
        });
        AlertDialog dialog=alert.create();
        dialog.show();
    }

    public void selectPersons(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.add_persons_table, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.editText);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        int person=Integer.parseInt(userInputDialogEditText.getText().toString());
                        loadActivity(person);
                        dialogBox.dismiss();
                    }
                })

                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public void getBookedTableData(){

        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading.. ");
        pd.show();
        String dateTime=Paper.book().read("date").toString()+"T"+Paper.book().read("time").toString();
        AndroidNetworking.post("http://zaafoo.com/fetchRestaurantBookings/")
                .addBodyParameter("datetime", dateTime)
                .addBodyParameter("restid", restid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr=response.getJSONArray("bookedtables");
                            for(int i=0;i<arr.length();i++){
                                JSONObject obj=arr.getJSONObject(i);
                                String id=obj.getString("id");
                                bookedTableNos.add(id);
                            }

                            if(!bookedTableNos.isEmpty()){
                                for(Table t:tableList){
                                    for(int i=0;i<bookedTableNos.size();i++)
                                        if (t.getId().equalsIgnoreCase(bookedTableNos.get(i))){
                                            t.setBooked(true);
                                        }
                                }

                            }

                            drawInitialLayout();
                            pd.dismiss();
                        } catch (JSONException e) {
                            pd.dismiss();
                            Toast.makeText(TableLayout.this,e.toString(),Toast.LENGTH_SHORT).show();

                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                        Toast.makeText(TableLayout.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Draw Floor Plan
    public void drawFloorPlan(Canvas c){

        JSONObject rest_data_object=null;
        String floorPlan=null;
        try {
            rest_data_object=new JSONObject(rest_data);
            floorPlan=rest_data_object.getString("flp");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringTokenizer st=new StringTokenizer(floorPlan," ");
        StringTokenizer st1;
        ArrayList<String> coordinates=new ArrayList<>();

        while(st.hasMoreTokens()) {
            st1 = new StringTokenizer(st.nextToken(), ",");
            while (st1.hasMoreTokens()) {
                String val = st1.nextToken();
                coordinates.add(val);
            }
        }

        for(int i=0;i<coordinates.size()-3;i=i+2){

            float startX,startY,endX,endY;
            startX= (Float.parseFloat(coordinates.get(i))/100*(screenWidth/7));
            startY= (Float.parseFloat(coordinates.get(i+1))/100*(screenWidth/7));
            endX= (Float.parseFloat(coordinates.get(i+2))/100*(screenWidth/7));
            endY= (Float.parseFloat(coordinates.get(i+3))/100*(screenWidth/7));
            c.drawLine(startX,startY,endX,endY,blackpaint);
        }
        int x=coordinates.size();
        c.drawLine((float) (Float.parseFloat(coordinates.get(x-2))/100*(screenWidth/7)),(float) (Float.parseFloat(coordinates.get(x-1))/100*(screenWidth/7)),(float) (Float.parseFloat(coordinates.get(0))/100*(screenWidth/7)),(float) (Float.parseFloat(coordinates.get(1))/100*(screenWidth/7)),blackpaint);

    }


    public void drawFloorText(Canvas c){

        ArrayList<FloorText> floor=myFloorText;
        if(!floor.isEmpty()){
            float x,y;
            for(FloorText f:floor){
                c.save();
                x= (float) (Float.parseFloat(f.getX())*screenWidth/7);
                y= (float) (Float.parseFloat(f.getY())*screenWidth/7);
                c.rotate((int)Float.parseFloat(f.getAngle()), x, y);
                c.drawText(f.getText(),x,y,pinkpaint);
                c.restore();
            }

        }
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
