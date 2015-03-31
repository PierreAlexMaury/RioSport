package com.google.riosport;



import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.riosport.elements.Event;
import com.google.riosport.elements.Practiced;
import com.google.riosport.webservice.WebServiceException;
import com.google.riosport.webservice.WebServiceInterface;

import java.util.ArrayList;


public class RightFragmentPage extends Fragment {

    private GoogleMap map;
    private MapView mapView;
    private Location user_location;
    private String provider;
    private RadioGroup group_seen, group_sport;
    private RadioButton all_sports,my_sports,public_button, private_button;
    private ArrayList<Marker> list_marker = new ArrayList<Marker>();

    private WebServiceInterface WSI = new WebServiceInterface();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rightview = inflater.inflate(R.layout.right_page_layout, container, false);
        mapView = (MapView) rightview.findViewById(R.id.map);
        group_seen = (RadioGroup) rightview.findViewById(R.id.group_seen);
        group_sport = (RadioGroup) rightview.findViewById(R.id.group_sport);
        all_sports = (RadioButton) rightview.findViewById(R.id.all_button_sport);
        my_sports = (RadioButton) rightview.findViewById(R.id.my_button_sport);
        public_button = (RadioButton) rightview.findViewById(R.id.public_button_map);
        private_button = (RadioButton) rightview.findViewById(R.id.private_button_map);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        MapsInitializer.initialize(getActivity());
        map.setMyLocationEnabled(true);

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = lm.getBestProvider(criteria, false);
        user_location = lm.getLastKnownLocation(provider);
        if (user_location != null) {
            double longitude = user_location.getLongitude();
            double latitude = user_location.getLatitude();
            LatLng point = new LatLng(latitude, longitude);
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(point, 13);
            map.moveCamera(cameraPosition);
            map.animateCamera(cameraPosition);
        }

        group_seen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(!(Main.list_event == null) && !Main.destroyed_visibility) {
                    removeMarkers(list_marker);
                    if ((i == R.id.public_button_map) && (all_sports.isChecked())) {
                        try {
                            Main.list_event = WSI.getAvailableEvents(Event.PUBLIC, Main.sport, Main.id_riosport);
                        } catch (WebServiceException e) {
                            e.printStackTrace();
                        }

                    } else if ((i == R.id.public_button_map) && (my_sports.isChecked())) {
                        try {
                            ArrayList<Practiced> list_practised;
                            ArrayList<String> list_sport = new ArrayList<String>();

                            list_practised = WebServiceInterface.getPracticedSports(Main.id_riosport);
                            if(!list_practised.isEmpty()) {

                                for (Practiced p : list_practised) {
                                    list_sport.add(p.getSport());
                                }

                                Main.list_event = WSI.getAvailableEvents(Event.PUBLIC, list_sport, Main.id_riosport);

                            }else {
                                Main.list_event.clear();
                            }

                        } catch (WebServiceException e) {
                            e.printStackTrace();
                        }

                    } else if ((i == R.id.private_button_map) && (all_sports.isChecked())) {
                        try {
                            Main.list_event = WSI.getAvailableEvents(Event.PRIVATE, Main.sport, Main.id_riosport);

                        } catch (WebServiceException e) {
                            e.printStackTrace();
                        }

                    } else if ((i == R.id.private_button_map) && (my_sports.isChecked())){
                        try {
                            ArrayList<Practiced> list_practised;
                            ArrayList<String> list_sport = new ArrayList<String>();

                            list_practised = WebServiceInterface.getPracticedSports(Main.id_riosport);
                            if(!list_practised.isEmpty()) {

                                for (Practiced p : list_practised) {
                                    list_sport.add(p.getSport());
                                }

                                Main.list_event = WSI.getAvailableEvents(Event.PRIVATE, list_sport, Main.id_riosport);

                            }else {
                                Main.list_event.clear();
                            }

                        } catch (WebServiceException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //nothing to do
                    }

                    for (Event e : Main.list_event) {
                        LatLng l = new LatLng(e.getLatitude(), e.getLongitude());
                        put_marker(l, e);
                    }
                }else{
                    Main.destroyed_visibility=false;
                }
            }});

        group_sport.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(!(Main.list_event==null) && !Main.destroyed_sport) {
                    removeMarkers(list_marker);

                    if ((i == R.id.all_button_sport) && (public_button.isChecked())) {
                        try {
                            Main.list_event = WSI.getAvailableEvents(Event.PUBLIC, Main.sport, Main.id_riosport);

                        } catch (WebServiceException e) {
                            e.printStackTrace();
                        }

                    } else if ((i == R.id.my_button_sport) && (public_button.isChecked())) {
                        try {

                            ArrayList<Practiced> list_practised;
                            ArrayList<String> list_sport = new ArrayList<String>();

                            list_practised = WebServiceInterface.getPracticedSports(Main.id_riosport);
                            if(!list_practised.isEmpty()) {

                                for (Practiced p : list_practised) {
                                    list_sport.add(p.getSport());
                                }

                                Main.list_event = WSI.getAvailableEvents(Event.PUBLIC, list_sport, Main.id_riosport);

                            }else {
                                Main.list_event.clear();
                            }

                        } catch (WebServiceException e) {
                            e.printStackTrace();
                        }

                    } else if ((i == R.id.all_button_sport) && (private_button.isChecked())) {
                        try {
                            Main.list_event = WSI.getAvailableEvents(Event.PRIVATE, Main.sport, Main.id_riosport);

                        } catch (WebServiceException e) {
                            e.printStackTrace();
                        }

                    } else if ((i == R.id.my_button_sport) && (private_button.isChecked())) {
                        try {
                            ArrayList<Practiced> list_practised;
                            ArrayList<String> list_sport = new ArrayList<String>();

                            list_practised = WebServiceInterface.getPracticedSports(Main.id_riosport);
                            if(!list_practised.isEmpty()) {

                                for (Practiced p : list_practised) {
                                    list_sport.add(p.getSport());
                                }

                                Main.list_event = WSI.getAvailableEvents(Event.PRIVATE, list_sport, Main.id_riosport);

                            }else {
                                Main.list_event.clear();
                            }

                        } catch (WebServiceException e) {
                            e.printStackTrace();
                        }
                    } else{
                        //nothing to do
                    }
                    for (Event e : Main.list_event) {
                        LatLng l = new LatLng(e.getLatitude(), e.getLongitude());
                        put_marker(l, e);
                    }
                }else{
                    Main.destroyed_sport=false;
                }
            }
        });

        if(Main.first_create_map==true){
           public_button.setChecked(true);
           all_sports.setChecked(true);
            try {
                Main.list_event = WSI.getAvailableEvents(Event.PUBLIC,Main.sport,Main.id_riosport);
                Main.list_event_feed=Main.list_event;
                for (Event e : Main.list_event){
                    LatLng l = new LatLng(e.getLatitude(),e.getLongitude());
                    put_marker(l,e);
                }
                Main.first_create_map=false;
            } catch (WebServiceException e) {
                e.printStackTrace();
            }

        }else{
            if(!(Main.list_event==null)){
                for (Event e : Main.list_event) {
                    LatLng l = new LatLng(e.getLatitude(), e.getLongitude());
                    put_marker(l, e);
                }
            }
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                boolean find = false;
                if(Main.list_event != null) {
                    for (Event e : Main.list_event) {
                        LatLng l = new LatLng(e.getLatitude(), e.getLongitude());
                        if (l.equals(marker.getPosition())) {
                            find = true;
                            Main.event_feed = e;
                            Intent intent = new Intent(getActivity(), Print_Event.class);
                            startActivityForResult(intent, 10);
                        }
                    }
                }
                if (!find)
                    Toast.makeText(getActivity(), "Page Event not foundd!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return rightview;
    }

    private void put_marker(LatLng l, Event event){
        if (event.getSport().equals("Football")) {
            Marker marker = map.addMarker(new MarkerOptions().position(l).title(event.getSport()).icon(BitmapDescriptorFactory.fromResource(R.drawable.football_icon)));
            list_marker.add(marker);
        } else if (event.getSport().equals("Volleyball")) {
            Marker marker = map.addMarker(new MarkerOptions().position(l).title(event.getSport()).icon(BitmapDescriptorFactory.fromResource(R.drawable.volley_icon)));
            list_marker.add(marker);
        } else {
            Marker marker = map.addMarker(new MarkerOptions().position(l).title(event.getSport()).icon(BitmapDescriptorFactory.fromResource(R.drawable.basket_icon)));
            list_marker.add(marker);
        }
    }

    private void removeMarkers(ArrayList<Marker> list_marker) {
        for (Marker marker : list_marker) {
                marker.remove();
            }
            list_marker.clear();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Main.destroyed_sport=true;
        Main.destroyed_visibility=true;
    }

}