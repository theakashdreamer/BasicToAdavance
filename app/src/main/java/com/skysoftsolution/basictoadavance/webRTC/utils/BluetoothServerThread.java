package com.skysoftsolution.basictoadavance.webRTC.utils;
import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.io.OutputStream;
public class BluetoothServerThread extends Thread {
    private final BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Handler uiHandler;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @SuppressLint("MissingPermission")
    public BluetoothServerThread(BluetoothAdapter bluetoothAdapter, Handler handler) {
        BluetoothServerSocket tmp = null;
        this.uiHandler = handler; // Store handler reference

        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("WebRTC_BT", MY_UUID);
        } catch (IOException e) {
            Log.e("BluetoothServer", "Socket creation failed", e);
        }
        serverSocket = tmp;
    }
    public void run() {
        try {
            Log.d("BluetoothServer", "Waiting for connection...");
            socket = serverSocket.accept(); // Blocks until a connection is made
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            Log.d("BluetoothServer", "Client connected!");
            listenForMessages();
        } catch (IOException e) {
            Log.e("BluetoothServer", "Error accepting connection", e);
        }
    }

    private void listenForMessages() {
        byte[] buffer = new byte[1024];
        int bytes;
        try {
            while ((bytes = inputStream.read(buffer)) > 0) {
                String receivedMessage = new String(buffer, 0, bytes);
                Log.d("BluetoothServer", "Received: " + receivedMessage);
                uiHandler.post(() -> {
                    Log.d("BluetoothServer", "✅ Sending to UI: " + receivedMessage);
                    uiHandler.obtainMessage(1, receivedMessage).sendToTarget();
                });

            }
        } catch (IOException e) {
            Log.e("BluetoothServer", "Error reading message", e);
        }
    }
}
