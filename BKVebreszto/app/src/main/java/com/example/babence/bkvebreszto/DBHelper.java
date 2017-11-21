package com.example.babence.bkvebreszto;

/**
 * Created by babence on 2017. 10. 23..
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_STOPS = "stops";
    public static final String COLUMN_STOP_ID = "stop_id";
    public static final String COLUMN_STOP_NAME = "stop_name";
    public static final String COLUMN_STOP_LAT= "stop_lat";
    public static final String COLUMN_STOP_LON = "stop_lon";
    public static final String COLUMN_SW_ENABLE = "sw_enable";
    public static final String COLUMN_CB_100 = "CB_100";
    public static final String COLUMN_CB_1000 = "CB_1000";
    public static final String COLUMN_CB_3000 = "CB_3000";
    public static final String COLUMN_CB_VIB = "CB_VIB";
    public static final String COLUMN_SOUND_URI = "SOUND_URI";
    public static final String COLUMN_SOUND_NAME = "SOUND_NAME";
    public static final String COLUMN_DONE_100 = "DONE_100";
    public static final String COLUMN_DONE_1000 = "DONE_1000";
    public static final String COLUMN_DONE_3000 = "DONE_3000";
    public static final String COLUMN_ORIG_DIST = "ORIG_DIST";

    private static final String DATABASE_NAME = "iks.db";
    private static final int DATABASE_VERSION = 7;

    private static final String DATABASE_CREATE = " CREATE TABLE IF NOT EXISTS " + TABLE_STOPS +
            "(" + COLUMN_STOP_ID + " VARCHAR(50) NOT NULL PRIMARY KEY," +
            COLUMN_STOP_NAME + " VARCHAR(200) NOT NULL," +
            COLUMN_STOP_LAT + " VARCHAR(50) NOT NULL," +
            COLUMN_STOP_LON + " VARCHAR(50) NOT NULL," +
            COLUMN_SW_ENABLE + " VARCHAR(50) NOT NULL," +
            COLUMN_CB_100 + " VARCHAR(50) NOT NULL," +
            COLUMN_CB_1000 + " VARCHAR(50) NOT NULL," +
            COLUMN_CB_3000 + " VARCHAR(50) NOT NULL," +
            COLUMN_CB_VIB + " VARCHAR(50) NOT NULL," +
            COLUMN_SOUND_URI + " VARCHAR(200) NOT NULL," +
            COLUMN_SOUND_NAME + " VARCHAR(200) NOT NULL," +
            COLUMN_DONE_100 + " VARCHAR(50) NOT NULL," +
            COLUMN_DONE_1000 + " VARCHAR(50) NOT NULL," +
            COLUMN_DONE_3000 + " VARCHAR(50) NOT NULL," +
            COLUMN_ORIG_DIST + " VARCHAR(50) NOT NULL" +
            ")";
/*
    CREATE TABLE IF NOT EXISTS `mydb`.`stops` (
  `stop_id` INT NOT NULL AUTO_INCREMENT,
  `stop_name` VARCHAR(200) NOT NULL,
  `stop_lat` DOUBLE NOT NULL,
  `stop_lon` DOUBLE NOT NULL,
  PRIMARY KEY (`stop_id`))
*/
    private static final String DATABASE_DELETE = "DROP TABLE IF EXISTS " + TABLE_STOPS;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL(DATABASE_DELETE);
        onCreate(db);
    }


}
