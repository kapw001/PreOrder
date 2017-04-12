package com.zaafoo.preorder.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.ManageAccount;
import com.zaafoo.preorder.activities.PastBooking;
import com.zaafoo.preorder.activities.PrivacyPolicyActivity;
import com.zaafoo.preorder.activities.ShareUsActivity;
import com.zaafoo.preorder.activities.SignUpActivity;

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
    TextView user_name;
    AvatarImageView aiv;
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


        return v;
    }

}
