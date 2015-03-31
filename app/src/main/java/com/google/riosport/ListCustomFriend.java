package com.google.riosport;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by pierre-alexandremaury on 11/11/2014.
 */
public class ListCustomFriend extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] profile;
    private final Bitmap[] image;
    private final Button friend_button;

    public ListCustomFriend(Activity context,
                      String[] profile, Bitmap[] image, Button friend_button ) {
        super(context, R.layout.list_custom_friend, profile);
        this.context = context;
        this.profile = profile;
        this.image = image;
        this.friend_button = friend_button;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_custom_friend, null, true);
        rowView.computeScroll();
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text_list_friend);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_list_friend);
        Button friend_button = (Button) rowView.findViewById(R.id.friend_button);
        txtTitle.setText(profile[position]);
        imageView.setImageBitmap(image[position]);
        friend_button.setText(R.string.addfriend);
        return rowView;
    }
}

