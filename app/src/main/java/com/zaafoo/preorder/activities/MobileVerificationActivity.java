package com.zaafoo.preorder.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.zaafoo.preorder.R;

import org.json.JSONObject;

import io.paperdb.Paper;

public class MobileVerificationActivity extends AppCompatActivity {

    EditText number,otp;
    Button verify;
    String OTP;
    String num;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        number=(EditText)findViewById(R.id.editText7);
        Paper.init(this);
        token=Paper.book().read("token");
        verify=(Button)findViewById(R.id.button5);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num=number.getText().toString();
                OTP=generateOTP();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AndroidNetworking.get("https://www.smsgatewayhub.com/api/mt/SendSMS?APIKey=6xN8q3BrxEKD5VKj21ISvw&senderid=ZAAFOO&channel=2&DCS=0&flashsms=0&number=91"+num+"&text=OTP-"+OTP+"&route=1")
                                .addPathParameter("pageNumber", "0")
                                .addQueryParameter("limit", "3")
                                .setPriority(Priority.LOW)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                createOTPVErifyDialog();
                                            }
                                        });

                                    }
                                    @Override
                                    public void onError(ANError error) {

                                    }
                                });
                    }
                }).start();

            }
        });
    }


    public String generateOTP(){
        int x=((int)(Math.random()*9000))+1000;
        return String.valueOf(x);
    }

    // Create OTP verify Dialog
    public void createOTPVErifyDialog(){

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.enter_otp_layout, null);

        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setView(mView);

        otp=(EditText)mView.findViewById(R.id.editText10);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(OTP.equals(otp.getText().toString())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendMobileNumberToZaafoo();
                        }
                    }).start();
                    startActivity(new Intent(MobileVerificationActivity.this, MainActivity.class));
                    dialog.dismiss();
                    finish();
                }
                else{
                    Toast.makeText(MobileVerificationActivity.this, "Could Not Verify..Try Again", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=alert.create();
        dialog.show();
    }

    private void sendMobileNumberToZaafoo() {
        AndroidNetworking.post("http://zaafoo.com/mobverifyview/")
                .addHeaders("Authorization","Token "+token)
                .addBodyParameter("mob", num)
                .addBodyParameter("otp", OTP)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MobileVerificationActivity.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Paper.book().delete("token");
        Paper.book().delete("user_name");
        FacebookSdk.sdkInitialize(this);
        LoginManager.getInstance().logOut();
    }
}

