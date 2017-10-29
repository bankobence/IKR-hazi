package com.example.babence.bkvebreszto;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    //sajat valtozoim
    public String lon = "longitude", lat = "latitude";
    public double longitude, latitude;
    public double lonBKS = 47.600125, latBKS = 19.046357;
    private static DatabaseManager myDB;
    public List<Stops> stops = new ArrayList<Stops>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        /*************************************************************************************
         * INNEN KEZDŐDIK A SAJÁT KÓDOM
         */

        //txt fajl meghatarozasa
        InputStream inputStream = getResources().openRawResource(R.raw.stops_mock);
        CSVImport csvFile = new CSVImport(inputStream);

        //beolvasas, egy Stops objektumlistat kapunk vissza
        stops = csvFile.read();
        myDB = new DatabaseManager(getBaseContext());
        myDB.printAllID(); //ez csak ugy ellenorzesnek, hogy mi van elotte az adatbazisban

        //vegigiteralva hozzaadjuk a lista tagjait a Stops tablahoz
        for (Stops s : stops) {
            //Log.e("add Stop", "ID: " + s.id + " ,name: " + s.name + " ,lat: " + s.lat + " ,lon:" + s.lon);
            myDB.addStop(s);
        }
        myDB.getGPS(2133); //teszt hogy jo-e a koordinata


         myDB.close();

        final LocationManager locationManagerNet = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

           /*
            * HA Android 6.0 feletti, akkor pont fordítva legyenek az ágak, tehát ami itt az ifben, ott az elseben
            * ezt majd szepre le lehetne kezelni verziofuggore
            */
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){
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
            }

        } else {
            if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){
                locationManagerNet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

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


        //time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        //Toast.makeText(MainActivity.this, "Legutobbi koordinatak: "+lon+lat+time , Toast.LENGTH_SHORT).show();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Legelso koordinatak: lon:" + longitude + " lat:" + latitude, Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, getAndroidVersion(), Toast.LENGTH_SHORT).show();

            }
        });


        // Remove the listener you previously added
        //locationManager.removeUpdates(locationListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
    SAJAT FUGGVENYEIM/OSZTALYAIM
     */

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
            double dist = getDistanceFromLatLonInKm(latBKS, lonBKS, longitude, latitude);
            // Called when a new location is found by the network location provider.
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + location.getLatitude() + " Lng: "
                            + location.getLongitude() + "\nTávolság a buszmegállótól: " + dist,
                    Toast.LENGTH_SHORT).show();

        }


        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }


    };
}
