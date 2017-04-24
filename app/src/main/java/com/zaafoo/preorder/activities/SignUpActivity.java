package com.zaafoo.preorder.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.zaafoo.preorder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

public class SignUpActivity extends AppCompatActivity {



    Button register;
    TextView alreadyRegistered;
    ProgressDialog pd;
    LoginButton loginButton;
    CallbackManager callbackManager;
    ProfileTracker tracker;
    ArrayList<String> fbPermissions;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_sign_up);
        Paper.init(this);
        layout=(LinearLayout)findViewById(R.id.register_back);
        layout.getBackground().setAlpha(10);
        loginButton=(LoginButton)findViewById(R.id.login_button);
        register=(Button)findViewById(R.id.signup);
        alreadyRegistered=(TextView)findViewById(R.id.already_have_account);
        fbPermissions=new ArrayList<>();
        fbPermissions.add("public_profile");
        fbPermissions.add("email");
        loginButton.setReadPermissions(fbPermissions);
        callbackManager=CallbackManager.Factory.create();


        tracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                tracker.stopTracking();
                if(currentProfile!=null) {
                    Profile profile = currentProfile;
                    Paper.book().write("profile_image", profile.getProfilePictureUri(120, 120).toString());
                }
            }
        };

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                tracker.startTracking();
                AccessToken accessToken=loginResult.getAccessToken();
                getZaafooToken(accessToken.getToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignUpActivity.this,"Login Cancelled By User",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignUpActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,UserRegisterActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void getZaafooToken(String accessToken) {
        final ProgressDialog px=new ProgressDialog(this);
        px.setCancelable(false);
        px.setTitle("Zaafoo");
        px.setMessage("Logging In..");
        px.show();

        AndroidNetworking.post("http://zaafoo.com/facebook-signup/")
                .addBodyParameter("access_token", accessToken)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String name = response.getString("username");
                            String token=response.getString("token");
                            String mobileVerified=response.getString("mobile");
                            Paper.book().write("user_name",name);
                            Paper.book().write("token",token);
                            px.dismiss();
                            if(mobileVerified.equals("null"))
                                startActivity(new Intent(SignUpActivity.this,MobileVerificationActivity.class));
                            else
                                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                            finish();
                        } catch (JSONException e) {
                            Log.e("json fb",e.getMessage());
                            px.dismiss();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        FacebookSdk.sdkInitialize(SignUpActivity.this);
                        LoginManager.getInstance().logOut();
                        Toast.makeText(SignUpActivity.this,"Oops..You Are Already Registered With Zaafoo",Toast.LENGTH_LONG).show();
                        px.dismiss();
                    }
                });


    }
}

