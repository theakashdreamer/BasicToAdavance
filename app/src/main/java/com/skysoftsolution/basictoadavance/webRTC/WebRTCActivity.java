package com.skysoftsolution.basictoadavance.webRTC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skysoftsolution.basictoadavance.R;
import com.skysoftsolution.basictoadavance.webRTC.adapter.ChatAdapter;
import com.skysoftsolution.basictoadavance.webRTC.adapter.DeviceAdapter;
import com.skysoftsolution.basictoadavance.webRTC.callBacks.SignalingInterface;
import com.skysoftsolution.basictoadavance.webRTC.entity.ChatMessage;
import com.skysoftsolution.basictoadavance.webRTC.entity.DeviceItem;
import com.skysoftsolution.basictoadavance.webRTC.entity.NearbyWebRTCManager;
import com.skysoftsolution.basictoadavance.webRTC.utils.BluetoothServerThread;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class WebRTCActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice selectedDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private PeerConnection peerConnection;
    private PeerConnectionFactory peerConnectionFactory;
    private DataChannel dataChannel;
    private Handler uiHandler; // UI handler for Bluetooth messages
    private EditText messageInput;
    private TextView tvChat;
    private Button btnEnableBluetooth, btnDiscoverDevices, btnStartConnection;
    private TextView tvStatus;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private RecyclerView recyclerViewChat;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    ImageView btnSendMessage, mic_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_rtcactivity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2"))); // Change to your desired color
        }
        btnEnableBluetooth = findViewById(R.id.btnEnableBluetooth);
        btnDiscoverDevices = findViewById(R.id.btnDiscoverDevices);
        btnStartConnection = findViewById(R.id.btnStartConnection);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        mic_button = findViewById(R.id.mic_button);
        messageInput = findViewById(R.id.message_input);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);
        if (bluetoothAdapter == null) {
            showToast("Bluetooth not supported");
            return;
        }

        btnSendMessage.setOnClickListener(view -> sendMessage());
        uiHandler = new Handler(Looper.getMainLooper(), msg -> {
            String receivedMessage = (String) msg.obj;
            chatMessages.add(new ChatMessage(receivedMessage, false));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
            return true;
        });
        BluetoothServerThread serverThread = new BluetoothServerThread(bluetoothAdapter, uiHandler);
        serverThread.start();
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.toString().trim().isEmpty()) {
                    btnSendMessage.setVisibility(View.VISIBLE);
                    btnSendMessage.setVisibility(View.GONE);
                } else {
                    btnSendMessage.setVisibility(View.GONE);
                    btnSendMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            showToast("Bluetooth Enabled");
        }
    }

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

    @SuppressLint("MissingPermission")
    private void startBluetoothConnection() {
        if (selectedDevice == null) {
            showToast("No Bluetooth Device Selected!");
            return;
        }
        new Thread(() -> {
            try {
                bluetoothAdapter.cancelDiscovery();
                bluetoothSocket = selectedDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();
                showToast("Bluetooth Connected");
                startListeningForMessages();
            } catch (IOException e) {
                showToast("Connection Failed, Retrying...");
                Log.e("Bluetooth", "Error: " + e.getMessage());
            }
        }).start();
    }

    private void startListeningForMessages() {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                int bytes;
                StringBuilder messageBuilder = new StringBuilder();
                while ((bytes = inputStream.read(buffer)) > 0) {
                    String receivedMessage = new String(buffer, 0, bytes);
                    Log.d("Bluetooth11111", "🔵 Raw received: " + receivedMessage); // Log received data
                    messageBuilder.append(receivedMessage);
                    // Check for End-of-Message Indicator (Newline or Special Char)
                    if (receivedMessage.contains("\n") || receivedMessage.endsWith("EOF")) {
                        final String fullMessage = messageBuilder.toString().trim();
                        messageBuilder.setLength(0); // Reset builder
                        Log.d("Bluetooth11111", "🟢 Complete message received: " + fullMessage); // Log full message
                        runOnUiThread(() -> {
                            Log.d("Bluetooth11111", "🟡 Updating RecyclerView with: " + fullMessage);
                            chatMessages.add(new ChatMessage(fullMessage, false));
                            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                            recyclerViewChat.scrollToPosition(chatMessages.size() - 1); // Auto-scroll
                            Log.d("Bluetooth11111", "✅ RecyclerView updated successfully.");
                        });
                    }
                }
            } catch (IOException e) {
                Log.e("Bluetooth11111", "❌ Error receiving message: ", e);
            }
        }).start();
    }


    private void sendMessage() {
        String message = messageInput.getText().toString();
        if (message.isEmpty()) return;
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
            }
        }).start();
    }

    private void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    private void initializeWebRTC() {
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this)
                .setEnableInternalTracer(true)
                .createInitializationOptions());

        peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory();

        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver() {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                sendMessageOverBluetooth("ICE:" + iceCandidate.sdp);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d("WebRTC", "Stream added");
            }
        });

        // Create Data Channel for Text Communication
        DataChannel.Init dcInit = new DataChannel.Init();
        dataChannel = peerConnection.createDataChannel("chat", dcInit);
        dataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long previousAmount) {
            }

            @Override
            public void onStateChange() {
            }

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

    private void sendMessageOverBluetooth(String message) {
        if (outputStream == null) return;
        new Thread(() -> {
            try {
                outputStream.write(message.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                Log.e("Bluetooth", "Error sending message", e);
            }
        }).start();
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