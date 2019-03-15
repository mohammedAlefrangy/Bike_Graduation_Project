package com.example.hmod_.bike.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.hmod_.bike.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class MapsActivity extends SupportMapFragment implements OnMapReadyCallback {


    private static final String ARG_SECTION_NUMBER = "section_number";

    private GoogleMap mMap;
    private final HashMap<String, Marker> markers = new HashMap<>();

    public MapsActivity() {
        MainActivity.mainActivity.getSupportActionBar().setTitle("Stations Map");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("MyMap", "onResume");
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {

            Log.d("MyMap", "setUpMapIfNeeded");

            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MyMap", "onMapReady");
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        MainActivity.db.collection("stations").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                return;
            }

            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                GeoPoint geoPoint = dc.getDocument().getGeoPoint("location");
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    int icon = R.drawable.empty_station;
                    if (dc.getDocument().getLong("bikes") != 0)
                        icon = R.drawable.notempty_station;
                    Marker docmarker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(icon))
                            .position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
                    );
                    markers.put(dc.getDocument().getId(), docmarker);
                } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                    markers.get(dc.getDocument().getId()).remove();
                    markers.remove(dc.getDocument().getId());
                } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
                    int icon = R.drawable.empty_station;
                    if (dc.getDocument().getLong("bikes") != 0)
                        icon = R.drawable.notempty_station;
                    markers.get(dc.getDocument().getId()).setPosition(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                    markers.get(dc.getDocument().getId()).setIcon(BitmapDescriptorFactory.fromResource(icon));
                }
            }

        });


        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager)
                    getActivity().getSystemService(getContext().LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            if (location != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.5210764, 34.44328), 10.0f));
            }
            mMap.setMyLocationEnabled(true);
        }
    }
}
