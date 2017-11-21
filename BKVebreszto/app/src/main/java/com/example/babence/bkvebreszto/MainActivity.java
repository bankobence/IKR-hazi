package com.example.babence.bkvebreszto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    //sajat valtozoim
    public static double longitude, latitude;
    private DatabaseManager myDB;
    protected List<Stops> stops = new ArrayList<>();
    public Stops activeStop;
    private static final int SONG_REQUEST_CODE = 42;
    private static final int SEARCH_REQUEST_CODE = 1;
    //protected double initialDistance;

    private TextView songName;
    private TextView stopName;
    protected CheckBox cb100;
    protected CheckBox cb1000;
    protected CheckBox cb3000;
    protected CheckBox cbVib;
    protected Switch alarmEnabled;
    protected String displayName = "Csengőhang neve";

    protected boolean alarmIsAlreadyOn;
    protected boolean cb100hasAlreadyPlayed;
    protected boolean cb1000hasAlreadyPlayed;
    protected boolean cb3000hasAlreadyPlayed;


    protected Uri selectedSoundUri = null;
    protected MediaPlayer mediaPlayer;
    protected Vibrator v;

    // Start without a delay
    // Vibrate for 1000 milliseconds
    // Sleep for 1000 milliseconds
    private long[] pattern = {0, 1000, 1000};



    @Override
    protected void onPause(){
        super.onPause();
        if(mediaPlayer != null) {
            mediaPlayer.reset();

        }
        if(v != null){
            v.cancel();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(mediaPlayer != null) {
            mediaPlayer.reset();

        }
        if(v != null){
            v.cancel();
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.reset();

        }
        if(v != null){
            v.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mediaPlayer = new MediaPlayer();

        myDB = new DatabaseManager(getBaseContext());
        final LocationManager locationManagerNet = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmIsAlreadyOn = false;
                if(mediaPlayer != null) {
                    mediaPlayer.reset();
                }
                if(v != null){
                    v.cancel();
                }
            }
        });

        stopName = (TextView) findViewById(R.id.stopNameText);
        stopName.setText(myDB.getStopName());
        stopName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intent, SEARCH_REQUEST_CODE);
            }
        });

        selectedSoundUri = myDB.getUri();
        songName = (TextView) findViewById(R.id.textSongName);
        songName.setText(myDB.getSoundName());
        songName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,SONG_REQUEST_CODE);
            }
        });


        cb100hasAlreadyPlayed = (myDB.getBoolean(DBHelper.COLUMN_DONE_100));
        cb1000hasAlreadyPlayed = (myDB.getBoolean(DBHelper.COLUMN_DONE_1000));
        cb3000hasAlreadyPlayed = (myDB.getBoolean(DBHelper.COLUMN_DONE_3000));

        cb100 = (CheckBox) findViewById(R.id.cb100m);
        cb100.setChecked(myDB.getBoolean(DBHelper.COLUMN_CB_100));
        cb100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                myDB.setBoolean(DBHelper.COLUMN_CB_100, isChecked);
                if(isChecked) {
                    cb100hasAlreadyPlayed = false;
                    myDB.setBoolean(DBHelper.COLUMN_DONE_100, false);
                }
            }
        });

        cb1000 = (CheckBox) findViewById(R.id.cb1km);
        cb1000.setChecked(myDB.getBoolean(DBHelper.COLUMN_CB_1000));
        cb1000.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                myDB.setBoolean(DBHelper.COLUMN_CB_1000, isChecked);
                if(isChecked) {
                    cb1000hasAlreadyPlayed = false;
                    myDB.setBoolean(DBHelper.COLUMN_DONE_1000, false);
                }
            }
        });

        cb3000 = (CheckBox) findViewById(R.id.cb3km);
        cb3000.setChecked(myDB.getBoolean(DBHelper.COLUMN_CB_3000));
        cb3000.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                myDB.setBoolean(DBHelper.COLUMN_CB_3000, isChecked);
                if(isChecked) {
                    cb3000hasAlreadyPlayed = false;
                    myDB.setBoolean(DBHelper.COLUMN_DONE_3000, false);
                }
            }
        });

        cbVib = (CheckBox) findViewById(R.id.cbVibrate);
        cbVib.setChecked(myDB.getBoolean(DBHelper.COLUMN_CB_VIB));
        cbVib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                myDB.setBoolean(DBHelper.COLUMN_CB_VIB, isChecked);

            }
        });



        alarmEnabled = (Switch) findViewById(R.id.enableSwitch);
        alarmEnabled.setChecked(myDB.getBoolean(DBHelper.COLUMN_SW_ENABLE));
        alarmEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    //myDB.getStop();
                    myDB.setBoolean(DBHelper.COLUMN_SW_ENABLE,isChecked);
                    //myDB.getBoolean(DBHelper.COLUMN_SW_ENABLE);
                    if(isChecked) {
                        cb100hasAlreadyPlayed = false;
                        cb1000hasAlreadyPlayed = false;
                        cb3000hasAlreadyPlayed = false;
                        myDB.setBoolean(DBHelper.COLUMN_DONE_100, false);
                        myDB.setBoolean(DBHelper.COLUMN_DONE_1000, false);
                        myDB.setBoolean(DBHelper.COLUMN_DONE_3000, false);
                        /*
                        initialDistance = getDistanceFromLatLonInKm(Double.parseDouble(activeStop.getLatitude()),
                                Double.parseDouble(activeStop.getLongitude()),
                                latitude,
                                longitude);*/
                    }

            }

        });


        /*
        * HA Android 6.0 feletti, fordítva kell valamiért ellenőrizni és engedélyt kérni, ezért a két ág
        */
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)
        {

            if(Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M){
                try {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            1); //ez requestCode, most nem kezelem, barmi lehet};


                } catch (SecurityException e) {
                    Log.e("MYAPP", "exception locManagernel:", e);
                }
            }else {
                locationManagerNet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Log.e("LOC1", "Location elkerve:");
            }

        } else {
            if(Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M){
                locationManagerNet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Log.e("LOC2", "Location elkerve:");
            }else {
                try {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            1); //ez requestCode, most nem kezelem, barmi lehet};


                } catch (SecurityException e) {
                    Log.e("MYAPP", "exception locManagernel:", e);
                }
            }
        }

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Legutóbbi koordinatak: lon:" + longitude + " lat:" + latitude, Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, getAndroidVersion(), Toast.LENGTH_SHORT).show();

            }
        });


        // Remove the listener you previously added
        //locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            activeStop = resultData.getParcelableExtra("Search_data");
            stopName.setText(activeStop.getStopName());
            //Log.e("MainActivity.ListItem", "Az aktív megálló: " + activeStop.getId()+ ", " + activeStop.getStopName());


            myDB.printAllID(); //ez csak ugy ellenorzesnek, hogy mi van elotte az adatbazisban
            myDB.clearTable();
            //myDB.printAllID();

            //Log.e("add Stop", "ID: " + s.id + " ,name: " + s.name + " ,lat: " + s.lat + " ,lon:" + s.lon);
            myDB.addStop(activeStop,
                    alarmEnabled.isChecked(),
                    cb100.isChecked(),
                    cb1000.isChecked(),
                    cb3000.isChecked(),
                    cbVib.isChecked(),
                    selectedSoundUri,
                    displayName,
                    cb100hasAlreadyPlayed,
                    cb1000hasAlreadyPlayed,
                    cb3000hasAlreadyPlayed,
                    getDistanceFromLatLonInKm(Double.parseDouble(activeStop.getLatitude()),
                            Double.parseDouble(activeStop.getLongitude()),
                            latitude,
                            longitude)
                    );

            myDB.printAllID();
            myDB.getStop();
        }

        if (requestCode == SONG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                Log.e("File valasztas", "Uri: " + uri.toString());
                dumpSoundMetaData(uri);
                selectedSoundUri = uri;
/*
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                    //mediaPlayer.prepare();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }*/



            }
        }
    }


    private void playSound(Uri uri){

        try {
            //mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.setDataSource(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }else{
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
            }

            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            /*mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });*/


        } catch (IOException ex) {
            Log.e("MP", "create failed:", ex);
        // fall through
        }

    }

    public void dumpSoundMetaData(Uri uri) {


        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor mCursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mCursor = this.getContentResolver()
                    .query(uri, null, null, null, null, null);
        }

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (mCursor != null && mCursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                displayName = mCursor.getString(
                        mCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.e("File valasztas", "Display Name: " + displayName);
                songName.setText(displayName);
                myDB.setUri(uri);
                myDB.getUri();

                myDB.setSoundName(displayName);
                myDB.getSoundName();


            }
        } finally {
            mCursor.close();
        }

    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release +")";
    }

    public double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //Log.e("LOCATION", "loc: " + location);
            if(alarmEnabled.isChecked()) {
                /*Log.e("RINGING", "100: " + String.valueOf(cb100hasAlreadyPlayed)+
                        ", 1000: " + String.valueOf(cb1000hasAlreadyPlayed)+
                        ", 3000: " + String.valueOf(cb3000hasAlreadyPlayed));

                Log.e("RINGING", "Song Uri: " + myDB.getUri());*/
                double dist = getDistanceFromLatLonInKm(myDB.getLongitude(), myDB.getLatitude(), longitude, latitude);
                if(!cb100hasAlreadyPlayed && dist < 0.1 && cb100.isChecked()) {
                    Log.e("CB100", "cb checked and dist: " + dist);
                    //ring and vibrate
                    cb100hasAlreadyPlayed = true;
                    cb1000hasAlreadyPlayed = true;
                    cb3000hasAlreadyPlayed = true;
                    myDB.setBoolean(DBHelper.COLUMN_DONE_100, true);
                    myDB.setBoolean(DBHelper.COLUMN_DONE_1000, true);
                    myDB.setBoolean(DBHelper.COLUMN_DONE_3000, true);
                    playSound(myDB.getUri());

                    if(cbVib.isChecked()) {
                        // The '0' here means to repeat indefinitely
                        // '0' is actually the index at which the pattern keeps repeating from (the start)
                        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
                        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= 26) {

                            v.vibrate(VibrationEffect.createWaveform(pattern, 1));
                        } else {
                            v.vibrate(pattern, 0);
                        }
                    }
                }
                if(!cb1000hasAlreadyPlayed && dist < 1 && cb1000.isChecked()) {
                    Log.e("CB1000", "cb checked and dist: " + dist);
                    //ring and vibrate
                    cb1000hasAlreadyPlayed = true;
                    cb3000hasAlreadyPlayed = true;
                    myDB.setBoolean(DBHelper.COLUMN_DONE_1000, true);
                    myDB.setBoolean(DBHelper.COLUMN_DONE_3000, true);
                    //mediaPlayer.start();
                    playSound(myDB.getUri());

                    if(cbVib.isChecked()) {
                        // The '0' here means to repeat indefinitely
                        // '0' is actually the index at which the pattern keeps repeating from (the start)
                        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
                        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= 26) {

                            v.vibrate(VibrationEffect.createWaveform(pattern, 1));
                        } else {
                            v.vibrate(pattern, 0);
                        }

                    }
                }
                if(!cb3000hasAlreadyPlayed && dist < 3 && cb3000.isChecked()) {
                    Log.e("CB3000", "cb checked and dist: " + dist);
                    //ring and vibrate

                    playSound(myDB.getUri());
                    cb3000hasAlreadyPlayed = true;
                    myDB.setBoolean(DBHelper.COLUMN_DONE_3000, true);

                    if(cbVib.isChecked()) {
                        // The '0' here means to repeat indefinitely
                        // '0' is actually the index at which the pattern keeps repeating from (the start)
                        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
                        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= 26) {

                            v.vibrate(VibrationEffect.createWaveform(pattern, 1));
                        } else {
                            v.vibrate(pattern, 0);
                        }

                    }
                }

                Toast.makeText(
                        getBaseContext(),
                        "Location changed: Lat: " + location.getLatitude() + " Lng: "
                                + location.getLongitude() + "\nTávolság a kiválasztott megállótól: " + dist,
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(
                        getBaseContext(),
                        "Location changed: Lat: " + location.getLatitude() + " Lng: "
                                + location.getLongitude(),
                        Toast.LENGTH_SHORT).show();
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("LOCATION", "StatusChanged");
        }

        public void onProviderEnabled(String provider) {
            Log.e("LOCATION", "ProviderEnabled");
        }

        public void onProviderDisabled(String provider) {
            Log.e("LOCATION", "ProviderDisabled");
        }

    };
}
