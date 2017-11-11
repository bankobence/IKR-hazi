package com.example.babence.bkvebreszto;

/**
 * Created by babence on 2017. 10. 23..
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static SQLiteDatabase database;
    private DBHelper dbHelper;


    public DatabaseManager(Context context){
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public static void addStop(Stops s) {

        if (!isIdThere(s.getId())) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_STOP_ID, s.getId());
            values.put(DBHelper.COLUMN_STOP_NAME, s.getStopName());
            values.put(DBHelper.COLUMN_STOP_LAT, s.getLatitude());
            values.put(DBHelper.COLUMN_STOP_LON, s.getLongitude());
            database.insert(DBHelper.TABLE_STOPS, null, values);
        }
    }

    //segitseg, hogy normalis ID-k vannak-e
    public void printAllID(){
        String[] column = {DBHelper.COLUMN_STOP_ID};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column, null , null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            Log.e("Adatbazis kezeles", "All ID: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_ID)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private static boolean isIdThere(String stopid){
        String[] column = {DBHelper.COLUMN_STOP_ID};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column ,DBHelper.COLUMN_STOP_ID + " LIKE '" + stopid + "'", null, null, null, null);
        Boolean rowExists = false;
        if (cursor.moveToFirst())
        {
            // DO SOMETHING WITH CURSOR
            rowExists = true;

        }
        cursor.close();
        return rowExists;
    }

    //ez majd jo lesz, felkesz
    public void getGPS(String stopid){
        String[] gpsLatLon;
        String[] column = {DBHelper.COLUMN_STOP_LAT, DBHelper.COLUMN_STOP_LON};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column ,DBHelper.COLUMN_STOP_ID+" LIKE '" + stopid + "'", null, null, null, null);
        int c = cursor.getColumnCount();
        Log.e("Adatbazis kezeles", "oszlopok szama: " + c);
        if (cursor.moveToFirst()) { // data?
            Log.e("Adatbazis kezeles", "lat: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_LAT)));
            Log.e("Adatbazis kezeles", "lon: " + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STOP_LON)));
        }
        cursor.close();
        //return gpsLatLon;
    }

}
