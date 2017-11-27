package com.example.babence.bkvebreszto;

/**
 * Created by babence on 2017. 11. 27..
 */

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ServiceHandler {

    private static final String URL = "http://bwcyzt.azurewebsites.net/api/stops";

    public String httpGet() {
        URL url = null;
        try {
            url = new URL(URL);
        } catch (MalformedURLException e) {
            Log.e("ServiceHandler", e.getMessage());
            e.printStackTrace();

        }
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            //Log.e("ServiceHandler", "urlConn: " + urlConnection.getResponseCode());
            urlConnection.setRequestMethod("GET");

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                //Log.e("ServiceHandler", "inputstream: " + in.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                //Log.e("ServiceHandler", "buffered reader: " + reader);

                String line;
                while ((line = reader.readLine()) != null) {
                    //Log.e("ServiceHandler", "Beolvasott sor: " + line);
                    result.append(line);
                }
            }

        }catch( IOException e) {
            Log.e("ServiceHandler", e.getMessage());
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return result.toString();
    }


}
