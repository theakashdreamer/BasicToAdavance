package com.skysoftsolution.basictoadavance.networkmonitoring;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
public class NetworkViewModel extends AndroidViewModel {

    public NetworkViewModel(@NonNull Application application) { super(application); }

    public LiveData<NetworkMonitor.Status> getStatus() {
        return NetworkMonitor.getInstance().getStatusLive();
    }

    public LiveData<Boolean> isConnected() {
        return NetworkMonitor.getInstance().getIsConnectedLive();
    }

    public boolean isOnlineNow() {
        return NetworkMonitor.getInstance().isOnlineNow();
    }
}
