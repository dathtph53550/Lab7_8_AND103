package com.example.and103_lab5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.and103_lab5.R;
import com.example.and103_lab5.model.District;


import java.util.ArrayList;

public class Adapter_Item_District_Select_GHN extends ArrayAdapter<District> {
    private Context context;
    private ArrayList<District> list;

    public Adapter_Item_District_Select_GHN(@NonNull Context context, ArrayList<District> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_district_spinner, parent, false);
        }

        TextView textViewProvinceName = convertView.findViewById(R.id.textViewDistrictName);

        District currentItem = getItem(position);

        if (currentItem != null) {
            textViewProvinceName.setText(currentItem.getDistrictName());
        }
        return convertView;
    }
}
