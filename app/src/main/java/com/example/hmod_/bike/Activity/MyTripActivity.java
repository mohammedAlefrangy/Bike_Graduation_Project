package com.example.hmod_.bike.Activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hmod_.bike.BluetoothControlUnit;
import com.example.hmod_.bike.BluetoothListener;
import com.example.hmod_.bike.R;
import com.example.hmod_.bike.Rent;
import com.example.hmod_.bike.SplachActivity;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import net.crosp.libs.android.circletimeview.CircleTimeView;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyTripActivity extends Fragment implements BluetoothListener {
    //    Intent intentThatStartedThisActivity;

    @BindView(R.id.circle_timer_view)
    CircleTimeView circle_timer_view;
    @BindView(R.id.rentedBike)
    TextView rentedBike;
    @BindView(R.id.estimatedCharges)
    TextView estimatedCharges;
    @BindView(R.id.returnBike)
    Button returnBikeBtn;
    @BindView(R.id.reopenBike)
    Button reopenBikeBtn;

    private BluetoothControlUnit bluetoothControlUnit;

    private int pendingJob = 0; // 0 nothing, 1 return bike, 2 repoen bike
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_trip, container, false);
        setHasOptionsMenu(true);

        //        intentThatStartedThisActivity = Objects.requireNonNull(getActivity()).getIntent();
        ButterKnife.bind(this, rootView);
        if (MainActivity.currentRent == null) {
            // TODO: We should show some error
            return rootView;
        }
        bluetoothControlUnit = BluetoothControlUnit.getInstance();
        bluetoothControlUnit.setListener(this);

        rentedBike.setText(getString(R.string.rented_bike_number) + " " + MainActivity.currentRent.getBikeNumber());
        Date now = new Date ();
        long currentDuration = (now.getTime() - MainActivity.currentRent.getStartTime().getTime()) / 1000;
        circle_timer_view.setCurrentTime(currentDuration);
        circle_timer_view.startTimer();
        String estimatedChargesFormat = getString(R.string.estimated_charges);
        estimatedCharges.setText(String.format(estimatedChargesFormat , (currentDuration/3600.0) * 2));
        circle_timer_view.setCircleTimerListener(new CircleTimeView.CircleTimerListener() {
            @Override
            public void onTimerStop() {
                Log.d("TIMER LISTENER", "onTimerStop ");
            }

            @Override
            public void onTimerStart(long time) {
                Log.d("TIMER LISTENER", "onTimerStart " + time);
            }

            @Override
            public void onTimerTimeValueChanged(long time) {
                estimatedCharges.setText(String.format(estimatedChargesFormat , (time/3600.0) * 2));
            }
        });
        returnBikeBtn.setOnClickListener(view -> returnBike ());
        reopenBikeBtn.setOnClickListener(view -> reopenBike ());
        MainActivity.mainActivity.getSupportActionBar().setTitle("My Trip");
        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void returnBike () {
        if (isNetworkAvailable () ) {
            if (bluetoothControlUnit.isConnected()) {
                SimpleBluetoothDeviceInterface deviceInterface = bluetoothControlUnit.getDeviceInterface();
                deviceInterface.sendMessage("retu:" + MainActivity.currentBikeKey);
            } else {
                pendingJob = 1;
                bluetoothControlUnit.reconnectToLastDevice();
            }
        } else {
            Toast.makeText(getActivity(), "Please connect to the internet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void reopenBike () {
        if (bluetoothControlUnit.isConnected()) {
            SimpleBluetoothDeviceInterface deviceInterface = bluetoothControlUnit.getDeviceInterface();
            deviceInterface.sendMessage("rent:" + MainActivity.currentBikeKey);
        } else {
            pendingJob = 2;
            bluetoothControlUnit.reconnectToLastDevice();
        }
    }

    @Override
    public void onConnected(SimpleBluetoothDeviceInterface deviceInterface) {
        if (pendingJob == 1) {
            returnBike ();
        } else if (pendingJob == 2){
            reopenBike ();
        }
    }

    @Override
    public void onMessageReceived(String message) {
        if (message.startsWith("retu:")) {
            String returnKey = message.substring(5);
            Map<String, Object> data = new HashMap<>();
            data.put("bike", MainActivity.currentRent.getBikeNumber());
            data.put("key", returnKey);
            data.put("rentid", MainActivity.currentRent.id);
            data.put("station", "nbqE62Fk1sk0ybcIllob");
            MainActivity.ff.getHttpsCallable("returnBike").call(data).addOnSuccessListener(httpsCallableResult -> {
                MainActivity.setCurrentBikeKey (null);
                Rent.updateCurrentRent(null);
                if (httpsCallableResult.getData() instanceof Map) {
                    Map<String, Object> dataObj = (Map<String, Object>) httpsCallableResult.getData();
                    Toast.makeText(getActivity(), (String) dataObj.get("msg"), Toast.LENGTH_SHORT).show();
                    SplachActivity.updateUser();
                }
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
        } else if (message.startsWith("clos:")) {
            Toast.makeText(getActivity(), "Please lock the bike to return it.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(Throwable error) {
        String errorMsg = "";
        if (error instanceof IOException) {
            if (error.getMessage().contains("timeout"))
                errorMsg = "Couldn't connect to the bike.";
        }
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}
