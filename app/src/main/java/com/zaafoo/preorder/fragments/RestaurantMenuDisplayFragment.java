package com.zaafoo.preorder.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.activities.AboutRestaurant;
import com.zaafoo.preorder.adapters.MenuExpandableAdapter;
import com.zaafoo.preorder.models.Cuisine;
import com.zaafoo.preorder.models.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantMenuDisplayFragment extends Fragment {

    JSONArray menu_array;
    JSONObject menu_object;
    ArrayList<Cuisine> cuisineList;
    ArrayList<String> cuisineNameList;
    ArrayList<Menu> menuList;
    MenuExpandableAdapter myadapter;
    ExpandableListView listView;

    public RestaurantMenuDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_restaurant_menu_display, container, false);
        listView=(ExpandableListView)v.findViewById(R.id.menu_display);
        cuisineList=new ArrayList<>();
        menuList=new ArrayList<>();
        cuisineNameList=new ArrayList<>();

        String rest_data= AboutRestaurant.giveRestDatatoFragments();
        parseMenudata(rest_data);
        return v;
    }

    private void parseMenudata(String rest_data) {
        try {
            JSONObject response=new JSONObject(rest_data);
            menu_array=response.getJSONArray("menus");

            // CUISINE LOOP
            for(int j=0;j<menu_array.length();j++){
                menu_object=menu_array.getJSONObject(j);
                if(!cuisineNameList.contains(menu_object.getString("cuisine")))
                    cuisineNameList.add(menu_object.getString("cuisine"));
            }

            for(String key:cuisineNameList)
            {
                for(int i=0;i<menu_array.length();i++){
                    menu_object=menu_array.getJSONObject(i);
                    if(menu_object.getString("cuisine").equalsIgnoreCase(key)) {
                        Menu m=new Menu();
                        String m_name = menu_object.getString("name");
                        String m_price = menu_object.getString("price");
                        String m_id = menu_object.getString("id");
                        m.setId(m_id);
                        m.setName(m_name);
                        m.setPrice(m_price);
                        menuList.add(m);

                    }
                }
                Cuisine c=new Cuisine();
                c.setName(key);
                c.setMenus(menuList);
                cuisineList.add(c);
                menuList=new ArrayList<>();

            }
            myadapter=new MenuExpandableAdapter(cuisineList,getActivity());
            listView.setAdapter(myadapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
