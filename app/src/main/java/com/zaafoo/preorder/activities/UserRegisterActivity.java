package com.zaafoo.preorder.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zaafoo.preorder.R;

import org.json.JSONObject;

public class UserRegisterActivity extends AppCompatActivity {


    EditText user_field,email_field,pass_field,first_name_field,last_name_field;
    Button register;
    ProgressDialog pd;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        layout=(LinearLayout)findViewById(R.id.user_register);
        layout.getBackground().setAlpha(10);
        user_field=(EditText)findViewById(R.id.username);
        email_field=(EditText)findViewById(R.id.email);
        pass_field=(EditText)findViewById(R.id.password);
        first_name_field=(EditText)findViewById(R.id.first_name);
        last_name_field=(EditText)findViewById(R.id.last_name);
        register=(Button)findViewById(R.id.user_signup_button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName=first_name_field.getText().toString();
                String lastName=last_name_field.getText().toString();
                String username=user_field.getText().toString();
                String email=email_field.getText().toString();
                String password=pass_field.getText().toString();
                registerUser(firstName,lastName,username,email,password);
            }
        });

    }


    private void registerUser(String firstName,String lastName,String username, String email, String password) {

        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading.. ");
        pd.show();
        AndroidNetworking.post("http://zaafoo.com/registrationrest/")
                .addBodyParameter("first_name", firstName)
                .addBodyParameter("last_name", lastName)
                .addBodyParameter("user", username)
                .addBodyParameter("email", email)
                .addBodyParameter("password",password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pd.dismiss();
                        Toast.makeText(UserRegisterActivity.this,"SuccessFully Registered",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserRegisterActivity.this,LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                        Toast.makeText(UserRegisterActivity.this,"User Details Exist",Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
