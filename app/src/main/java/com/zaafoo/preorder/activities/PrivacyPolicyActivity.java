package com.zaafoo.preorder.activities;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.zaafoo.preorder.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PrivacyPolicyActivity extends AppCompatActivity {

    TextView policy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        policy=(TextView)findViewById(R.id.textView23);
        AssetManager manager=getAssets();
        try {
            String str = "";
            StringBuffer buffer = new StringBuffer();
            InputStream is=manager.open("policy.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
            }
            policy.setText(buffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
