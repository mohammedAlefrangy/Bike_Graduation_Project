package com.example.hmod_.bike;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.hmod_.bike.Activity.LoginActivity;
import com.example.hmod_.bike.Activity.MainActivity;

public class SplachActivity extends AppCompatActivity {
    private int SPLASH_TIME = 1500;
    private static int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            hideSplach ();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hideSplach ();
            } else {
                // TODO: Show something that we can't run this application without the fucking location
            }
        }
    }

    private void hideSplach (){
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplachActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME);
    }
}