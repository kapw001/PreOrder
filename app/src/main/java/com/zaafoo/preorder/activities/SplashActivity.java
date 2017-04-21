package com.zaafoo.preorder.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.zaafoo.preorder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    String token;
    static String rest_list;
    static String city_data;
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Paper.init(this);
        token=Paper.book().read("token");
        layout=(RelativeLayout)findViewById(R.id.splash_back);
        layout.getBackground().setAlpha(10);
        checkAndroidVersion();
    }

    private void checkAndroidVersion() {
        AndroidNetworking.post("http://zaafoo.com/varparamread/")
                .addBodyParameter("var_name", "android_version")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array=response.getJSONArray("ret");
                            JSONObject obj=array.getJSONObject(0);
                            String version=obj.getString("f");
                            if(version.equalsIgnoreCase(String.valueOf(getVersion(SplashActivity.this))))
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getcityId();
                                }
                            }).start();
                            else
                                showUpgradeDailog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void showUpgradeDailog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Warning");
        builder.setMessage("You are currently using old version of Zaafoo.Upgrade To Use Zaafoo");
        builder.setCancelable(false);
        builder.setNegativeButton("Ok,Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.zaafoo.preorder" )));
                finish();
            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();
    }


    public void getcityId(){
        AndroidNetworking.get("http://zaafoo.com/publicrest/cities/")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        city_data=response;
                        goToHome();
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    public static String returnCityData(){
        return city_data;
    }

    public void goToHome(){

        if(token!=null)
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
        else
            startActivity(new Intent(SplashActivity.this,SignUpActivity.class));

        finish();
    }


    public float getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

}

