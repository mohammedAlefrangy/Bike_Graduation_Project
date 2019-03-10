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

import com.example.hmod_.bike.R;

import net.crosp.libs.android.circletimeview.CircleTimeView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyTripActivity extends Fragment {
    //    Intent intentThatStartedThisActivity;

    @BindView(R.id.circle_timer_view)
    CircleTimeView circle_timer_view;
    @BindView(R.id.rentedBike)
    TextView rentedBike;
    @BindView(R.id.estimatedCharges)
    TextView estimatedCharges;


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

        rentedBike.setText(getString(R.string.rented_bike_number) + MainActivity.currentRent.getBikeNumber());
        Date now = new Date ();

        circle_timer_view.setCurrentTime( (now.getTime() - MainActivity.currentRent.getStartTime().getTime()) / 1000);
        circle_timer_view.startTimer();
        String estimatedChargesFormat = getString(R.string.estimated_charges);
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

        return rootView;
    }
}
