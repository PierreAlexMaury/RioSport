package com.google.riosport;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.riosport.elements.User;
import com.google.riosport.webservice.WebServiceException;
import com.google.riosport.webservice.WebServiceInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class Print_Event extends Activity {

    private ListView list_view_participant;
    private CheckBox participation;
    private ArrayList<User> list_participants = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_event);

        list_view_participant = (ListView) findViewById(R.id.listView1);

        try {

            list_participants = WebServiceInterface.getInvolvedUsers(Main.event_feed.getId_event());
            list_view_participant.setAdapter(new ParticipantAdapter(this, list_participants));

        } catch (WebServiceException no_event) {
            no_event.printStackTrace();
        }

        TextView event_description = (TextView) findViewById(R.id.eventDescription);
        event_description.setText(Main.event_feed.getDescription());

        TextView event_name = (TextView) findViewById(R.id.name);
        event_name.setText(Main.event_feed.getSport());

        TextView event_localization = (TextView) findViewById(R.id.localization);
        event_localization.setText(Main.event_feed.getLocation());

        TextView event_timestamp = (TextView) findViewById(R.id.timestamp);
        event_timestamp.setText(Main.event_feed.getDateTime());

        ImageView event_pic = (ImageView) findViewById(R.id.sportPic);
        event_pic.setImageResource(ImageSport(Main.event_feed.getSport()));

        participation= (CheckBox) findViewById(R.id.participate);

        participation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getBaseContext(), "You're going to this event!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(), "You don't go to this event...", Toast.LENGTH_SHORT).show();
                }
            }
        });

       customActionBar();

    }


    public int ImageSport(String name){
        int id_image_sport;

        if (name.equals("Football"))
            id_image_sport = Main.imageId[1];
        else if(name.equals("Volleyball"))
            id_image_sport = Main.imageId[2];
        else
            id_image_sport = Main.imageId[0];
        return id_image_sport;
    }

    public void customActionBar(){

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
