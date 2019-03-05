package com.example.hmod_.bike.Activity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hmod_.bike.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Experimental;
import com.google.firebase.functions.HttpsCallableResult;
import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import org.json.JSONObject;

public class ConnectBike extends Fragment {

    Intent intentThatStartedThisActivity ;

    @BindView(R.id.rent_now)
    Button rent_now;
    @BindView(R.id.spinner)
    Spinner spinner;

    private HashMap<String, BluetoothDevice> blDevices = new HashMap<>();
    private List<String> blDevicesName = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private final String TAG = "bluetooth";
    BluetoothManager bluetoothManager = BluetoothManager.getInstance();
    private SimpleBluetoothDeviceInterface deviceInterface;
    private String bikeNumber = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_connect_bike, container, false);
        intentThatStartedThisActivity = Objects.requireNonNull(getActivity()).getIntent();
        ButterKnife.bind(this, rootView);
//        getSupportActionBar().setTitle("Connect to a bike");

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, blDevicesName);
        // Specify the layout to use when the list of choices appearsfinal private
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            // We need to enable the Bluetooth, so we ask the user
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // REQUEST_ENABLE_BT es un valor entero que vale 1
            startActivityForResult(enableBtIntent, 1);
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        MainActivity.mainActivity.registerReceiver(mReceiver, filter);
        if (mBluetoothAdapter.isDiscovering()) {
            // Bluetooth is already in modo discovery mode, we cancel to restart it again
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        /*BluetoothManager bluetoothManager = BluetoothManager.getInstance();
        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(context, "Bluetooth not available.", Toast.LENGTH_LONG).show();

        }*/
        rent_now.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                connectButton ();
            }
        });


        return rootView ;

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
                    if (blDevices.get(device.getName()) == null){
                        blDevices.put (device.getName(), device);
                        blDevicesName.add(device.getName().substring(5));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    public void connectButton () {
        bikeNumber = (String) spinner.getSelectedItem();
        BluetoothDevice blDevice = blDevices.get("Bike-"+bikeNumber);
        Log.d(TAG, "Connect to :"+blDevice.getName());
        bluetoothManager.openSerialDevice(blDevice.getAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
    }
    private void onConnected(BluetoothSerialDevice connectedDevice) {
        deviceInterface = connectedDevice.toSimpleDeviceInterface();
        if (bikeNumber.isEmpty()) return;
        Map<String, Object> data = new HashMap<>();
        data.put("bike", bikeNumber);
        MainActivity.ff.getHttpsCallable("rentBike").call(data).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                if (httpsCallableResult.getData() instanceof Map) {
                    Map<String, Object> dataObj = (Map<String, Object>) httpsCallableResult.getData();
                    try {
                        deviceInterface.sendMessage((String) dataObj.get("key"));
                    } catch (Exception ex) {}
                }
            }
        });

    }
    private void onError(Throwable error) {
        // Handle the error
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBluetoothAdapter.cancelDiscovery();
        MainActivity.mainActivity.unregisterReceiver(mReceiver);

    }


}
