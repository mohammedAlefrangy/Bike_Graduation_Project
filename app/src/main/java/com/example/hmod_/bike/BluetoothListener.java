package com.example.hmod_.bike;

import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

public interface BluetoothListener {
    public void onConnected (SimpleBluetoothDeviceInterface deviceInterface);
    public void onMessageReceived (String message);
    public void onError (Throwable error);
}
