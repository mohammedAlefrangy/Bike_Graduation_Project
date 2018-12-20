package com.example.hmod_.bike.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
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

public class MapsActivity extends SupportMapFragment implements OnMapReadyCallback {

    private final LatLng KIEL = new LatLng(31.5078947, 34.4561705);
    private final LatLng HAMBURG = new LatLng(31.508315, 34.4535093);


    private static final String ARG_SECTION_NUMBER = "section_number";

    private GoogleMap mMap;
    private Marker marker;

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

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setMapToolbarEnabled(false);


            mMap.addMarker(new MarkerOptions().position(HAMBURG)
                    .title("Hamburg"));
            mMap.addMarker(new MarkerOptions()
                    .position(KIEL)
                    .title("Kiel")
                    .snippet("Kiel is cool")
                   );


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15.0f));

            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


            return ;
        }
//        mMap.setMyLocationEnabled(true);
    }
}
