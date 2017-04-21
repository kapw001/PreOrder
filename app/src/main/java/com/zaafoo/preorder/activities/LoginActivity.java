package com.zaafoo.preorder.activities;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.androidnetworking.interfaces.StringRequestListener;
import com.facebook.FacebookSdk;
import com.zaafoo.preorder.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    Button signIn;
    EditText user, pass;
    ProgressDialog pd;
    TextView forgotPass;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signIn = (Button) findViewById(R.id.user_sign_in_button);
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        forgotPass=(TextView)findViewById(R.id.forgot_pass);
        layout=(LinearLayout)findViewById(R.id.login_back);
        layout.getBackground().setAlpha(10);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = user.getText().toString();
                String pass_word = pass.getText().toString();
                loginUser(user_name, pass_word);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(v);
            }
        });
    }


    // Log In User
    private void loginUser(final String user_name, String pass_word) {
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading.. ");
        pd.show();
        AndroidNetworking.post("http://zaafoo.com/loginrest/")
                .addBodyParameter("user", user_name)
                .addBodyParameter("password", pass_word)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pd.dismiss();
                            String token = response.getString("token");
                            String isMobileVerified = response.getString("isMobileVerified");
                            Paper.book().write("token", token);
                            if (isMobileVerified.equalsIgnoreCase("true")) {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                Paper.book().write("user_name",user_name);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(LoginActivity.this, MobileVerificationActivity.class);
                                Paper.book().write("user_name",user_name);
                                startActivity(i);
                            }
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Change Password
    public void changePassword(View v){

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.alert_dialog_input, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.alert_email_input);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        sendEmailForgotPass(userInputDialogEditText.getText().toString());
                        Toast.makeText(LoginActivity.this,"Password Sent",Toast.LENGTH_SHORT).show();
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }



    public void sendEmailForgotPass(String email){

        AndroidNetworking.post("http://zaafoo.com/forgotpasswordview/")
                .addBodyParameter("email",email)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }

                    @Override
                    public void onError(ANError error) {
                    }
                });
    }
}


    // Get All Restaurant in a particular city


