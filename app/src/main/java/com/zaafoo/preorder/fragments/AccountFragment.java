package com.zaafoo.preorder.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.ManageAccount;
import com.zaafoo.preorder.activities.PastBooking;
import com.zaafoo.preorder.activities.PrivacyPolicyActivity;
import com.zaafoo.preorder.activities.ShareUsActivity;
import com.zaafoo.preorder.activities.SignUpActivity;
import com.zaafoo.preorder.activities.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */

public class AccountFragment extends Fragment {

    Button logOut;
    Button myBookings;
    Button manageAccount;
    Button shareUs;
    Button privacyPolicy;
    Button callus;
    TextView user_name;
    AvatarImageView aiv;
    ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_account, container, false);
        logOut=(Button)v.findViewById(R.id.button6);
        myBookings=(Button)v.findViewById(R.id.button2);
        manageAccount=(Button)v.findViewById(R.id.button3);
        shareUs=(Button)v.findViewById(R.id.button5);
        privacyPolicy=(Button)v.findViewById(R.id.button4);
        callus=(Button)v.findViewById(R.id.callus);
        user_name=(TextView)v.findViewById(R.id.user);
        aiv = (AvatarImageView)v.findViewById(R.id.item_avatar);
        aiv.setImageResource(R.drawable.person);
        String img=Paper.book().read("profile_image");
        if(img!=null)
            Picasso.with(getActivity()).load(img).into(aiv);

        String name=Paper.book().read("user_name","User");
        if(name!=null)
            user_name.setText(name);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Paper.book().delete("token");
                String img=Paper.book().read("profile_image");
                if(img!=null)
                    Paper.book().delete("profile_image");
                Paper.book().delete("email");
                Paper.book().delete("mobile");
                FacebookSdk.sdkInitialize(getActivity());
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
        myBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PastBooking.class));
            }
        });
        manageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ManageAccount.class));
            }
        });
        shareUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShareUsActivity.class));
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class));
            }
        });
        callus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int checkPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            100);
                } else {
                    callZaafoo();
                }

            }
        });
        return v;
    }

    private void callZaafoo() {
        final String[] call = new String[1];
        pd=new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("Calling..");
        pd.show();
        AndroidNetworking.post("http://zaafoo.com/varparamread/")
                .addBodyParameter("var_name", "helpline")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array=response.getJSONArray("ret");
                            response=array.getJSONObject(0);
                            call[0] =response.getString("c");
                            pd.dismiss();
                            Intent callIntent=new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+ call[0]));
                            startActivity(callIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                    }
                });


    }


}
