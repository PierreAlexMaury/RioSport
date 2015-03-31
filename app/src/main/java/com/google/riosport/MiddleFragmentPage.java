package com.google.riosport;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.riosport.elements.Event;
import com.google.riosport.webservice.WebServiceException;
import com.google.riosport.webservice.WebServiceInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MiddleFragmentPage extends Fragment {
    private ImageView testimage;
    private List<FeedItem> feedItems;
    private ListView listView;
    private FeedListAdapter listAdapter;
    private WebServiceInterface WSI = new WebServiceInterface();
    private  Event event_null = new Event();
    private int first_feed_private = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        if (Main.reload_feed){
            try {
                Main.list_event_feed = (ArrayList<Event>) WSI.getAvailableEvents(Event.PUBLIC, Main.sport, Main.id_riosport).clone();

            } catch (WebServiceException e) {
                e.printStackTrace();
            }
            Main.reload_feed = false;
        }

        View middleview = inflater.inflate(R.layout.middle_page_layout, container, false);
        listView = (ListView) middleview.findViewById(R.id.list);

        try {
            Main.list_event_feed_private = WSI.getAvailableEvents(Event.PRIVATE, Main.sport, Main.id_riosport);

        } catch (WebServiceException e) {
            e.printStackTrace();
        }

        if(!(Main.list_event_feed_private.isEmpty())){
            for (Event e : Main.list_event_feed_private) {
                Main.list_event_feed.add(e);
            }
        }
        Main.list_event_feed.add(0,event_null);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);

        CreateItems(Main.list_event_feed);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position==0 || position == first_feed_private){
                //null
                }else {
                    Main.event_feed = Main.list_event_feed.get(position);
                    Intent intent = new Intent(getActivity(), Print_Event.class);
                    startActivityForResult(intent, 10);
                }
            }
        });

        return middleview;

    }

    public void CreateItems (ArrayList<Event> list_event){
        FeedItem item_ini = new FeedItem();
        item_ini.setId(Main.list_event_feed.get(0).getId_event());
        item_ini.setName("           "+"PUBLIC EVENTS");
        feedItems.add(item_ini);

        for (Event e : list_event){
            if (e.getId_event() != -1) {
                FeedItem item = new FeedItem();
                item.setId(e.getId_event());
                item.setName(e.getSport() + " - " + e.getLocation());

                item.setSportPic(ImageSport(e.getSport()));
                item.setStatus(e.getDescription());
                item.setTimeStamp(e.getDateTime());

                feedItems.add(item);
            if(e.getVisibility().equals("private") && first_feed_private == -1){
                first_feed_private = list_event.indexOf(e);
            }
            }
        }
        if (first_feed_private != -1) {
            Main.list_event_feed.add(first_feed_private, event_null);
            FeedItem item_private = new FeedItem();
            item_private.setId(Main.list_event_feed.get(first_feed_private).getId_event());
            item_private.setName("           " + "PRIVATE EVENTS");
            feedItems.add(first_feed_private, item_private);
        }
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