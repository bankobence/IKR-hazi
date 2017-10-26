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

    public CSVImport(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public List read(){
        List resultList = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            int line = 0;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                if(line != 0) {
                    resultList.add(row);
                    //az elso negy mindig benne van: id,nev,lat,lon
                    Log.e("MYAPP", row[0] + row[1] + row[2] + row[3]);
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
