package com.example.hmod_.bike;

import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

public interface BluetoothListener {
    void onConnected(SimpleBluetoothDeviceInterface deviceInterface);
    void onMessageReceived(String message);
    void onError(Throwable error);
}
