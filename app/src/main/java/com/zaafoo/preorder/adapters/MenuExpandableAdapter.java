package com.zaafoo.preorder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.zaafoo.preorder.R;
import com.zaafoo.preorder.models.Cuisine;
import com.zaafoo.preorder.models.Menu;

import java.util.ArrayList;

/**
 * Created by SUB on 3/30/2017.
 */

public class MenuExpandableAdapter extends BaseExpandableListAdapter {

    ArrayList<Cuisine> cuisineList;
    Context context;

    public MenuExpandableAdapter(ArrayList<Cuisine> cuisineList, Context context) {
        this.cuisineList = cuisineList;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return cuisineList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return cuisineList.get(groupPosition).getMenus().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return cuisineList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return cuisineList.get(groupPosition).getMenus().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Cuisine cuisine = (Cuisine) this.getGroup(groupPosition);
        String title = cuisine.getName();

        if (convertView == null) {
            LayoutInflater infalter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalter.inflate(R.layout.cuisine_layout, null);
        }

        TextView heading = (TextView) convertView.findViewById(R.id.cuisine_name);
        heading.setText(title);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Menu menu = (Menu) this.getChild(groupPosition, childPosition);
        String menu_name = menu.getName();
        String menu_price = menu.getPrice();
        if (convertView == null) {
            LayoutInflater infalter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalter.inflate(R.layout.menu_child_item, null);
        }

        TextView menuName = (TextView) convertView.findViewById(R.id.menu_item_name);
        TextView menuPrice = (TextView) convertView.findViewById(R.id.menu_item_price);
        menuName.setText(menu_name);
        menuPrice.setText("Rs." + menu_price);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}