package com.skysoftsolution.basictoadavance.SensorDetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.skysoftsolution.basictoadavance.R;
public class SensorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private long lastGyroTimestamp = 0;
    private float totalAngleX = 0f, totalAngleY = 0f, totalAngleZ = 0f;
    private OnShakeDetectedListener callback;
    private long lastShakeTime = 0;
    private double lastX, lastY, lastZ;

    private static final double SHAKE_THRESHOLD = 2.7; // Tune this (2.5 to 3.0 is good)
    private static final int SHAKE_WAIT_TIME_MS = 500;
    private double lastAccel = 0.0;
    private double currentAccel = SensorManager.GRAVITY_EARTH;
    private double accel = 0.0;

    public interface OnShakeDetectedListener {
        void onShakeDetected(float angleX, float angleY, float angleZ, double shakeStrength);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnShakeDetectedListener) {
            callback = (OnShakeDetectedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnShakeDetectedListener");
        }
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        lastAccel = currentAccel;
        currentAccel = Math.sqrt(x * x + y * y + z * z);
        double delta = currentAccel - lastAccel;
        accel = accel * 0.9 + delta; // low-pass filter
        Log.d("SensorSecondFragment", "Filtered acceleration: " + accel);
        long now = System.currentTimeMillis();
        Log.d("SensorSecondFragment", "Phone shake detected! accel: " + accel);
        if (accel > SHAKE_THRESHOLD && (now - lastShakeTime) > SHAKE_WAIT_TIME_MS) {
            lastShakeTime = now;
            Log.d("SensorSecondFragment11", "Phone was moved with acceleration: after thershould " + accel);
            if (callback != null) {
                callback.onShakeDetected(totalAngleX, totalAngleY, totalAngleZ, accel);
            }
        }

 /*       if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (lastGyroTimestamp != 0) {
                float dt = (event.timestamp - lastGyroTimestamp) * 1.0f / 1_000_000_000.0f; // sec

                float rotX = event.values[0];
                float rotY = event.values[1];
                float rotZ = event.values[2];

                totalAngleX += Math.toDegrees(rotX * dt);
                totalAngleY += Math.toDegrees(rotY * dt);
                totalAngleZ += Math.toDegrees(rotZ * dt);
            }
            lastGyroTimestamp = event.timestamp;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            if (acceleration > SHAKE_THRESHOLD) {
                if (callback != null) {
                    if (callback != null) {
                        callback.onShakeDetected(totalAngleX, totalAngleY, totalAngleZ, acceleration);
                    }

                    // reset for next shake
                    totalAngleX = totalAngleY = totalAngleZ = 0f;
                }
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}

