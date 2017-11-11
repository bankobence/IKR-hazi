package com.example.babence.bkvebreszto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;

/**
 * Created by babence on 2017. 10. 28..
 */



public class Stops implements Parcelable {
    private String id;
    //int id;
    private String name;
    private String lat;
    private String lon;

    public Stops(String[] row){

        id = row[0];
        //id = valueOf(row[0]);
        name = row[1];
        lat = row[2];
        lon = row[3];

    }

    protected Stops(Parcel in) {
        id = in.readString();
        //id = in.readInt();
        name = in.readString();
        lat = in.readString();
        lon = in.readString();
    }

    public static final Creator<Stops> CREATOR = new Creator<Stops>() {
        @Override
        public Stops createFromParcel(Parcel in) {
            return new Stops(in);
        }

        @Override
        public Stops[] newArray(int size) {
            return new Stops[size];
        }
    };

    public String getStopName(){
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        //parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(lat);
        parcel.writeString(lon);
    }


    public String getLatitude() {
        return lat;
    }

    public String getLongitude() {
        return lon;
    }
    public String getId() {return id;}
}
