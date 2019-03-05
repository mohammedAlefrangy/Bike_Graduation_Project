package com.example.hmod_.bike.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MapsActivity extends SupportMapFragment implements OnMapReadyCallback {


    private static final String ARG_SECTION_NUMBER = "section_number";

    private GoogleMap mMap;
    private HashMap<String, Marker> markers = new HashMap<>();

    public MapsActivity() {
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

        MainActivity.db.collection("bikes").whereEqualTo("available", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    GeoPoint geoPoint = dc.getDocument().getGeoPoint("location");
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Marker docmarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude()))
                        );
                        markers.put(dc.getDocument().getId(), docmarker);
                    } else if (dc.getType() == DocumentChange.Type.REMOVED) {
                        markers.get(dc.getDocument().getId()).remove();
                        markers.remove(dc.getDocument().getId());
                    } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
                        markers.get(dc.getDocument().getId()).setPosition(new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude()));
                    }
                }

            }
        });


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.5210764,34.44328), 15.0f));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        mMap.setMyLocationEnabled(true);
    }
}
