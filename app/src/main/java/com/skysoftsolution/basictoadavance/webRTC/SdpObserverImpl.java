package com.skysoftsolution.basictoadavance.webRTC;
import android.util.Log;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class SdpObserverImpl implements SdpObserver {

    private static final String TAG = "SdpObserver";

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        Log.d(TAG, "SDP Creation Success: " + sessionDescription.type);
    }

    @Override
    public void onSetSuccess() {
        Log.d(TAG, "SDP Set Success");
    }

    @Override
    public void onCreateFailure(String error) {
        Log.e(TAG, "SDP Creation Failure: " + error);
    }

    @Override
    public void onSetFailure(String error) {
        Log.e(TAG, "SDP Set Failure: " + error);
    }
}
