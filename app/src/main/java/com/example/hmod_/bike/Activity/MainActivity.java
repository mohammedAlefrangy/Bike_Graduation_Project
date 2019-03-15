package com.example.hmod_.bike.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hmod_.bike.R;
import com.example.hmod_.bike.Rent;
import com.example.hmod_.bike.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.location)
    ImageButton location;
    private NavigationView navigationView;

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    public static FirebaseUser currentAuthUser;

    public static User currentUser;
    public static final FirebaseFunctions ff = FirebaseFunctions.getInstance();
    public static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static MainActivity mainActivity;
    public static Rent currentRent = null;
    public static String currentBikeKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.find_bike);
        changeFragment (R.id.find_bike);

        //TODO: Move the firebase auth to splach activity
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            createSignInIntent();
        } else {
            updateUser();
        }


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


    public void updateUser() {
        currentAuthUser = mAuth.getCurrentUser();
        db.collection("users").document(currentAuthUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    currentUser = document.toObject(User.class);

                } else {
                    currentUser = new User(currentAuthUser);
                    db.collection("users").document(currentAuthUser.getUid()).set(currentUser);
                }
                updateUI();
            }
        });
    }

    private void updateUI() {
        TextView usernameTV = navigationView.getHeaderView(0).findViewById(R.id.userName);
        usernameTV.setText(currentUser.getName());
        TextView creditsTV = navigationView.getHeaderView(0).findViewById(R.id.credits);
        creditsTV.setText("Credits: " + currentUser.getCredits() + " NIS");
        if (MyAccount.instance != null) MyAccount.instance.updateUI();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        changeFragment (id);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment (int id) {
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.find_bike) {
            fragmentClass = MapsActivity.class;
        } else if (id == R.id.connect_to_bike) {
            fragmentClass = ConnectBike.class;
        } else if (id == R.id.my_rents) {
            fragmentClass = MyRentActivity.class;
        } else if (id == R.id.my_account) {
            fragmentClass = MyAccount.class;
        } else if (id == R.id.my_trip) {
            fragmentClass = MyTripActivity.class;
        } else if (id == R.id.where_to_buy_vouchers) {
            fragmentClass = WhereVouchersActivity.class;
        } else if (id == R.id.rate_us) {

        } else if (id == R.id.log_out) {
//            AuthUI.getInstance().signOut(this);
        }

        try {
            if (fragmentClass != null) {
                fragment = (Fragment) fragmentClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    public void setCurrentRent (Rent rent) {
        MainActivity.currentRent = rent;
        boolean inRent = currentRent != null;
        Menu menu = navigationView.getMenu();
        menu.getItem(1).setVisible(!inRent);
        menu.getItem(2).setVisible(inRent);
        if (menu.getItem(1).isChecked()) {
            changeFragment(R.id.my_trip);
            navigationView.setCheckedItem(R.id.my_trip);
        } else if (menu.getItem(2).isChecked()) {
            changeFragment(R.id.my_rents);
            navigationView.setCheckedItem(R.id.my_rents);
        }
    }
}