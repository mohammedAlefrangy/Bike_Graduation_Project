package com.example.hmod_.bike;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothControlUnit {
    private final BluetoothManager bluetoothManager = BluetoothManager.getInstance();
    private SimpleBluetoothDeviceInterface deviceInterface;
    private static BluetoothControlUnit instance;
    private BluetoothListener listener;

    public static BluetoothControlUnit getInstance() {
        if (instance == null){
            instance = new BluetoothControlUnit();
        }
        return instance;
    }

    public void connectToDevice(BluetoothDevice device) {
        bluetoothManager.openSerialDevice(device.getAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
    }

    public SimpleBluetoothDeviceInterface getDeviceInterface() {
        return deviceInterface;
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {
        deviceInterface = connectedDevice.toSimpleDeviceInterface();
        deviceInterface.setListeners(this::onMessageReceived, null, this::onError);
        listener.onConnected(deviceInterface);
    }

    private void onError(Throwable error) {
        String errorMsg = error.getMessage();
        if (errorMsg.contains("socket closed")) {
            //Connection lost
        }
        Log.d("Bluetooth error:", error.getMessage());
        listener.onError(error);
    }
    private void onMessageReceived (String messsage) {
        listener.onMessageReceived(messsage);
    }
    public void setListener (BluetoothListener listener) {
        this.listener = listener;
    }
}
