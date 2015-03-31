package com.google.riosport;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.riosport.elements.User;


/**
 * Created by Anthony on 29/11/2014.
 */
public class ParticipantAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<User> participants;
    private LayoutInflater layoutInflater;

    public ParticipantAdapter(Activity activity,ArrayList<User> participants) {
        this.participants = participants;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return participants.size();
    }

    @Override
    public Object getItem(int position) {
        return participants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView==null)
            convertView= layoutInflater.inflate(R.layout.row_list, null);

            TextView NameView = (TextView) convertView.findViewById(R.id.text_list_participant);
            ImageView PhotoView = (ImageView) convertView.findViewById(R.id.image_list_participant);

        User user = participants.get(position);

        NameView.setText(user.getFull_name());
        new LoadProfileImage(PhotoView).execute(participants.get(position).getUrl_avatar());

        return convertView;

    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
         }
    }



}
