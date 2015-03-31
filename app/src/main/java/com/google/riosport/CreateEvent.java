package com.google.riosport;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.riosport.elements.Event;
import com.google.riosport.webservice.WebServiceException;
import com.google.riosport.webservice.WebServiceInterface;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class CreateEvent extends FragmentActivity {

    private Event event;
    private ListView list;
    private TextView title, type, duration, day_choice, hour_choice, minutes_choice, description;
    private RadioGroup group;
    private NumberPicker day_picker, hour_picker, min_picker;
    private EditText description_text;
    private Button next;
    private DatePicker date;
    private TimePicker time;
    private AutoCompleteTextView complete_place;
    private DownloadTask placesDownloadTask, placeDetailsDownloadTask;
    private ParserTask placesParserTask, placeDetailsParserTask;
    private GoogleMap map;
    private final int PLACES=0;
    private final int PLACES_DETAILS=1;
    private int STEP, year, day, month = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        customActionBar();
        final Animation FadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        final Animation FadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        group = (RadioGroup) findViewById(R.id.group);
        title = (TextView) findViewById(R.id.title);
        type = (TextView) findViewById(R.id.type);
        duration = (TextView) findViewById(R.id.duration);
        day_choice = (TextView) findViewById(R.id.day);
        hour_choice = (TextView) findViewById(R.id.hour);
        minutes_choice = (TextView) findViewById(R.id.min);
        description = (TextView) findViewById(R.id.description);
        next = (Button) findViewById(R.id.next_step);
        day_picker = (NumberPicker) findViewById(R.id.day_picker);
        hour_picker = (NumberPicker) findViewById(R.id.hour_picker);
        min_picker = (NumberPicker) findViewById(R.id.min_picker);
        date = (DatePicker) findViewById(R.id.datePicker);
        time = (TimePicker) findViewById(R.id.timePicker);
        description_text = (EditText) findViewById(R.id.description_text);

        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        date.init(year, month, day, null);
        complete_place = (AutoCompleteTextView) findViewById(R.id.auto_complete);
        complete_place.setThreshold(1);
        final SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getView().setVisibility(View.INVISIBLE);
        map=fm.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        event = new Event();
        ListCustom adapter = new ListCustom(this, Main.sport, Main.imageId);
        list=(ListView)findViewById(R.id.choose_sport);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                event.setSport(Main.sport.get(position));
                list.startAnimation(FadeOut);
                list.setVisibility(View.INVISIBLE);
                title.startAnimation(FadeOut);
                title.setVisibility(View.INVISIBLE);
                title.startAnimation(FadeIn);
                title.setVisibility(View.VISIBLE);
                title.setText("Where?");
                complete_place.setVisibility(View.VISIBLE);
                fm.getView().setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
            }
        });
        complete_place.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return false;
            }
        });
        complete_place.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Creating a DownloadTask to download Google Places matching "s"
                placesDownloadTask = new DownloadTask(PLACES);

                // Getting url to the Google Places Autocomplete api
                String url = getAutoCompleteUrl(charSequence.toString());

                // Start downloading Google Places
                // This causes to execute doInBackground() of DownloadTask class
                placesDownloadTask.execute(url);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });

        complete_place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView lv = (ListView) adapterView;
                SimpleAdapter adapter = (SimpleAdapter) adapterView.getAdapter();
                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(i);
                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);
                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));
                // Start downloading Google Place Details
                // This causes to execute doInBackground() of DownloadTask class
                placeDetailsDownloadTask.execute(url);
                event.setLocation(complete_place.getText().toString());
                next.setEnabled(true);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (STEP==0) {
                    complete_place.startAnimation(FadeOut);
                    complete_place.setVisibility(View.INVISIBLE);
                    fm.getView().startAnimation(FadeOut);
                    fm.getView().setVisibility(View.INVISIBLE);
                    title.startAnimation(FadeOut);
                    title.setVisibility(View.INVISIBLE);
                    title.startAnimation(FadeIn);
                    title.setVisibility(View.VISIBLE);
                    title.setText("When?");
                    date.startAnimation(FadeIn);
                    date.setVisibility(View.VISIBLE);
                    time.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                    time.setCurrentMinute(calendar.get(Calendar.MINUTE));
                    time.startAnimation(FadeIn);
                    time.setVisibility(View.VISIBLE);

                    date.setMinDate(System.currentTimeMillis() - 100000);

                    STEP=1;
                    next.setEnabled(false);
                }else if (STEP==1){
                    next.setText("Create event");
                    next.setEnabled(false);
                    date.startAnimation(FadeOut);
                    date.setVisibility(View.INVISIBLE);
                    time.startAnimation(FadeOut);
                    time.setVisibility(View.INVISIBLE);
                    title.startAnimation(FadeOut);
                    title.setVisibility(View.INVISIBLE);
                    title.startAnimation(FadeIn);
                    title.setVisibility(View.VISIBLE);
                    title.setText("Informations");
                    type.startAnimation(FadeIn);
                    type.setVisibility(View.VISIBLE);
                    group.startAnimation(FadeIn);
                    group.setVisibility(View.VISIBLE);
                    duration.startAnimation(FadeIn);
                    duration.setVisibility(View.VISIBLE);
                    day_choice.startAnimation(FadeIn);
                    day_choice.setVisibility(View.VISIBLE);
                    day_picker.startAnimation(FadeIn);
                    day_picker.setVisibility(View.VISIBLE);
                    hour_choice.startAnimation(FadeIn);
                    hour_choice.setVisibility(View.VISIBLE);
                    hour_picker.startAnimation(FadeIn);
                    hour_picker.setVisibility(View.VISIBLE);
                    minutes_choice.startAnimation(FadeIn);
                    minutes_choice.setVisibility(View.VISIBLE);
                    min_picker.startAnimation(FadeIn);
                    min_picker.setVisibility(View.VISIBLE);
                    description.startAnimation(FadeIn);
                    description.setVisibility(View.VISIBLE);
                    description_text.startAnimation(FadeIn);
                    description_text.setVisibility(View.VISIBLE);

                    day_picker.setMinValue(0);
                    day_picker.setMaxValue(30);
                    hour_picker.setMinValue(0);
                    hour_picker.setMaxValue(23);
                    min_picker.setMinValue(0);
                    min_picker.setMaxValue(59);
                    STEP=2;
                }else if (STEP == 2){
                    try {
                        WebServiceInterface.addEvent(event);
                    } catch (WebServiceException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getBaseContext(), "Event Created!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateEvent.this, MyFragment.class);
                    Main.reload_feed = true;
                    startActivityForResult(intent, 10);
                }
            }
        });

        date.init(date.getYear(), date.getMonth(), date.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                event.setDay(d);
                event.setYear(y);
                event.setMonth(m);
            }
        });

        time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int h, int m) {
                if (h < calendar.get(Calendar.HOUR_OF_DAY) || ((m < calendar.get(Calendar.MINUTE)) && (h == calendar.get(Calendar.HOUR_OF_DAY)))) {
                    Toast.makeText(getBaseContext(), "Do not select a hour before the current hour!", Toast.LENGTH_SHORT).show();
                    next.setEnabled(false);
                } else {
                    event.setHour(h);
                    event.setMinute(m);
                    next.setEnabled(true);
                }
            }
        });

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.private_button){
                    try {
                        event.setVisibility(Event.PRIVATE);
                    } catch (WebServiceException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        event.setVisibility(Event.PUBLIC);
                    } catch (WebServiceException e) {
                        e.printStackTrace();
                    }
                }
                next.setEnabled(true);
            }
        });

        day_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old_value, int new_value) {
                event.setDurationDay(new_value);
            }
        });

        hour_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old_value, int new_value) {
                event.setDurationHour(new_value);
            }
        });

        min_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old_value, int new_value) {
                event.setDurationMinute(new_value);
            }
        });

        description_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                event.setDescription(editable.toString());
            }
        });

        event.setOwner(Main.id_riosport);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
      super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.deconnect:
                Toast.makeText(getBaseContext(), "Sign out ...", Toast.LENGTH_SHORT).show();
                Main.sign_out=true;
                Intent intent = new Intent(CreateEvent.this, Main.class);
                startActivity(intent);
                break;
            case R.id.quit:
                AlertDialog.Builder box = new AlertDialog.Builder(this);
                box.setIcon(R.drawable.rs);
                box.setTitle("Quit RioSport ?");
                box.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), Main.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("LOGOUT", true);
                                startActivity(intent);
                            }
                        }
                );

                box.setNegativeButton("No", null);
                box.show();
                break;
        }
        return true;
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

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        int parserType = 0;

        public ParserTask(int type){
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try{
                jObject = new JSONObject(jsonData[0]);

                switch(parserType){
                    case PLACES :
                        PlaceJsonParser placeJsonParser = new PlaceJsonParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS :
                        PlaceDetailsJsonParser placeDetailsJsonParser = new PlaceDetailsJsonParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                }

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            switch(parserType){
                case PLACES :
                    String[] from = new String[] { "description"};
                    int[] to = new int[] { android.R.id.text1 };

                    // Creating a SimpleAdapter for the AutoCompleteTextView
                    SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

                    // Setting the adapter
                    complete_place.setAdapter(adapter);
                    break;
                case PLACES_DETAILS :
                    HashMap<String, String> hm = result.get(0);

                    // Getting latitude from the parsed data
                    double latitude = Double.parseDouble(hm.get("lat"));

                    // Getting longitude from the parsed data
                    double longitude = Double.parseDouble(hm.get("lng"));

                    LatLng point = new LatLng(latitude, longitude);

                    CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(point,15);

                    // Showing the user input location in the Google Map
                    map.moveCamera(cameraPosition);
                    map.animateCamera(cameraPosition);

                    MarkerOptions options = new MarkerOptions();
                    options.position(point);
                    options.title("Position");
                    options.snippet("Latitude:"+latitude+",Longitude:"+longitude);

                    // Adding the marker in the Google Map
                    map.addMarker(options);
                    event.setLatitude(latitude);
                    event.setLongitude(longitude);
                    break;
            }
        }
    }

    private String getAutoCompleteUrl(String place){
        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyA2toFv4UxuibiRt4z68eyM7C8SAJeuCfc";
        // place to be be searched
        String input="";
        try {
            input = "input=" + URLEncoder.encode(place, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        // place type to be searched
        //String types = "types=(regions)";
        // Sensor enabled
        String sensor = "sensor=true";
        // Building the parameters to the web service
        String parameters = input+"&"+sensor+"&"+key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

        return url;
    }

    private String getPlaceDetailsUrl(String ref){
        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyA2toFv4UxuibiRt4z68eyM7C8SAJeuCfc";
        // reference of place
        String reference = "reference="+ref;
        // Sensor enabled
        String sensor = "sensor=true";
        // Building the parameters to the web service
        String parameters = reference+"&"+sensor+"&"+key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;

        return url;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        private int downloadType=0;
        // Constructor
        public DownloadTask(int type){
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch(downloadType){
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);
                    // Start parsing google places json data
                    // This causes to execute doInBackground() of ParserTask class
                    placesParserTask.execute(result);
                    break;
                case PLACES_DETAILS :
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
            }
        }
    }
}


