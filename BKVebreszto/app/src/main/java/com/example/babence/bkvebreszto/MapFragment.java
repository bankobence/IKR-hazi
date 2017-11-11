package com.example.babence.bkvebreszto;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends android.support.v4.app.Fragment
        implements OnMapReadyCallback {

    MapView mapView;
    GoogleMap mMap;
    private OnFragmentInteractionListener mListener;

    protected HashMap<String, Marker> courseMarkers = new HashMap<String, Marker>();

    public List<Stops> mStops = new ArrayList<Stops>();

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnCameraChangeListener(getCameraChangeListener());
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        LatLng myPosition = new LatLng(MainActivity.latitude, MainActivity.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,16));
        mStops = getArguments().getParcelableArrayList("megallok");
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
               //Toast.makeText(getContext(),"KATT: "+marker.getTitle(),Toast.LENGTH_SHORT).show();
                if (mListener != null) {
                    outerLoop:
                    for(String key : courseMarkers.keySet()){
                        if(courseMarkers.get(key).equals(marker)) {
                            for(Stops actualStop : mStops) {
                                if (key.equals(actualStop.getId())) {
                                    //Log.e("MarkerSearch", "ActualStop: " + key + " ID: " + actualStop.getId());
                                    mListener.onFragmentInteraction(actualStop);
                                    break outerLoop;

                                }
                            }
                        }
                    }

                }
            }
        });

    }

    protected Marker createMarker(double latitude, double longitude, String title) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_clipart)));
                //.snippet(snippet));

    }

    public GoogleMap.OnCameraChangeListener getCameraChangeListener()
    {
        return new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition position)
            {
                addItemsToMap(mStops);
            }
        };
    }

    private void addItemsToMap(List<Stops> items)
    {
        if(this.mMap != null)
        {
            //This is the current user-viewable region of the map
            LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;

            //Loop through all the items that are available to be placed on the map
            for(Stops item : items)
            {

                //If the item is within the the bounds of the screen
                if(bounds.contains(new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude()))))
                {

                    //If the item isn't already being displayed
                    if(!courseMarkers.containsKey(item.getId()))
                    {
                        //Log.e("Map markers", "Aktualis zoom: " + mMap.getCameraPosition().zoom );
                        if(mMap.getCameraPosition().zoom >= 16) {
                            //Add the Marker to the Map and keep track of it with the HashMap
                            //getMarkerForItem just returns a MarkerOptions object
                            this.courseMarkers.put(item.getId(), createMarker(Double.parseDouble(item.getLatitude()),
                                    Double.parseDouble(item.getLongitude()),
                                    item.getStopName()));
                        }
                    }else{
                        //Ha nagyon kizoomolunk de fent van a térképen, akkor töröljük
                        if(courseMarkers.containsKey(item.getId()) && mMap.getCameraPosition().zoom < 16)
                        {
                            //1. Remove the Marker from the GoogleMap
                            courseMarkers.get(item.getId()).remove();

                            //2. Remove the reference to the Marker from the HashMap
                            courseMarkers.remove(item.getId());
                        }
                    }
                }else //If the marker is off screen
                    {
                        //If the course was previously on screen
                        if(courseMarkers.containsKey(item.getId()))
                        {
                            //1. Remove the Marker from the GoogleMap
                            courseMarkers.get(item.getId()).remove();

                            //2. Remove the reference to the Marker from the HashMap
                            courseMarkers.remove(item.getId());
                        }
                    }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return v;

    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public static MapFragment newInstance(ArrayList stops) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("megallok", stops);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
    *Ezt azért tettem be, hogy eltűnjön a billentyűzet mikor a listáról a térképre.
    *Így viszont eltűnik az actionbar, amit vissza lehet hozni, ha lehúzod a képernyőt.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                mImm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                Log.e(TAG, "setUserVisibleHint: ", e);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Stops stops);
    }
}
