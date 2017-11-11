package com.example.babence.bkvebreszto;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by babence on 2017. 10. 23..
 */

public class CSVImport {
    InputStream inputStream;
    public List resultList;
    public Stops stop;
    public CSVImport(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public List read(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            resultList = new ArrayList<Stops>();
            String csvLine;
            int line = 0;
            while ((csvLine = reader.readLine()) != null) {

                //specialis regularis kifejezes, mert az allamas nev is tartalmazhat vesszot...
                String[] row = csvLine.split(";(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if(line != 0) {
                    //amig uj sor van, a stops konstruktoraval Stops objektumokat csinalunk
                    stop = new Stops(row);
                    //es hozzaadjuk egy listahoz, ami Stopsokat tartalmaz
                    resultList.add(stop);

                    //az elso negy mindig benne van: id,nev,lat,lon
                    //Log.e("MYAPP", "elso: " + row[0] + " ,masodik: " + row[1] + " ,harmadik: " + row[2] + ", negyedik: " + row[3]+";");
                    line++;
                }else line++;
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return resultList;
    }

}
