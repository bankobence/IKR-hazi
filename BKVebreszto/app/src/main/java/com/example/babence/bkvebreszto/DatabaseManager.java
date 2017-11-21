package com.example.babence.bkvebreszto;

/**
 * Created by babence on 2017. 10. 23..
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.babence.bkvebreszto.DBHelper.TABLE_STOPS;

public class DatabaseManager {

    private static SQLiteDatabase database;
    private DBHelper dbHelper;


    public DatabaseManager(Context context){
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void clearTable(){
        Log.e("Adatbazis kezeles", "Adatok torolve, torolt bejegyzesek szama: " + database.delete(TABLE_STOPS, "1",null));
    }

    public void close()
    {
        dbHelper.close();
    }

    public void addStop(Stops s, boolean alarmEnabled, boolean cb100, boolean cb1000,
                                    boolean cb3000, boolean cbVib, Uri selectedSoundUri, String displaySound,
                                    boolean done_100, boolean done_1000, boolean done_3000,double orig_dist) {

            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_STOP_ID, s.getId());
            values.put(DBHelper.COLUMN_STOP_NAME, s.getStopName());
            values.put(DBHelper.COLUMN_STOP_LAT, s.getLatitude());
            values.put(DBHelper.COLUMN_STOP_LON, s.getLongitude());
            values.put(DBHelper.COLUMN_SW_ENABLE, String.valueOf(alarmEnabled));
            values.put(DBHelper.COLUMN_CB_100, String.valueOf(cb100));
            values.put(DBHelper.COLUMN_CB_1000, String.valueOf(cb1000));
            values.put(DBHelper.COLUMN_CB_3000, String.valueOf(cb3000));
            values.put(DBHelper.COLUMN_CB_VIB, String.valueOf(cbVib));
            if(selectedSoundUri == null) {
                values.put(DBHelper.COLUMN_SOUND_URI, "");
            }else {
                values.put(DBHelper.COLUMN_SOUND_URI, selectedSoundUri.toString());
            }
            values.put(DBHelper.COLUMN_SOUND_NAME, displaySound);
            values.put(DBHelper.COLUMN_DONE_100, String.valueOf(done_100));
            values.put(DBHelper.COLUMN_DONE_1000, String.valueOf(done_1000));
            values.put(DBHelper.COLUMN_DONE_3000, String.valueOf(done_3000));
            values.put(DBHelper.COLUMN_ORIG_DIST, String.valueOf(orig_dist));


            database.insert(TABLE_STOPS, null, values);
    }

    public boolean getBoolean(String COLUMN){
        boolean state = false;
        String[] column = {COLUMN};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column, null, null, null, null, null);
        if(cursor.moveToNext()){
            state=onoffToBoolean(cursor.getString(cursor.getColumnIndex(COLUMN)));
            //Log.e("Adatbazis kezeles", "GET BOOLEAN: " + onoffToBoolean(cursor.getString(cursor.getColumnIndex(COLUMN))));
        }
        cursor.close();
        return state;
    }

    public void setBoolean(String COLUMN, boolean state){
        String id = getDbID();
        ContentValues values = new ContentValues();
        values.put(COLUMN, String.valueOf(state));
        database.update(DBHelper.TABLE_STOPS, values, "stop_id='"+id+"'" , null);
    }

    public void setUri(Uri selectedSoundUri){
        String id = getDbID();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_SOUND_URI, selectedSoundUri.toString());
        database.update(DBHelper.TABLE_STOPS, values, "stop_id='"+id+"'" , null);
    }

    public Uri getUri(){
        Uri storedUri = null;
        String[] column = {DBHelper.COLUMN_SOUND_URI};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column, null, null, null, null, null);

        if(cursor.moveToNext()){
            storedUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SOUND_URI)));
            Log.e("Adatbazis kezeles", "GET URI: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SOUND_URI)));
        }
        cursor.close();
        return storedUri;
    }

    public void setSoundName(String soundName){
        String id = getDbID();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_SOUND_NAME, soundName);
        database.update(DBHelper.TABLE_STOPS, values, "stop_id='"+id+"'" , null);
    }

    public String getSoundName(){
        String storedSound = null;
        String[] column = {DBHelper.COLUMN_SOUND_NAME};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column, null, null, null, null, null);

        if(cursor.moveToNext()){
            storedSound = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SOUND_NAME));
            Log.e("Adatbazis kezeles", "GET SoundName: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SOUND_NAME)));
        }
        cursor.close();
        return storedSound;
    }

    public String getStopName(){
        String stopName = "";
        String[] column = {DBHelper.COLUMN_STOP_NAME};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column, null, null, null, null, null);

        if(cursor.moveToNext()){
            stopName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_NAME));
            Log.e("Adatbazis kezeles", "GET STOPNAME: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_NAME)));
        }
        cursor.close();
        return stopName;
    }

    public double getLongitude(){
        String stopLon = "";
        String[] column = {DBHelper.COLUMN_STOP_LON};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column, null, null, null, null, null);

        if(cursor.moveToNext()){
            stopLon = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_LON));
            //Log.e("Adatbazis kezeles", "GET STOPNAME: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_LON)));
        }
        cursor.close();
        return  Double.parseDouble(stopLon);
    }
    public double getLatitude(){
        String stopLat = "";
        String[] column = {DBHelper.COLUMN_STOP_LAT};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column, null, null, null, null, null);

        if(cursor.moveToNext()){
            stopLat = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_LAT));
            //Log.e("Adatbazis kezeles", "GET STOPNAME: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_LAT)));
        }
        cursor.close();
        return  Double.parseDouble(stopLat);
    }

    public String getDbID(){
        String storedID ="";
        String[] column = {DBHelper.COLUMN_STOP_ID};
        Cursor cursor = database.query(TABLE_STOPS, column, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            storedID=cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_ID));
            cursor.moveToNext();
        }
        cursor.close();
        return storedID;
    }



    //segitseg, hogy normalis ID-k vannak-e
    public void printAllID(){
        String[] column = {DBHelper.COLUMN_STOP_ID};
        Cursor cursor = database.query(TABLE_STOPS, column, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            Log.e("Adatbazis kezeles", "All ID: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_ID)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private static boolean isIdThere(String stopid){
        String[] column = {DBHelper.COLUMN_STOP_ID};
        Cursor cursor = database.query(TABLE_STOPS, column ,DBHelper.COLUMN_STOP_ID + " LIKE '" + stopid + "'", null, null, null, null);
        Boolean rowExists = false;
        if (cursor.moveToFirst())
        {
            // DO SOMETHING WITH CURSOR
            rowExists = true;

        }
        cursor.close();
        return rowExists;
    }


    public void getStop(){
        String[] column = {DBHelper.COLUMN_STOP_ID, DBHelper.COLUMN_STOP_NAME,DBHelper.COLUMN_STOP_LAT, DBHelper.COLUMN_STOP_LON,
                DBHelper.COLUMN_SW_ENABLE, DBHelper.COLUMN_CB_100, DBHelper.COLUMN_CB_1000,
                DBHelper.COLUMN_CB_3000, DBHelper.COLUMN_CB_VIB, DBHelper.COLUMN_SOUND_URI, DBHelper.COLUMN_SOUND_NAME,
                DBHelper.COLUMN_DONE_100, DBHelper.COLUMN_DONE_1000, DBHelper.COLUMN_DONE_3000, DBHelper.COLUMN_ORIG_DIST};

        Cursor cursor = database.query(TABLE_STOPS, column, null, null, null, null, null);
        int c = cursor.getColumnCount();
        Log.e("Adatbazis kezeles", "oszlopok szama: " + c);
        if (cursor.moveToFirst()) { // data?
            Log.e("Adatbazis kezeles", "ID: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_ID)));
            Log.e("Adatbazis kezeles", "name: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_NAME)));
            Log.e("Adatbazis kezeles", "lat: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_LAT)));
            Log.e("Adatbazis kezeles", "lon: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_LON)));
            Log.e("Adatbazis kezeles", "SW: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SW_ENABLE)));
            Log.e("Adatbazis kezeles", "100: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CB_100)));
            Log.e("Adatbazis kezeles", "1000: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CB_1000)));
            Log.e("Adatbazis kezeles", "3000: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CB_3000)));
            Log.e("Adatbazis kezeles", "VIBR: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CB_VIB)));
            Log.e("Adatbazis kezeles", "URI: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SOUND_URI)));
            Log.e("Adatbazis kezeles", "SOUND: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SOUND_NAME)));
            Log.e("Adatbazis kezeles", "DONE_100: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DONE_100)));
            Log.e("Adatbazis kezeles", "DONE_1000: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DONE_1000)));
            Log.e("Adatbazis kezeles", "DONE_3000: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DONE_3000)));
            Log.e("Adatbazis kezeles", "ORIG_DIST: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ORIG_DIST)));
        }
        cursor.close();
    }

    public static boolean onoffToBoolean(String str){
        if(str.equals("true")) return true;
        else return false;
    }

}
