package com.example.hmod_.bike.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hmod_.bike.Adapter.AdapterForListItem;
import com.example.hmod_.bike.R;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRentActivity extends Fragment implements AdapterForListItem.OnItemClickListener {

    GridLayoutManager layoutManager;
    private AdapterForListItem mAdapter;
    private AdapterForListItem.OnItemClickListener onItemClickListener;

    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> date_and_price = new ArrayList<>();
    Intent intentThatStartedThisActivity;

    private static final String TAG = "MyRentActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter;
    BroadcastReceiver receiver;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_rent, container, false);
        intentThatStartedThisActivity = Objects.requireNonNull(getActivity()).getIntent();
        ButterKnife.bind(this, rootView);

//        recyclerView = rootView.findViewById(R.id.recyclerView);
//        ButterKnife.bind(getActivity());
        onItemClickListener = this;


        initText();

        // Get an instance of the BluetoothAdapter class
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();

        if (mBluetoothAdapter == null) {
            // If the adapter is null it means that the device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            // We need to enable the Bluetooth, so we ask the user
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // REQUEST_ENABLE_BT es un valor entero que vale 1
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothAdapter.startDiscovery();
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "onCreate: " + pairedDevices.size());
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG, "Device found: " + deviceName + "; MAC " + deviceHardwareAddress);
            }
        }


        return rootView;
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "onReceive: " + deviceName + " " + deviceHardwareAddress);
            }
        }
    };

    private void initText() {
        time.add(" 1 Hour and 32 Minutes ");
        date_and_price.add(" 2019/01/04 01:08pm -- 2 NIS ");

        time.add(" 15 Minutes ");
        date_and_price.add(" 2019/01/11 10:17pm -- 1 NIS ");

        time.add(" 30 Minutes ");
        date_and_price.add(" 2019/01/7 11:32pm -- 4 NIS ");

        time.add(" 2 Hour and 15 Minutes ");
        date_and_price.add(" 2019/01/04 12:08pm -- 3 NIS ");


        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterForListItem(time, date_and_price, getContext(), onItemClickListener);
        recyclerView.setAdapter(mAdapter);
        layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(getActivity(), " Thank You :) ", Toast.LENGTH_SHORT).show();
    }

}
