package com.skysoftsolution.basictoadavance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.skysoftsolution.basictoadavance.SensorDetector.SensorFragment;
import com.skysoftsolution.basictoadavance.databinding.ActivitySensorActivityBinding;

public class SensorActivityActivity extends AppCompatActivity implements SensorFragment.OnShakeDetectedListener {

    private ActivitySensorActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySensorActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentContainer.getId(), new SensorFragment())
                .commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startLockTask();
        }
    }

    @Override
    public void onShakeDetected(float angleX, float angleY, float angleZ, double shakeStrength) {
        int progress;
        String level;

        if (shakeStrength < 10) {
            progress = 0; level = "High"; // Very sensitive
        } else if (shakeStrength < 15) {
            progress = 1; level = "Medium";
        } else {
            progress = 2; level = "Low"; // Only strong shakes
        }

        binding.sensitivitySeekBar.setProgress(progress);
        binding.statusTextView.setText("Auto Sensitivity: " + level+"shakeStrength"+shakeStrength);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Cheating detected! Closing test.", Toast.LENGTH_SHORT).show();
        stopLockTask(); // unlock before finish
    }
}
