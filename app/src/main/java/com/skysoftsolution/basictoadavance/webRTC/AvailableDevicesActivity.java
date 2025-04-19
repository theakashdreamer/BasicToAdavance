package com.skysoftsolution.basictoadavance.webRTC;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.skysoftsolution.basictoadavance.R;
import com.skysoftsolution.basictoadavance.databinding.ActivityAvailableDevicesBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AvailableDevicesActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private WifiManager wifiManager;
    private ListView listViewBluetooth, listViewWifi;
    private ArrayAdapter<String> bluetoothDevicesAdapter, wifiDevicesAdapter;
    private final int REQUEST_ENABLE_BT = 1;
    private final int REQUEST_PERMISSION = 100;
    private ActivityAvailableDevicesBinding binding;

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager.PeerListListener peerListListener;
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvailableDevicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        bluetoothDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        wifiDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);

        binding.listViewBluetooth.setAdapter(bluetoothDevicesAdapter);
        binding.listViewWifi.setAdapter(wifiDevicesAdapter);
        binding.scanBluetooth.setOnClickListener(v -> scanBluetoothDevices());

        binding.listViewBluetooth.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDevice = bluetoothDevicesAdapter.getItem(position);
            if (selectedDevice != null) {
                String address = selectedDevice.substring(selectedDevice.length() - 17); // Extract MAC address
                connectToDevice(address);
            }
        });

        binding.scanWifi.setOnClickListener(v -> discoverPeers());


        checkPermissions();
    }

    private void discoverPeers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {// TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(AvailableDevicesActivity.this, "Scanning for peers...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(AvailableDevicesActivity.this, "Discovery failed!", Toast.LENGTH_SHORT).show();
            }
        });

        peerListListener = peers -> {
            wifiDevicesAdapter.clear();
            for (WifiP2pDevice device : peers.getDeviceList()) {
                wifiDevicesAdapter.add(device.deviceName + " - " + device.deviceAddress);
            }
            wifiDevicesAdapter.notifyDataSetChanged();
        };

        wifiP2pManager.requestPeers(channel, peerListListener);
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(String address) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        new Thread(() -> {
            BluetoothSocket socket = null;
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                bluetoothAdapter.cancelDiscovery(); // Stop discovery before connecting
                socket.connect(); // Attempt connection
                runOnUiThread(() -> Toast.makeText(AvailableDevicesActivity.this, "Connected to " + device.getName(), Toast.LENGTH_SHORT).show());
                manageConnectedSocket(socket);

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AvailableDevicesActivity.this, "Connection Failed! Retrying..." + e.getMessage(), Toast.LENGTH_SHORT).show());

                // Try an alternative connection method
                try {
                    socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                    socket.connect();
                    runOnUiThread(() -> Toast.makeText(AvailableDevicesActivity.this, "Connected via fallback!", Toast.LENGTH_SHORT).show());
                    manageConnectedSocket(socket);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(AvailableDevicesActivity.this, "Final connection attempt failed!" + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                byte[] buffer = new byte[1024];
                int bytes;

                while ((bytes = inputStream.read(buffer)) > 0) {
                    String receivedMessage = new String(buffer, 0, bytes);
                    runOnUiThread(() -> Toast.makeText(AvailableDevicesActivity.this, "Received: " + receivedMessage, Toast.LENGTH_SHORT).show());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_PERMISSION);
        }
    }

    private void scanBluetoothDevices() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        bluetoothDevicesAdapter.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothDevicesAdapter.add(device.getName() + " - " + device.getAddress());
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);

        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    bluetoothDevicesAdapter.add(device.getName() + " - " + device.getAddress());
                    bluetoothDevicesAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void scanWifiNetworks() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Enabling WiFi...", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        wifiDevicesAdapter.clear();
        wifiManager.startScan();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<ScanResult> scanResults = wifiManager.getScanResults();

        for (ScanResult scanResult : scanResults) {
            wifiDevicesAdapter.add(scanResult.SSID + " - " + scanResult.BSSID);
        }

        wifiDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(bluetoothReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void connectToPeer(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(AvailableDevicesActivity.this, "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(AvailableDevicesActivity.this, "Connection failed!", Toast.LENGTH_SHORT).show();
            }
        });

        connectionInfoListener = info -> {
            if (info.groupFormed && info.isGroupOwner) {
                startServer();
            } else if (info.groupFormed) {
                startClient(info.groupOwnerAddress.getHostAddress());
            }
        };

        wifiP2pManager.requestConnectionInfo(channel, connectionInfoListener);
    }

    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8888);
                Socket client = serverSocket.accept();
                OutputStream outputStream = client.getOutputStream();
                outputStream.write("Hello from server".getBytes());
                outputStream.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startClient(String hostAddress) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(hostAddress, 8888);
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytes = inputStream.read(buffer);
                String message = new String(buffer, 0, bytes);
                runOnUiThread(() -> Toast.makeText(AvailableDevicesActivity.this, "Received: " + message, Toast.LENGTH_SHORT).show());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}