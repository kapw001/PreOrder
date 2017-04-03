package com.zaafoo.preorder.fragments;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.ShareUsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfferFragment extends Fragment {


    ImageView offer;
    public OfferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_offer, container, false);
        offer=(ImageView)v.findViewById(R.id.imageView4);
        Picasso.with(getActivity()).load("http://zaafoo.com/static/offer.jpg").into(offer);
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShareUsActivity.class));
            }
        });
        return  v;
    }

}
