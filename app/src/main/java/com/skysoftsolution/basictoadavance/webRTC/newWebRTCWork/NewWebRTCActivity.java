package com.skysoftsolution.basictoadavance.webRTC.newWebRTCWork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.skysoftsolution.basictoadavance.R;
import com.skysoftsolution.basictoadavance.webRTC.CustomPeerConnectionObserver;
import com.skysoftsolution.basictoadavance.webRTC.adapter.ChatAdapter;
import com.skysoftsolution.basictoadavance.webRTC.entity.ChatMessage;

import org.webrtc.DataChannel;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NewWebRTCActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice selectedDevice;
    private OutputStream outputStream;
    private InputStream inputStream;
    private PeerConnection peerConnection;
    private PeerConnectionFactory peerConnectionFactory;
    private DataChannel dataChannel;
    private Handler uiHandler;
    private EditText messageInput;
    private Button btnEnableBluetooth, btnDiscoverDevices, btnStartConnection;
    ImageView btnSendMessage;
    private RecyclerView recyclerViewChat;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_web_rtcactivity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2"))); // Change to your desired color
        }
        // Initialize UI
        btnEnableBluetooth = findViewById(R.id.btnEnableBluetooth);
        btnDiscoverDevices = findViewById(R.id.btnDiscoverDevices);
        btnStartConnection = findViewById(R.id.btnStartConnection);
        btnSendMessage = findViewById(R.id.btnSendMessage12);
        messageInput = findViewById(R.id.message_input12);
        recyclerViewChat = findViewById(R.id.recyclerViewChat1);

        // Initialize RecyclerView
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);

        // Initialize Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        uiHandler = new Handler(Looper.getMainLooper());

        btnStartConnection.setOnClickListener(view -> startBluetoothConnection());


        btnSendMessage.setOnClickListener(view -> sendMessage());

        // Initialize WebRTC
        initializeWebRTC();
    }
    // Enable Bluetooth
    @SuppressLint("MissingPermission")
    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            showToast("Bluetooth Enabled");
        }
    }

    // Discover Bluetooth Devices
    @SuppressLint("MissingPermission")
    private void discoverBluetoothDevices() {
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                selectedDevice = device;
                showToast("Using Paired Device: " + device.getName());
                return;
            }
        }
        showToast("No Paired Devices Found!");
    }

    // Start Bluetooth Connection
    @SuppressLint("MissingPermission")
    private void startBluetoothConnection() {
        if (selectedDevice == null) {
            showToast("No Bluetooth Device Selected!");
            return;
        }
        new Thread(() -> {
            try {
                bluetoothSocket = selectedDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();
                showToast("Bluetooth Connected");
                startListeningForMessages();
            } catch (IOException e) {
                showToast("Connection Failed!");
            }
        }).start();
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (message.isEmpty()) return;

        if (dataChannel != null && dataChannel.state() == DataChannel.State.OPEN) {
            sendMessageOverWebRTC(message);
        } else if (outputStream != null) {
            sendMessageOverBluetooth(message);
        } else {
            showToast("No active connection!");
        }
    }

    private void sendMessageOverWebRTC(String message) {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        DataChannel.Buffer dataBuffer = new DataChannel.Buffer(buffer, false);

        if (dataChannel != null && dataChannel.state() == DataChannel.State.OPEN) {
            dataChannel.send(dataBuffer);
            runOnUiThread(() -> {
                chatMessages.add(new ChatMessage(message, true));
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                messageInput.setText("");
            });
        } else {
            showToast("WebRTC channel not open!");
        }
    }

    private void sendMessageOverBluetooth(String message) {
        new Thread(() -> {
            try {
                outputStream.write(message.getBytes());
                outputStream.flush();
                runOnUiThread(() -> {
                    chatMessages.add(new ChatMessage(message, true));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    messageInput.setText("");
                });
            } catch (IOException e) {
                Log.e("Bluetooth", "Error sending message", e);
                showToast("Failed to send message!");
            }
        }).start();
    }

    private void startListeningForMessages() {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = inputStream.read(buffer)) > 0) {
                    String receivedMessage = new String(buffer, 0, bytes);
                    runOnUiThread(() -> {
                        chatMessages.add(new ChatMessage(receivedMessage, false));
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
                    });
                }
            } catch (IOException e) {
                Log.e("Bluetooth", "Error receiving message", e);
            }
        }).start();
    }

    // Initialize WebRTC
    private void initializeWebRTC() {
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions());
        peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory();

        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver());

        // Create WebRTC DataChannel
        DataChannel.Init dcInit = new DataChannel.Init();
        dataChannel = peerConnection.createDataChannel("chat", dcInit);

        dataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long previousAmount) {}

            @Override
            public void onStateChange() {}

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                ByteBuffer data = buffer.data;
                byte[] bytes = new byte[data.remaining()];
                data.get(bytes);
                String receivedMessage = new String(bytes);
                runOnUiThread(() -> {
                    chatMessages.add(new ChatMessage(receivedMessage, false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                });
            }
        });
    }

    // Show Toast Message
    private void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_enable_bluetooth) {
            enableBluetooth();
            return true;
        } else if (id == R.id.action_discover_devices) {
            discoverBluetoothDevices();
            return true;
        } else if (id == R.id.action_start_connection) {
            startBluetoothConnection();
            return true;
        } else if (id == android.R.id.home) {
            finish(); // Back button functionality
            return true;
        }
        return true;
    }
}