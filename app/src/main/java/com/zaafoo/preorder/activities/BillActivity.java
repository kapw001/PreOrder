package com.zaafoo.preorder.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.adapters.BillListAdapter;
import com.zaafoo.preorder.models.Bill;
import com.zaafoo.preorder.models.Menu;
import com.zaafoo.preorder.models.Table;
import com.zaafoo.preorder.others.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

public class BillActivity extends AppCompatActivity {


    int noOfTables;
    ArrayList<String> bookedTables;
    Button payNow;
    String reference_No;
    private JSONObject book_object;
    String finalTotalAmount;
    JSONObject bill_object;
    ArrayList<Bill> bill_data;
    ListView lv;
    String rest_data;
    String token;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        Paper.init(this);
        token=Paper.book().read("token");
        Intent intent=getIntent();
        rest_data=intent.getExtras().getString("rest_data");
        lv=(ListView)findViewById(R.id.bill_total_list);
        payNow=(Button)findViewById(R.id.button11);
        bookedTables=new ArrayList<>();
        bill_data=new ArrayList<>();
        getBookingData();
        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(BillActivity.this,FinalPaymentActivity.class);
                intent1.putExtra("amount",finalTotalAmount);
                intent1.putExtra("reference",reference_No);
                intent1.putExtra("pay","xyz");
                startActivity(intent1);
                finish();
            }
        });

    }


    private void getBookingData() {
        ArrayList<Table> myTables=new SessionManagement(this).loadTables();
        for(Table t:myTables){
            if(t.isSelected()) {
                bookedTables.add(t.getId());
                noOfTables++;
            }
        }

        ArrayList<Menu> mymenu=new SessionManagement(this).loadCartItems();

        String token= Paper.book().read("token");
        String restid=Paper.book().read("restid");
        String personalRequest="xyz";
        String date=Paper.book().read("date")+"T"+Paper.book().read("time");


        final JSONObject booking_object=createJsonObject(bookedTables,noOfTables,mymenu,token,restid,personalRequest,date);
        book_object=booking_object;
        try {
            JSONArray arr1=book_object.getJSONArray("tablnos");
            JSONArray arr2=book_object.getJSONArray("menus");
            String rest=book_object.getString("restid");
            bill_object=new JSONObject();
            bill_object.put("tables",arr1);
            bill_object.put("menus",arr2);
            bill_object.put("restid",rest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                getBookingIdFromServer(booking_object);        
            }
        }).start();
        
    }

    private void getBookingIdFromServer(JSONObject booking_object) {
        AndroidNetworking.post("http://zaafoo.com/resbookview/")
                .addJSONObjectBody(booking_object)
                .addHeaders("Authorization","Token "+token)
                .setTag("booking object")
                .setContentType("application/json")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            reference_No=response.getString("BookingID");
                            getBillDetails();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e("error",error.toString());
                        Toast.makeText(BillActivity.this, "Something went wrong.Try Later", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void getBillDetails() {

        pd=new ProgressDialog(this);
        pd.setMessage("Preparing Bill");
        pd.setCancelable(false);
        pd.show();
        AndroidNetworking.post("http://zaafoo.com/calculatebillview/")
                .addJSONObjectBody(bill_object)
                .addHeaders("Authorization","Token "+token)// posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray bill=response.getJSONArray("Bill");
                            JSONObject objk=bill.getJSONObject(bill.length()-1);
                            finalTotalAmount=objk.getString("total");
                            for(int z=0;z<bill.length();z++){
                                JSONObject obj=bill.getJSONObject(z);
                                Bill b=new Bill();
                                b.setName(obj.getString("name"));
                                b.setPrice(obj.getString("price"));
                                b.setQuantity(obj.getString("qos"));
                                b.setTotal(obj.getString("total"));
                                bill_data.add(b);
                            }

                            BillListAdapter adap=new BillListAdapter(BillActivity.this,R.layout.bill_final_list,bill_data);
                            lv.setAdapter(adap);
                            pd.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pd.dismiss();
                        }



                    }
                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                    }
                });

    }


    public JSONObject createJsonObject(ArrayList<String> bookedTableId, int noOfTables, ArrayList<Menu> mymenu, String token, String restid, String perRequest, String date){


        JSONObject book_obj=new JSONObject();
        JSONArray table_array=new JSONArray();
        JSONArray menu_array=new JSONArray();
        JSONObject table_Obj;
        JSONObject menu_obj;


        for(String key:bookedTableId){
            table_Obj=new JSONObject();
            try {
                table_Obj.put("tableid",Integer.parseInt(key));
            }
            catch (JSONException e1) {
                e1.printStackTrace();
            }
            table_array.put(table_Obj);
        }


        for(int i=0;i<mymenu.size();i++){
            try {
                menu_obj=new JSONObject();
                menu_obj.put("menuid",Integer.parseInt(mymenu.get(i).getId()));
                menu_obj.put("value",Integer.parseInt(mymenu.get(i).getAmount()));
                menu_array.put(i,menu_obj);
            } catch (JSONException e) {
            }
        }

        try {
            book_obj.put("tables",noOfTables);
            book_obj.put("Personal Request",perRequest);
            book_obj.put("restid",restid);
            book_obj.put("tablnos",table_array);
            book_obj.put("menus",menu_array);
            book_obj.put("date",date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Memory Optimization
        table_array=null;
        menu_array=null;
        table_Obj=null;
        menu_obj=null;
        //

        return  book_obj;

    }
}
