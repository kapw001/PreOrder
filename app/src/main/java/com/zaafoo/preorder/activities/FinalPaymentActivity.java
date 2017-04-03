package com.zaafoo.preorder.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ebs.android.sdk.Config;
import com.ebs.android.sdk.EBSPayment;
import com.ebs.android.sdk.PaymentRequest;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.models.Menu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

public class FinalPaymentActivity extends AppCompatActivity {

    final String SECRET_KEY="58a990f5bab2422a53f185a7def64f63";
    final int MERCHANT_ID=22818;
    ArrayList<HashMap<String, String>> custom_post_parameters;
    String PaymentStatus,MerchantRefNo;
    static int paid=1;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_payment);
        pb=(ProgressBar)findViewById(R.id.progressBar2);
        Intent intent = getIntent();
        String referenceNo=intent.getStringExtra("reference");
        String amount=intent.getExtras().getString("amount");
        String pay=intent.getExtras().getString("pay");
        String payment_id = intent.getStringExtra("payment_id");
        String user_name=Paper.book().read("user_name");
        if(payment_id!=null) {

            try {
                JSONObject jObject=new JSONObject(payment_id);
                PaymentStatus = jObject.getString("PaymentStatus");
                if(PaymentStatus.equalsIgnoreCase("failed")){
                    if(!pb.isShown())
                        pb.setVisibility(View.VISIBLE);
                    Toast.makeText(this,"Oops..Payment Failed",Toast.LENGTH_LONG).show();
                    goToHome();
                }
                else {
                    if(!pb.isShown())
                        pb.setVisibility(View.VISIBLE);
                    MerchantRefNo = jObject.getString("MerchantRefNo");
                    sendTransactionSuccessData(MerchantRefNo, jObject.toString());
                    goToHome();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // Payment Code
        if(pay!=null)
        goForPayment(user_name,"abcxyz@mail.com",amount,referenceNo);
    }

    private void sendTransactionSuccessData(String merchantRefNo, String s) {
        Paper.init(this);
        String token = Paper.book().read("token");
        AndroidNetworking.post("http://zaafoo.com/contranview/")
                .addBodyParameter("bookingid", merchantRefNo)
                .addBodyParameter("transactiondump", s)
                .addBodyParameter("issuccess", "1")
                .addHeaders("Authorization", "Token " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Toast.makeText(FinalPaymentActivity.this, "Thank You", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
    private void goForPayment(String user,String email,String amount,String referenceNo) {

        PaymentRequest.getInstance().setFailureid("1");
        PaymentRequest.getInstance().setTransactionAmount(amount);
        PaymentRequest.getInstance().setReferenceNo(referenceNo);
        PaymentRequest.getInstance().setCurrency("INR");
        PaymentRequest.getInstance().setLogEnabled("1");
        PaymentRequest.getInstance().setTransactionDescription("Zaafoo Pre_order and Table Booking");
        PaymentRequest.getInstance().setAccountId(MERCHANT_ID);
        PaymentRequest.getInstance().setSecureKey(SECRET_KEY);
        // Payment Options
        PaymentRequest.getInstance().setHidePaymentOption(false);
        PaymentRequest.getInstance().setHideCashCardOption(false);
        PaymentRequest.getInstance().setHideCreditCardOption(false);
        PaymentRequest.getInstance().setHideDebitCardOption(false);
        PaymentRequest.getInstance().setHideNetBankingOption(false);
        PaymentRequest.getInstance().setHideStoredCardOption(true);
        // Shipping Details
        PaymentRequest.getInstance().setShippingName(user);
        PaymentRequest.getInstance().setShippingEmail(email);
        PaymentRequest.getInstance().setShippingAddress("NA");
        PaymentRequest.getInstance().setShippingCity("NA");
        PaymentRequest.getInstance().setShippingPostalCode("NA");
        PaymentRequest.getInstance().setShippingState("NA");
        PaymentRequest.getInstance().setShippingCountry("IND");
        PaymentRequest.getInstance().setShippingPhone("9078728771");
        // Billing Details
        PaymentRequest.getInstance().setBillingName(user);
        PaymentRequest.getInstance().setBillingEmail(email);
        PaymentRequest.getInstance().setBillingAddress("NA");
        PaymentRequest.getInstance().setBillingCity("NA");
        PaymentRequest.getInstance().setBillingPostalCode("NA");
        PaymentRequest.getInstance().setBillingState("NA");
        PaymentRequest.getInstance().setBillingCountry("IND");
        PaymentRequest.getInstance().setBillingPhone("9078728771");

        custom_post_parameters = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hashpostvalues = new HashMap<String, String>();
        hashpostvalues.put("account_details", "saving");
        hashpostvalues.put("merchant_type", "gold");
        custom_post_parameters.add(hashpostvalues);

        PaymentRequest.getInstance()
                .setCustomPostValues(custom_post_parameters);

        EBSPayment.getInstance().init(this,MERCHANT_ID, SECRET_KEY, Config.Mode.ENV_LIVE,
                Config.Encryption.ALGORITHM_SHA512, "zaafoo.com" );

    }

    public void goToHome(){

        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.GONE);
                Intent intent = new Intent(FinalPaymentActivity.this, MainActivity.class);
                intent.putExtra("ads","true");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Paper.book().write("cart_items",new ArrayList<Menu>());
            }
        }, 1000);
    }
}
