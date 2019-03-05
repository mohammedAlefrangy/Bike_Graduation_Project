package com.example.hmod_.bike.Activity;


import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.hmod_.bike.R;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConnectBike extends Fragment {

    Intent intentThatStartedThisActivity ;

    @BindView(R.id.rent_now)
    Button rent_now;
    @BindView(R.id.spinner)
    Spinner spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_connect_bike, container, false);
        intentThatStartedThisActivity = Objects.requireNonNull(getActivity()).getIntent();
        ButterKnife.bind(this, rootView);
//        getSupportActionBar().setTitle("Connect to a bike");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);




        final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 10);
        myAnim.setInterpolator(interpolator);

        Timer timer = new Timer();
        //Set the schedule function
        timer.scheduleAtFixedRate(new TimerTask() {

                                      @Override
                                      public void run() {
                                          // Magic here
                                          rent_now.startAnimation(myAnim);
                                      }
                                  },
                0, 1000); // 1000 Millisecond  = 1 second


        rent_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
//
//                // Use bounce interpolator with amplitude 0.2 and frequency 20
//                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 10);
//                myAnim.setInterpolator(interpolator);
//
//                rent_now.startAnimation(myAnim);

//                final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
//                rent_now.startAnimation(myAnim);
//                int[] attrs = new int[]{R.attr.selectableItemBackground};
//                TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
//                int backgroundResource = typedArray.getResourceId(0, 1);
//                rent_now.setBackgroundResource(backgroundResource);

            }
        });



        return rootView ;
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//    }

}
