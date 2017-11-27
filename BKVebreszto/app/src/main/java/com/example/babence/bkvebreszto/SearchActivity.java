package com.example.babence.bkvebreszto;


import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements MapFragment.OnFragmentInteractionListener, StopsearchFragment.OnListFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private List<Stops> stops = new ArrayList<Stops>();
    private DatabaseManager myDB;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onResume() {
        super.onResume();
        new FetchAllStopFromServer(this).execute();
    }


    public void dataReceived(List<Stops> stoplist) {
        stops.clear();
        stops.addAll(stoplist);
        Log.e("SearchActivity", "Stops size: " + stops.size());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //txt fajl meghatarozasa
        /*InputStream inputStream = getResources().openRawResource(R.raw.stops);
        if(inputStream != null) {
            CSVImport csvFile = new CSVImport(inputStream);
            if(csvFile != null) {

                //beolvasas, egy Stops objektumlistat kapunk vissza
                stops = csvFile.read();

            }else{
                Log.e("FetchCSV", "Rossz csv fájl");
            }

        }else{
            Log.e("FetchCSV", "Nem jó az inputStream");
        }*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //vissza gomb
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public void onFragmentInteraction(Stops item) {
        Log.e("MapItemSelected", "A kattintott megálló: " + item.getId()+ ", " + item.getStopName());
        Intent intent = new Intent();
        intent.putExtra("Search_data", item);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onListFragmentInteraction(Stops item) {
        Log.e("ListItemSelected", "A kattintott megálló: " + item.getId()+ ", " + item.getStopName());
        Intent intent = new Intent();
        intent.putExtra("Search_data", item);
        setResult(RESULT_OK, intent);
        finish();
    }




    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0){
                return StopsearchFragment.newInstance((ArrayList) stops);
            }else{
                return MapFragment.newInstance((ArrayList) stops);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MEGÁLLÓK";
                case 1:
                    return "TÉRKÉP";
            }
            return null;
        }
    }
}
