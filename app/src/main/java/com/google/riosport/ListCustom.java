package com.google.riosport;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pierre-alexandremaury on 05/11/2014.
 */
public class ListCustom extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> sport;
    private final Integer[] imageId;

    public ListCustom(Activity context,
                      ArrayList<String> sport, Integer[] imageId ) {
        super(context, R.layout.list_custom, sport);
        this.context = context;
        this.sport = sport;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_custom, null, true);
        rowView.computeScroll();
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text_list);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_list);
        txtTitle.setText(sport.get(position));
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
