package com.google.riosport;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.riosport.elements.Event;
import com.google.riosport.elements.Gender;
import com.google.riosport.elements.User;
import com.google.riosport.webservice.WebServiceException;
import com.google.riosport.webservice.WebServiceInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class Main extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private User user;
    private TextView textdate = null;
    private int year, day, month, day_week, gender;
    protected static String id_user, picture, monthString, dayString, txtName, txtEmail, birthday, urlpicture = null; //private normalement

    protected static boolean sign_out = false;

    private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;
    private static final String TAG = "Main";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

    private TextView mSignInStatus;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;
    private ConnectionResult mConnectionResult;
    private ImageView imgProfilePic, christ;
    private com.google.riosport.customTextView titlefrag1;
    private com.google.riosport.customTextView titlefrag2;
    public static int id_riosport, is_user;
    public static ArrayList<String> sport;
    public static Boolean init_fragment, destroyed_sport, destroyed_visibility=false;
    public static Boolean first_create_map=true;
    public static ArrayList<Event> list_event = null;
    public static ArrayList<Event> list_event_feed = null;
    public static ArrayList<Event> list_event_feed_private = null;
    public static Integer[] imageId = {R.drawable.basket, R.drawable.football, R.drawable.volley};
    public static Event event_feed;
    public static Boolean reload_feed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("LOGOUT", false))
        {
            finish();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //final Animation FadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);

        //setCurrentDate();
        try {
            sport = WebServiceInterface.getSports();
        } catch (WebServiceException e) {
            e.printStackTrace();
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, Plus.PlusOptions.builder()
                        .addActivityTypes("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity").build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mSignInStatus = (TextView) findViewById(R.id.sign_in_status);
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        imgProfilePic = (ImageView) findViewById(R.id.imagepic);


        christ = (ImageView) findViewById(R.id.christ);
        titlefrag1 = (com.google.riosport.customTextView) findViewById(R.id.titleFrag1);
        titlefrag2 = (com.google.riosport.customTextView) findViewById(R.id.titleFrag2);
        //titlefrag1.startAnimation(FadeIn);
        //titlefrag2.startAnimation(FadeIn);
        customActionBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.sign_in_button:
                int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if (available != ConnectionResult.SUCCESS) {
                    showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
                    return;
                }

                try {
                    mSignInStatus.setText(getString(R.string.signing_in_status));
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    // Fetch a new result to start.
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
            return super.onCreateDialog(id);
        }

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return null;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
        }
        return new AlertDialog.Builder(this)
                .setMessage(R.string.plus_generic_error)
                .setCancelable(true)
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SIGN_IN
                || requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
            if (resultCode == RESULT_CANCELED) {
                mSignInStatus.setText(getString(R.string.signed_out_status));
            } else if (resultCode == RESULT_OK && !mGoogleApiClient.isConnected()
                    && !mGoogleApiClient.isConnecting()) {
                // This time, connect should succeed.
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (sign_out) {
            mSignInStatus.setVisibility(View.VISIBLE);
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            sign_out = false;
        }else{
            mSignInStatus.setVisibility(View.INVISIBLE);
            getProfileInformation();
            try {
                is_user = WebServiceInterface.isUser("google+",id_user);
                if (is_user==1){
                    init_fragment=true;
                }
                if (birthday == null && is_user==0) {
                    AlertDialog.Builder box;
                    final EditText input = new EditText(this);
                    box = new AlertDialog.Builder(this);
                    box.setView(input);
                    box.setTitle("No date of birth found");
                    box.setIcon(R.drawable.rs);
                    box.setMessage("Enter your date of birth (yyyy-mm-dd)");

                    box.setPositiveButton("Validate", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    user.setDate_of_birth(input.getText().toString());
                                    try {
                                        id_riosport=WebServiceInterface.addUser(id_user, "google+", user);
                                    } catch (WebServiceException e) {
                                        e.printStackTrace();
                                    }
                                    updateButtons(true /* isSignedIn */);
                                    Intent intent = new Intent(Main.this, MyFragment.class);
                                    startActivityForResult(intent, 10);
                                }
                            }
                    );

                    box.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                    user.setDate_of_birth("0000-00-00");
                                    try {
                                        id_riosport=WebServiceInterface.addUser(id_user, "google+", user);
                                    } catch (WebServiceException e) {
                                        e.printStackTrace();
                                    }
                                    updateButtons(true /* isSignedIn */);
                                    Intent intent = new Intent(Main.this, MyFragment.class);
                                    startActivityForResult(intent, 10);
                                }
                            }
                    );

                    final AlertDialog dialog = box.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (input.getText().toString().matches("[1-2][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]") && input.length()>0){
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }else{
                            Toast toast = Toast.makeText(getBaseContext(), "Wrong format, follow indications", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                            }

                        }
                    });
                }else if (is_user==1){
                    id_riosport=WebServiceInterface.getInternalId("google+",id_user);
                    updateButtons(true /* isSignedIn */);
                    Intent intent = new Intent(Main.this, MyFragment.class);
                    startActivityForResult(intent, 10);
                }else{
                    user.setDate_of_birth(birthday);
                    try {
                        id_riosport=WebServiceInterface.addUser(id_user, "google+", user);
                    } catch (WebServiceException e) {
                        e.printStackTrace();
                    }
                    updateButtons(true /* isSignedIn */);
                    Intent intent = new Intent(Main.this, MyFragment.class);
                    startActivityForResult(intent, 10);
                }
            } catch (WebServiceException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onConnectionSuspended(int cause) {
        mSignInStatus.setText(R.string.loading_status);
        mGoogleApiClient.connect();
        updateButtons(false /* isSignedIn */);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        mConnectionResult = result;
        updateButtons(false /* isSignedIn */);
    }

    private void updateButtons(boolean isSignedIn) {
        if (isSignedIn) {
            mSignInButton.setVisibility(View.INVISIBLE);
        } else {
            if (mConnectionResult == null) {
                // Disable the sign-in button until onConnectionFailed is called with result.
                mSignInButton.setVisibility(View.INVISIBLE);
                mSignInStatus.setText(getString(R.string.loading_status));
            } else {
                // Enable the sign-in button since a connection result is available.
                mSignInButton.setVisibility(View.VISIBLE);
                mSignInStatus.setText(getString(R.string.signed_out_status));
            }
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String id = currentPerson.getId();
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String birth = currentPerson.getBirthday();
                int gend = currentPerson.getGender();

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl + ", id: " + id + ", birth: " + birth + ", gender: " + gender);

                Gender gender1;
                if (gender == 0){
                    gender1 = Gender.male;
                }else{
                    gender1 = Gender.female;
                }
                txtEmail = email;
                txtName = personName;
                birthday = birth;
                urlpicture = personPhotoUrl;
                id_user = id;
                gender = gend;

                user= new User(personName,personPhotoUrl,gender1,"");
                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + 150;

                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            //bmImage.setImageBitmap(result);
            picture=saveToInternalSorage(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
                if (mGoogleApiClient.isConnected()) {
                    mSignInStatus.setVisibility(View.VISIBLE);
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                    sign_out = false;
                }
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


            //case R.id.action_settings:
                //break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(TAG,TAG);
    }

    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.png");

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 90, fos);


            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
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
    public void setCurrentDate() {
        //textdate = (TextView)findViewById(R.id.day);
        final Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        month++;
        switch (month) {
            case 1:  monthString = getString(R.string.jan);
                break;
            case 2:  monthString = getString(R.string.feb);
                break;
            case 3:  monthString = getString(R.string.mar);
                break;
            case 4:  monthString = getString(R.string.apr);
                break;
            case 5:  monthString = getString(R.string.may);
                break;
            case 6:  monthString = getString(R.string.jun);
                break;
            case 7:  monthString = getString(R.string.jul);
                break;
            case 8:  monthString = getString(R.string.aug);
                break;
            case 9:  monthString = getString(R.string.sep);
                break;
            case 10: monthString = getString(R.string.oct);
                break;
            case 11: monthString = getString(R.string.nov);
                break;
            case 12: monthString = getString(R.string.dec);
                break;}

        day = calendar.get(Calendar.DAY_OF_MONTH);
        day_week = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day_week) {
            case(Calendar.MONDAY) : dayString = getString(R.string.mon);
                break;
            case(Calendar.TUESDAY) : dayString = getString(R.string.tue);
                break;
            case(Calendar.WEDNESDAY) : dayString = getString(R.string.wed);
                break;
            case(Calendar.THURSDAY) : dayString = getString(R.string.thu);
                break;
            case(Calendar.FRIDAY) : dayString = getString(R.string.fri);
                break;
            case(Calendar.SATURDAY) : dayString = getString(R.string.sat);
                break;
            case(Calendar.SUNDAY) : dayString = getString(R.string.sun);
                break;
        }

        // set current date into textview
        textdate.setText(new StringBuilder()
                // Month is 0 based, so you have to add 1
                .append(dayString).append(" ")
                .append(day).append(" ")
                .append(monthString).append(" ")
                .append(year).append(" "));
        // set current date into Date Picker
    }


}
