package com.example.hmod_.bike.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hmod_.bike.Fragment.MapsActivity;
import com.example.hmod_.bike.Fragment.RentBike;
import com.example.hmod_.bike.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class Main2Activity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ShowsFragmentPagerAdapter showsFragmentPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static final String TAG = "Main2Activity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_SIGN_IN = 1;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //to access point for firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();


        Toolbar mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        showsFragmentPagerAdapter = new ShowsFragmentPagerAdapter(getSupportFragmentManager(), this);


        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter = new ShowsFragmentPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(pagerAdapter);


        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        authStateListener();

    }

    private void authStateListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user signed in
                    onSignedInInitialize(user.getDisplayName());

                } else {
                    // user signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignedInInitialize(String displayName) {
//        mUsername = displayName;
//        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
//        mUsername = ANONYMOUS;
//        mMessageAdapter.clear();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;

        }
    }

//    private void attachDatabaseReadListener() {
//        if (mChildEventListener == null) {
//            mChildEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
////                    mMessageAdapter.add(friendlyMessage);
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            };
//            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
//        mMessageAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class ShowsFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private Context context;

        public ShowsFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MapsActivity();
                case 1:
                    return new MapsActivity();
                case 2:
                    return new RentBike();
                default:
                    return new MapsActivity();
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.map);
                case 1:
                    return getString(R.string.logo);
                case 2:
                    return getString(R.string.rent_bike);
                default:
                    return getString(R.string.map);
            }
        }
    }
}
