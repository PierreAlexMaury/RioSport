package com.google.riosport;

import android.content.Intent;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LeftFragmentPage extends Fragment {

    ListView list;
    private Button friend;
    String[] profile_txt = {Main.txtName};
    Bitmap[] Image = new Bitmap[1];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View leftview = inflater.inflate(R.layout.left_page_layout, container, false);

        try{
            File f= new File(Main.picture, "profile.png");
            Bitmap image = getCircleBitmap(BitmapFactory.decodeStream(new FileInputStream(f)));
            Image[0]=image;
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        ListCustomFriend adapter = new ListCustomFriend(getActivity(), profile_txt, Image, friend);
        list=(ListView)leftview.findViewById(R.id.list_friend);
        list.setAdapter(adapter);



        return leftview;
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
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

}