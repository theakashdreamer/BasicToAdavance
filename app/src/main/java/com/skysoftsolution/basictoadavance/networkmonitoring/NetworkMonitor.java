package com.skysoftsolution.basictoadavance.networkmonitoring;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public final class NetworkMonitor {

    public static final class Status {
        public enum Type { AVAILABLE, UNAVAILABLE, LOSING, LOST }

        public final Type type;
        @Nullable public final Integer maxMsToLive;

        private Status(Type t, @Nullable Integer ms) {
            this.type = t;
            this.maxMsToLive = ms;
        }

        public static Status available() { return new Status(Type.AVAILABLE, null); }
        public static Status unavailable() { return new Status(Type.UNAVAILABLE, null); }
        public static Status losing(int ms) { return new Status(Type.LOSING, ms); }
        public static Status lost() { return new Status(Type.LOST, null); }

        @Override
        public String toString() {
            return type == Type.LOSING ? "LOSING(" + maxMsToLive + "ms)" : type.name();
        }
    }

    private static volatile NetworkMonitor INSTANCE;

    public static NetworkMonitor getInstance() {
        if (INSTANCE == null) {
            synchronized (NetworkMonitor.class) {
                if (INSTANCE == null) INSTANCE = new NetworkMonitor();
            }
        }
        return INSTANCE;
    }

    private Context appContext;
    private ConnectivityManager cm;

    private final MutableLiveData<Status> statusLive = new MutableLiveData<>(Status.unavailable());
    private final MediatorLiveData<Boolean> isConnectedLive = new MediatorLiveData<>();

    private NetworkMonitor() {
        isConnectedLive.addSource(statusLive,
                s -> isConnectedLive.setValue(s.type == Status.Type.AVAILABLE));
    }

    private final ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            updateFromCapabilities(network);
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities caps) {
            boolean validated = hasValidatedInternet(caps);
            statusLive.postValue(validated ? Status.available() : Status.unavailable());
        }

        @Override
        public void onLosing(@NonNull Network network, int maxMsToLive) {
            statusLive.postValue(Status.losing(maxMsToLive));
        }

        @Override
        public void onLost(@NonNull Network network) {
            statusLive.postValue(Status.lost());
            // No need to re-check currentHasInternet(), it'll be updated if a new network is found
        }

        @Override
        public void onUnavailable() {
            statusLive.postValue(Status.unavailable());
        }
    };

    @MainThread
    public synchronized void init(@NonNull Context context) {
        if (appContext != null) return;

        appContext = context.getApplicationContext();
        cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        statusLive.setValue(currentHasInternet() ? Status.available() : Status.unavailable());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(callback);
        } else {
            NetworkRequest req = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            cm.registerNetworkCallback(req, callback);
        }
    }

    public synchronized void shutdown() {
        if (cm == null) return;
        try {
            cm.unregisterNetworkCallback(callback);
        } catch (Exception ignored) {}
    }

    public LiveData<Status> getStatusLive() { return statusLive; }
    public LiveData<Boolean> getIsConnectedLive() { return isConnectedLive; }
    public boolean isOnlineNow() { return currentHasInternet(); }

    private boolean currentHasInternet() {
        if (cm == null) return false;
        Network active = cm.getActiveNetwork();
        if (active == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(active);
        if (caps == null) return false;
        return hasValidatedInternet(caps); // ✅ Only trust VALIDATED
    }

    private boolean hasValidatedInternet(@NonNull NetworkCapabilities caps) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        // Fallback: treat basic INTERNET capability as best effort (older APIs)
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void updateFromCapabilities(@NonNull Network network) {
        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        if (caps == null) {
            statusLive.postValue(Status.unavailable());
            return;
        }
        statusLive.postValue(hasValidatedInternet(caps) ? Status.available() : Status.unavailable());
    }
}
