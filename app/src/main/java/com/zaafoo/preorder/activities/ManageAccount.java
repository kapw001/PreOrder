package com.zaafoo.preorder.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zaafoo.preorder.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

public class ManageAccount extends AppCompatActivity {


    Button confirm;
    EditText newpass,oldpass;
    String newpassword,oldpassword,token;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        confirm=(Button)findViewById(R.id.change_pass);
        newpass=(EditText)findViewById(R.id.editText2);
        oldpass=(EditText)findViewById(R.id.editText9);
        token= Paper.book().read("token");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newpassword=newpass.getText().toString();
                oldpassword=oldpass.getText().toString();
                AndroidNetworking.post("http://zaafoo.com/changepasswordview/")
                        .addHeaders("Authorization","Token "+token)
                        .addBodyParameter("oldpassword", oldpassword)
                        .addBodyParameter("newpassword", newpassword)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String message=response.getString("message");
                                    Toast.makeText(ManageAccount.this,message, Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error
                                Toast.makeText(ManageAccount.this,error.getErrorDetail(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });




    }
}
