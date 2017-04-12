package com.zaafoo.preorder.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.models.FloorText;
import com.zaafoo.preorder.models.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import io.paperdb.Paper;

public class CustomerTableView extends AppCompatActivity {

    ArrayList<String> bookedTableList;
    String rest_id;
    ArrayList<Table> tableList;
    Paint pinkpaint, bluepaint, blackpaint, whitepaint;
    LinearLayout ll;
    Bitmap bg;
    Canvas canvas;
    int screenWidth,screenHeight;
    ArrayList<FloorText> myFloorText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_table_view);

        Paper.init(this);
        bookedTableList=new ArrayList<>();
        tableList=new ArrayList<>();
        myFloorText=new ArrayList<>();

        ll = (LinearLayout)findViewById(R.id.table_view);
        ll.post(new Runnable() {
            @Override
            public void run() {
                screenHeight=ll.getHeight();screenWidth=ll.getWidth();
            }
        });

        initialiseColors();
        String transactions= Paper.book().read("transactions");
        prepareBookedTableData(transactions);
        getRestaurantTableData();
    }

    // GET restaurant data
    private void getRestaurantTableData() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading Tables..");
        pd.show();
        AndroidNetworking.post("http://zaafoo.com/cusresview/")
                .addBodyParameter("restid", rest_id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        pd.dismiss();
                        prepareTablesAndFloor(response.toString());

                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                    }
                });
    }

    // Prepare table & floor data
    private void prepareTablesAndFloor(String rest_data) {
        JSONArray table_array;
        JSONObject table_object,obj;
        Table table;

        try {

            obj=new JSONObject(rest_data);
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

            if(!bookedTableList.isEmpty()){
                for(Table t:tableList){
                    for(int i=0;i<bookedTableList.size();i++)
                        if (t.getId().equalsIgnoreCase(bookedTableList.get(i))){
                            t.setBooked(true);
                        }
                }

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

            drawInitialLayout(rest_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // GET booked Tables data & Restid
    private void prepareBookedTableData(String transactions) {

        try {
            JSONObject obj=new JSONObject(transactions);
            rest_id=obj.getString("restaurant");
            JSONArray tableArray=obj.getJSONArray("tables");
            JSONObject tableObj;
            for(int i=0;i<tableArray.length();i++){
                tableObj=tableArray.getJSONObject(i);
                String id=tableObj.getString("id");
                bookedTableList.add(id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initialiseColors(){
        pinkpaint = new Paint();
        pinkpaint.setColor(Color.parseColor("#e1336f"));
        pinkpaint.setTextSize(30);

        blackpaint = new Paint();
        blackpaint.setColor(Color.parseColor("#000000"));
        blackpaint.setStrokeWidth(2);

        whitepaint = new Paint();
        whitepaint.setColor(Color.parseColor("#FFFFFF"));
        whitepaint.setTextSize(20);
        whitepaint.setTextAlign(Paint.Align.CENTER);

        bluepaint = new Paint();
        bluepaint.setColor(Color.parseColor("#0099cc"));
    }

    private void drawInitialLayout(String rest_data) {

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
            drawFloorPlan(canvas,rest_data);
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

    private void drawFloorPlan(Canvas c, String rest_data) {
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

    // Draw Floor Text
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

}
