package com.example.hmod_.bike.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hmod_.bike.R;

import net.crosp.libs.android.circletimeview.CircleTimeView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyTripActivity extends Fragment {
    //    Intent intentThatStartedThisActivity;

    @BindView(R.id.circle_timer_view)
    CircleTimeView circle_timer_view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_trip, container, false);
        setHasOptionsMenu(true);

        //        intentThatStartedThisActivity = Objects.requireNonNull(getActivity()).getIntent();
        ButterKnife.bind(this, rootView);

        circle_timer_view.setCurrentTime(0);
        circle_timer_view.startTimer();

        circle_timer_view.setCircleTimeListener(new CircleTimeView.CircleTimeListener() {
            @Override
            public void onTimeManuallySet(long time) {
                Log.d("TIME LISTENER", "onTimeManuallySet " + time);
            }

            @Override
            public void onTimeManuallyChanged(long time) {
                Log.d("TIME LISTENER", "onTimeManuallyChanged " + time);
            }

            @Override
            public void onTimeUpdated(long time) {
                Log.d("TIME LISTENER", "onTimeUpdated " + time);
            }
        });

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
            }
        });

        /*start_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle_timer_view.startTimer();
            }
        });

        stop_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle_timer_view.stopTimer();
            }
        });*/
        return rootView;
    }
}
