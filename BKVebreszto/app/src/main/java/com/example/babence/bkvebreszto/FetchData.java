package com.example.babence.bkvebreszto;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by babence on 2017. 11. 07..
 */

public class FetchData extends AsyncTask<Void, Void, List<Stops>> {

    private SearchActivity sActivity;
    private ProgressDialog pDialog;
    //ProgressBar progressBar = new ProgressBar(sActivity, null, android.R.attr.progressBarStyleSmall);
    private static DatabaseManager myDB;
    public List<Stops> stops = new ArrayList<Stops>();

    public FetchData(SearchActivity activity) {
        sActivity = activity;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        //progressBar.setIndeterminate(true);
        pDialog = new ProgressDialog(sActivity);
        pDialog.setMessage("Megállók betöltése...");
        pDialog.show();
    }

    @Override
    protected List<Stops> doInBackground(Void... arg0) {

        //txt fajl meghatarozasa
        InputStream inputStream = sActivity.getResources().openRawResource(R.raw.stops);
        if(inputStream != null) {
            CSVImport csvFile = new CSVImport(inputStream);
            if(csvFile != null) {

                //beolvasas, egy Stops objektumlistat kapunk vissza
                stops = csvFile.read();
                //myDB = new DatabaseManager(getBaseContext());
                //myDB.printAllID(); //ez csak ugy ellenorzesnek, hogy mi van elotte az adatbazisban
                /*
                //vegigiteralva hozzaadjuk a lista tagjait a Stops tablahoz
                for (Stops s : stops) {
                    //Log.e("add Stop", "ID: " + s.id + " ,name: " + s.name + " ,lat: " + s.lat + " ,lon:" + s.lon);
                    myDB.addStop(s);
                }*/

                //myDB.close();
            }else{
                Log.e("FetchData", "Rossz csv fájl");
            }

        }else{
            Log.e("FetchData", "Nem jó az inputStream");
        }
        return stops;
    }


    @Override
    protected void onPostExecute(List<Stops> stops) {
        sActivity.dataReceived(stops);
        //MapFragment.getAdapter().notifyDataSetChanged();
        //super.onPostExecute(stops);

        pDialog.dismiss();
    }
}
