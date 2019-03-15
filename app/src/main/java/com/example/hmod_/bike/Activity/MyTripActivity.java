package com.example.hmod_.bike.Activity;

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
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import net.crosp.libs.android.circletimeview.CircleTimeView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyTripActivity extends Fragment implements BluetoothListener {
    //    Intent intentThatStartedThisActivity;

    @BindView(R.id.circle_timer_view)
    private
    CircleTimeView circle_timer_view;
    @BindView(R.id.rentedBike)
    private
    TextView rentedBike;
    @BindView(R.id.estimatedCharges)
    private
    TextView estimatedCharges;
    @BindView(R.id.returnBike)
    private
    Button returnBikeBtn;
    @BindView(R.id.parkBike)
    Button parkBikeBtn;


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
        BluetoothControlUnit.getInstance().setListener(this);

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
                Log.d("TIMER LISTENER", "onTimerTimeValueChanged " + time);
                estimatedCharges.setText(String.format(estimatedChargesFormat , (time/3600.0) * 2));
            }
        });
        returnBikeBtn.setOnClickListener(view -> returnBike ());
        MainActivity.mainActivity.getSupportActionBar().setTitle("My Trip");
        return rootView;
    }

    private void returnBike () {
        Log.d("sendmes", "retu:" + MainActivity.currentBikeKey);
        SimpleBluetoothDeviceInterface deviceInterface = BluetoothControlUnit.getInstance().getDeviceInterface();
        deviceInterface.sendMessage("retu:" + MainActivity.currentBikeKey);
    }

    @Override
    public void onConnected(SimpleBluetoothDeviceInterface deviceInterface) {

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
                MainActivity.currentBikeKey =  null;
                Rent.updateCurrentRent(null);
                if (httpsCallableResult.getData() instanceof Map) {
                    Map<String, Object> dataObj = (Map<String, Object>) httpsCallableResult.getData();
                    Toast.makeText(getActivity(), (String) dataObj.get("msg"), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onError(Throwable error) {

    }
}
