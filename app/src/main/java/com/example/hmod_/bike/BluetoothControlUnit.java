package com.example.hmod_.bike;

import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.hmod_.bike.Activity.MainActivity;
import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothControlUnit {
    private BluetoothManager bluetoothManager;
    private SimpleBluetoothDeviceInterface deviceInterface;
    private BluetoothListener listener;
    private String lastConnectedDeviceAddress = null;
    private boolean _isConnected = false;

    private static BluetoothControlUnit instance;

    public static BluetoothControlUnit getInstance() {
        if (instance == null){
            instance = new BluetoothControlUnit();
        }
        return instance;
    }

    public void connectToDevice(String address) {
        if (bluetoothManager != null) bluetoothManager.close();
        bluetoothManager = BluetoothManager.getInstance();
        bluetoothManager.openSerialDevice(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
    }

    public void reconnectToLastDevice () {
        lastConnectedDeviceAddress = MainActivity.prefs.getString("lastConnectedDeviceAddress", null);
        connectToDevice (lastConnectedDeviceAddress);
    }

    public SimpleBluetoothDeviceInterface getDeviceInterface() {
        return deviceInterface;
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {
        _isConnected = true;
        lastConnectedDeviceAddress = connectedDevice.getMac();
        SharedPreferences.Editor editor = MainActivity.prefs.edit();
        editor.putString("lastConnectedDeviceAddress", lastConnectedDeviceAddress);
        editor.commit();

        deviceInterface = connectedDevice.toSimpleDeviceInterface();
        deviceInterface.setListeners(this::onMessageReceived, null, this::onError);
        listener.onConnected(deviceInterface);
        Log.d("Bluetooth connected:", connectedDevice.getMac());
    }

    private void onError(Throwable error) {
        String errorMsg = error.getMessage();
        if (errorMsg.contains("socket closed") || errorMsg.contains("Broken pipe")) {
            _isConnected = false;
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
    public boolean isConnected () {
        return _isConnected;
    }
}
