package com.example.babence.bkvebreszto;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import static com.example.babence.bkvebreszto.MyStopsRecyclerViewAdapter.*;

class FetchAllStopFromServer extends AsyncTask<Void, Void, List<Stops>> {


        private static final String TAG_ID = "stop_id";
        private static final String TAG_NAME = "stop_name";
        private static final String TAG_LAT = "stop_lat";
        private static final String TAG_LON = "stop_lon";
        //private static final String TAG_CODE = "stop_code";
        private static final String TAG_LOCTYPE =  "location_type";

        private ProgressDialog pDialog;
    private SearchActivity sActivity;
        protected List<Stops> stops = new ArrayList<>();

    FetchAllStopFromServer(SearchActivity activity) {
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
        protected List<Stops> doInBackground(Void... voids) {


            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.httpGet();
            //Log.e("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray stopsArray =  new JSONArray(jsonStr);
                    Log.e("FetchAllStopFromServer", "JSON Arraysize: " + String.valueOf(stopsArray.length()));

                    for (int i = 0; i < stopsArray.length(); i++) {
                        JSONObject stopObject = stopsArray.getJSONObject(i); //megvan a stop object
                        if(stopObject.getString(TAG_LOCTYPE).equals("") || stopObject.getString(TAG_LOCTYPE).equals("")){
                            String[] row = new String[6];
                            row[0]=stopObject.getString(TAG_ID);
                            row[1]=stopObject.getString(TAG_NAME);
                            row[2]=stopObject.getString(TAG_LAT);
                            row[3]=stopObject.getString(TAG_LON);
                            Stops stop = new Stops(row);
                            stops.add(stop);
                        }

                    }


                } catch (JSONException e) {
                    Log.e("FetchAllStopFromServer", e.getMessage());
                    e.printStackTrace();

                }

            } else {
                Log.e("FetchAllStopFromServer", "ServiceHandler couldn't get any data from the URL");
            }
            //Log.e("FetchAllStopFromServer", "Stops size: " + stops.size());
            //pDialog.dismiss();
            return stops;

        }
    @Override
    protected void onPostExecute(List<Stops> stops) {
        sActivity.dataReceived(stops);
        StopsearchFragment.getAdapter().notifyDataSetChanged();
        //MyStopsRecyclerViewAdapter.notifyDataSetChanged();
        //super.onPostExecute(stops);

        pDialog.dismiss();
    }
}




