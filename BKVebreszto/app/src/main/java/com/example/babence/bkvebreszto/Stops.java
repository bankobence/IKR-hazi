package com.example.babence.bkvebreszto;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;

/**
 * Created by babence on 2017. 10. 28..
 */

public class Stops {
    int id;
    String name;
    String lat;
    String lon;

    public Stops(String[] row){
        id = valueOf(row[0]);
        name = row[1];
        lat = row[2];
        lon = row[3];
    }

    public String getStopName(){
        return name;
    }
}
