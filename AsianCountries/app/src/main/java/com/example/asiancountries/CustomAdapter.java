package com.example.asiancountries;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> country;
    private final ArrayList<String> capital;

    public CustomAdapter(Activity context,ArrayList<String> country,ArrayList<String> capital)
    {
        super(context,R.layout.list_view_country_list,country);
        this.context=context;
        this.country=country;
        this.capital=capital;
    }

    public View getView(int position, View view, ViewGroup viewGroup)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_view_country_list,null,true);
        TextView category1=rowView.findViewById(R.id.country);
        TextView desc1=rowView.findViewById(R.id.capital);
        category1.setText(country.get(position));
        desc1.setText(capital.get(position));
        return rowView;

    }

}
