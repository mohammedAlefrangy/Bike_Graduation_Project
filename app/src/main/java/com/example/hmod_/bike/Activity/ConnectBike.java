package com.example.hmod_.bike.Activity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hmod_.bike.BluetoothControlUnit;
import com.example.hmod_.bike.BluetoothListener;
import com.example.hmod_.bike.R;
import com.example.hmod_.bike.Rent;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class ConnectBike extends Fragment implements BluetoothListener {

//    Intent intentThatStartedThisActivity;

    @BindView(R.id.rent_now)
    private
    Button rent_now;
    @BindView(R.id.spinner)
    private
    Spinner spinner;
    private PulsatorLayout pulsator;

    private final HashMap<String, BluetoothDevice> blDevices = new HashMap<>();
    private final List<String> blDevicesName = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private final String TAG = "bluetooth";

    private String bikeNumber = "";
    private Menu mMenu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_connect_bike, container, false);
        setHasOptionsMenu(true);
//        intentThatStartedThisActivity = Objects.requireNonNull(getActivity()).getIntent();
        ButterKnife.bind(this, rootView);
//        getSupportActionBar().setTitle("Connect to a bike");

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, blDevicesName);
        // Specify the layout to use when the list of choices appearsfinal private
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setEnabled(false);

        //PulsatorLayout animation
        pulsator = rootView.findViewById(R.id.pulsator);
        pulsator.stop();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            MainActivity.mainActivity.registerReceiver(mReceiver, filter);
            if (mBluetoothAdapter.isDiscovering()) {
                // Bluetooth is already in modo discovery mode, we cancel to restart it again
                mBluetoothAdapter.cancelDiscovery();
            }
            mBluetoothAdapter.startDiscovery();
        } else {
            Toast.makeText(getContext(), "Bluetooth not available.", Toast.LENGTH_LONG).show();
        }
        rent_now.setOnClickListener(v -> {
            connectButton();
            pulsator.start();
        });

        MainActivity.mainActivity.getSupportActionBar().setTitle("Connect To A Bike");
        return rootView;

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // A Bluetooth device was found
                // Getting device information from the intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, "Device found: " + device.getName() + "; MAC " + device.getAddress());
                if (device.getName().startsWith("Bike-")) {
                    if (blDevices.get(device.getName()) == null) {
                        spinner.setEnabled(true);
                        blDevices.put(device.getName(), device);
                        blDevicesName.add(device.getName().substring(5));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private void connectButton() {
        bikeNumber = (String) spinner.getSelectedItem();
        if (bikeNumber != null) {
            BluetoothDevice blDevice = blDevices.get("Bike-" + bikeNumber);
            Log.d(TAG, "Connect to :" + blDevice.getName());
            BluetoothControlUnit.getInstance().setListener(this);
            BluetoothControlUnit.getInstance().connectToDevice(blDevice);

        }
        // TODO: If we didn't find a bike
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
            MainActivity.mainActivity.unregisterReceiver(mReceiver);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        this.mMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            Log.d(TAG, "onOptionsItemSelected: " + id);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                AnimatedVectorDrawableCompat.create(getContext(), R.drawable.ic_sync).start();

            } else {
                mMenu.getItem(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_sync));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(SimpleBluetoothDeviceInterface deviceInterface) {
        if (bikeNumber.isEmpty()) return;
        Map<String, Object> data = new HashMap<>();
        data.put("bike", bikeNumber);
        MainActivity.ff.getHttpsCallable("rentBike").call(data).addOnSuccessListener(httpsCallableResult -> {
            if (httpsCallableResult.getData() instanceof Map) {
                Map<String, Object> dataObj = (Map<String, Object>) httpsCallableResult.getData();
                MainActivity.currentBikeKey =  (String) dataObj.get("key");
                Rent.updateCurrentRent((String) dataObj.get ("rentId"));
                deviceInterface.sendMessage("rent:" + dataObj.get("key"));
                pulsator.stop();
            }
        });
    }

    @Override
    public void onMessageReceived(String message) {

    }

    @Override
    public void onError(Throwable error) {

    }
}
