package com.example.babence.bkvebreszto;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    //sajat valtozoim
    public static double longitude, latitude;
    private DatabaseManager myDB;
    protected List<Stops> stops = new ArrayList<>();
    public Stops activeStop;
    Location currentBestLocation = null;
    protected AlertDialog alert = null;
    protected Dialog dialog = null;
    private static final int SONG_REQUEST_CODE = 42;
    private static final int SEARCH_REQUEST_CODE = 1;
    //protected double initialDistance;

    LocationManager locationManagerNet=null;
    LocationManager locationManagerGPS=null;

    private TextView songName;
    private TextView stopName;
    protected CheckBox cb100;
    protected CheckBox cb1000;
    protected CheckBox cb3000;
    protected CheckBox cbVib;
    protected Switch alarmEnabled;
    protected String displayName = "Csengőhang neve";

    protected boolean cb100hasAlreadyPlayed;
    protected boolean cb1000hasAlreadyPlayed;
    protected boolean cb3000hasAlreadyPlayed;


    protected Uri selectedSoundUri = null;
    protected static MediaPlayer mediaPlayer;
    protected Vibrator vibr;

    // Start without a delay
    // Vibrate for 1000 milliseconds
    // Sleep for 1000 milliseconds
    private long[] pattern = {0, 1000, 1000};

    @Override
    protected void onPause(){
        super.onPause();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();

        }
        if(vibr != null){
            vibr.cancel();
        }
        if(alert != null) { alert.dismiss(); }
        if(dialog != null) { dialog.dismiss(); }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();

        }
        if(vibr != null){
            vibr.cancel();
        }
        if(alert != null) { alert.dismiss(); }
        if(dialog != null) { dialog.dismiss(); }
        // Remove the listener you previously added
        //locationManager.removeUpdates(locationListener);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();

        }
        if(vibr != null){
            vibr.cancel();
        }
        if(alert != null) { alert.dismiss(); }
        if(dialog != null) { dialog.dismiss(); }

        // Remove the listener you previously added
        if(locationManagerNet != null) {locationManagerNet.removeUpdates(locationListener);}
        if(locationManagerGPS != null) {locationManagerGPS.removeUpdates(locationListener);}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        myDB = new DatabaseManager(getBaseContext());

        locationManagerNet = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerGPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
                locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Log.e("LOC1", "Location elkerve:");
            }

        } else {
            if(Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M){
                locationManagerNet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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

        if (locationManagerNet.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Toast.makeText(this, "Helymeghatározás engedélyezve", Toast.LENGTH_SHORT).show();
        }else{
            buildAlertMessageNoGps();
        }

        Button button = (Button) findViewById(R.id.getGPS);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Legutóbbi koordinatak: lon:"
                        + longitude + " lat:" + latitude
                        + "\nSzolgáltató: " + currentBestLocation.getProvider(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, getAndroidVersion(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        //megallo valasztas masik activityben
        if (requestCode == SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            activeStop = resultData.getParcelableExtra("Search_data");
            stopName.setText(activeStop.getStopName());

            myDB.printAllID(); //ez csak ugy ellenorzesnek, hogy mi van elotte az adatbazisban
            myDB.clearTable();
            //myDB.printAllID();
            cb100hasAlreadyPlayed = false;
            cb1000hasAlreadyPlayed = false;
            cb3000hasAlreadyPlayed = false;

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

        //zenevalasztas
        if (requestCode == SONG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                Log.e("File valasztas", "Uri: " + uri.toString());
                dumpSoundMetaData(uri);
                selectedSoundUri = uri;
            }
        }
    }

    //ring and vibrate
    private void startAlarm(int alertDistance){

        Uri uri = myDB.getUri();
        Log.e("MainActivity", "startAlarm URI: " + uri);

        if(cbVib.isChecked()) {

            vibr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= 26) {

                vibr.vibrate(VibrationEffect.createWaveform(pattern, 1));
            } else {
                vibr.vibrate(pattern, 0);
            }

        }
        mediaPlayer = new MediaPlayer();

            try {

                /*File filePath = new File(uri.getPath());
                FileInputStream is = new FileInputStream(filePath);
                mediaPlayer.setDataSource(is.getFD());*/
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                //is.close();


            }catch(IOException e){
                try {
                    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    if (alert == null) {
                        // alert is null, using backup
                        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        // I can't see this ever being null (as always have a default notification)
                        // but just incase
                        if (alert == null) {
                            // alert backup is null, using 2nd backup
                            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        }
                    }
                    mediaPlayer.setDataSource(getApplicationContext(), alert);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } else {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.alarm_dialog);
        dialog.setCanceledOnTouchOutside(false);
        String dialogText = String.valueOf(alertDistance) + " méter közel vagy!";

        TextView text = (TextView) dialog.findViewById(R.id.alarmText);
        text.setText(dialogText);

        Button button = (Button) dialog.findViewById(R.id.stopAlarmButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                if(vibr != null){
                    vibr.cancel();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
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

    public static double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
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

    public static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if(isBetterLocation(location, currentBestLocation)) {
                currentBestLocation = location;
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            //Log.e("LOCATION", "loc: " + location);
            if(alarmEnabled.isChecked()) {
                double dist = getDistanceFromLatLonInKm(myDB.getLongitude(), myDB.getLatitude(), longitude, latitude);
                Toast.makeText(
                        getBaseContext(),
                        "Helyzet megváltozott: Lat: " + location.getLatitude() + " Lng: "
                                + location.getLongitude() + "\nTávolság a kiválasztott megállótól: " + dist
                                + "\nSzolgáltató: " + currentBestLocation.getProvider(),
                        Toast.LENGTH_SHORT).show();

                /*Log.e("RINGING", "100: " + String.valueOf(cb100hasAlreadyPlayed)+
                        ", 1000: " + String.valueOf(cb1000hasAlreadyPlayed)+
                        ", 3000: " + String.valueOf(cb3000hasAlreadyPlayed));

                Log.e("RINGING", "Song Uri: " + myDB.getUri());*/

                if(!cb100hasAlreadyPlayed && dist < 0.1 && cb100.isChecked()) {
                    Log.e("CB100", "cb checked and dist: " + dist);

                    startAlarm(100);

                    cb100hasAlreadyPlayed = true;
                    cb1000hasAlreadyPlayed = true;
                    cb3000hasAlreadyPlayed = true;
                    myDB.setBoolean(DBHelper.COLUMN_DONE_100, true);
                    myDB.setBoolean(DBHelper.COLUMN_DONE_1000, true);
                    myDB.setBoolean(DBHelper.COLUMN_DONE_3000, true);

                }
                if(!cb1000hasAlreadyPlayed && dist < 1 && cb1000.isChecked()) {
                    Log.e("CB1000", "cb checked and dist: " + dist);

                    startAlarm(1000);

                    cb1000hasAlreadyPlayed = true;
                    cb3000hasAlreadyPlayed = true;
                    myDB.setBoolean(DBHelper.COLUMN_DONE_1000, true);
                    myDB.setBoolean(DBHelper.COLUMN_DONE_3000, true);

                }
                if(!cb3000hasAlreadyPlayed && dist < 3 && cb3000.isChecked()) {
                    Log.e("CB3000", "cb checked and dist: " + dist);

                    startAlarm(3000);

                    cb3000hasAlreadyPlayed = true;
                    myDB.setBoolean(DBHelper.COLUMN_DONE_3000, true);

                }

            }
            else {
                Toast.makeText(
                        getBaseContext(),
                        "Helyzet megváltozott: Lat: " + location.getLatitude() + " Lng: "
                                + location.getLongitude() + "\nSzolgáltató: " + currentBestLocation.getProvider(),
                        Toast.LENGTH_SHORT).show();
            }
        }




        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("LOCATION", "StatusChanged");
        }

        public void onProviderEnabled(String provider) {
            Log.e("LOCATION", "ProviderEnabled: " + provider);
        }

        public void onProviderDisabled(String provider) {
            Log.e("LOCATION", "ProviderDisabled: " + provider);
        }

    };

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Helymeghatározás kikapcsolva, bekapcsolja?")
                .setCancelable(false)
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
}
