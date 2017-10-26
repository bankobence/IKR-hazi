package com.example.babence.bkvebreszto;

/**
 * Created by babence on 2017. 10. 23..
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    /*
    public static final String META_KEY_FAVOURITE = "favourite";
    public static final String META_KEY_HIDDEN = "hidden";
    public static final String META_KEY_LASTVALUE = "lastvalue";
    */

    public DatabaseManager(Context context){
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    private void addStop(int stopid, String stopname, String stoplat, String stoplon){

        ContentValues values = new ContentValues();

        //invalid inputok kezelese itt vagy a csv-nel
        values.put(DBHelper.COLUMN_STOP_ID, stopid);
        values.put(DBHelper.COLUMN_STOP_NAME, stopname);
        values.put(DBHelper.COLUMN_STOP_LAT, stoplat);
        values.put(DBHelper.COLUMN_STOP_LON, stoplon);

        database.insert(DBHelper.TABLE_STOPS, null, values);
    }
/*
    private String[] getGPS(int stopid){
        String[] gpsLatLon;
        String[] column = {DBHelper.COLUMN_STOP_LAT, DBHelper.COLUMN_STOP_LON};
        Cursor cursor = database.query(DBHelper.TABLE_STOPS, column ,DBHelper.COLUMN_STOP_ID+" = " + stopid, null, null, null, null);

        return gpsLatLon;
    }*/
/*
    private String getMeta(int nodeid, String metakey){
        String meta = null;
        String[] column = {DBHelper.COLUMN_META_VALUE};

        Cursor cursor = database.query(DBHelper.TABLE_NODE_META, column ,DBHelper.COLUMN_NODE_ID+" = " + nodeid +
                " AND " + DBHelper.COLUMN_META_KEY +" = '" + metakey + "'", null, null, null, null);

        if(cursor.moveToNext())
        {meta=cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_META_VALUE));} //itt out of indexes exception
        cursor.close();
        return meta;
    }

    private void setMeta(int nodeid, String metakey, String value){
        //mivel a delMeta fĂĽggvĂ©ny mindig kitĂ¶rli az adott sort
        //ezĂ©rt felesleges a setMetanak is megkapnia, hogy mire ĂˇllĂ­tottuk a metavalue-t, Ăşgyhogy ezt a paramĂ©tert kitĂ¶rĂ¶ltem

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NODE_ID, nodeid);
        values.put(DBHelper.COLUMN_META_KEY, metakey);

        if(metakey.equals(META_KEY_LASTVALUE)){
            values.put(DBHelper.COLUMN_META_VALUE, value);
        }
        else{
            values.put(DBHelper.COLUMN_META_VALUE, "true");
        }
        database.insert(DBHelper.TABLE_NODE_META, null, values);

    }

    private void delMeta(int nodeid, String metakey){
        database.delete(DBHelper.TABLE_NODE_META, DBHelper.COLUMN_NODE_ID
                + " = " + nodeid + " AND " + DBHelper.COLUMN_META_KEY + " = '" + metakey + "'", null);

    }

    public List<String> getAllFavourites(){
        List<String> ret = new ArrayList<String>();

        String[] column = {DBHelper.COLUMN_NODE_ID};
        Cursor cursor = database.query(DBHelper.TABLE_NODE_META, column ,DBHelper.COLUMN_META_KEY + " = '" + META_KEY_FAVOURITE
                        + "' AND " + DBHelper.COLUMN_META_VALUE +" = 'true'",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String str = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NODE_ID));
            ret.add(str);
            cursor.moveToNext();
        }

        cursor.close();

        return ret;
    }

    public String getLastValue(int nodeid){
        return getMeta(nodeid, META_KEY_LASTVALUE);
    }

    public void setLastValue(int nodeid, String percent){
        setMeta(nodeid, META_KEY_LASTVALUE, percent);
    }

    public boolean isFavourite(int nodeid){
        String record = getMeta(nodeid, META_KEY_FAVOURITE);
        return Boolean.valueOf(record);
    }

    public void addFavourite(int nodeid){
        setMeta(nodeid, META_KEY_FAVOURITE, null);
    }

    public void deleteFavourite(int nodeid){
        delMeta(nodeid, META_KEY_FAVOURITE);
    }

    public boolean isHidden(int nodeid){
        String record = getMeta(nodeid, META_KEY_HIDDEN);

        return Boolean.valueOf(record);
    }

    public void Hide(int nodeid){
        setMeta(nodeid, META_KEY_HIDDEN, null);
    }

    public void unHide(int nodeid){
        delMeta(nodeid, META_KEY_HIDDEN);
    }
*/
}
