package com.example.hmod_.bike;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.hmod_.bike.Activity.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;

public class SplachActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int RC_SIGN_IN = 123;
    private static FirebaseAuth mAuth;
    private static SplachActivity splachActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splachActivity = this;
        MainActivity.prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_splach);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {

            loginUser ();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loginUser ();
            } else {
                // TODO: Show something that we can't run this application without the fucking location
            }
        }
    }

    private void loginUser () {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            createSignInIntent();
        } else {
            updateUser(true);
        }
    }

    private void hideSplach (){
        //int SPLASH_TIME = 1000;
        //new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
        Intent i = new Intent(SplachActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        splachActivity = null;
        //}, SPLASH_TIME);
    }

    private void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.ic_bike_parking)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void updateUser () {
        updateUser(false);
    }
    public static void updateUser(boolean isInit) {
        MainActivity.currentAuthUser = mAuth.getCurrentUser();
        MainActivity.db.collection("users").document(MainActivity.currentAuthUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String lastRentId = MainActivity.prefs.getString ("lastRentId", null);
                    MainActivity.currentBikeKey = MainActivity.prefs.getString ("lastBikeKey", null);
                    Rent.updateCurrentRent (lastRentId, isInit);
                    MainActivity.currentUser = document.toObject(User.class);
                } else {
                    MainActivity.currentUser = new User(MainActivity.currentAuthUser);
                    MainActivity.db.collection("users").document(MainActivity.currentAuthUser.getUid()).set(MainActivity.currentUser);
                }
                if (MainActivity.mainActivity != null)
                    MainActivity.mainActivity.updateHeaderUI ();
                if (SplachActivity.splachActivity != null)
                    SplachActivity.splachActivity.hideSplach();
            }
        });
    }
}