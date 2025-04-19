package com.skysoftsolution.basictoadavance.omrSheet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.skysoftsolution.basictoadavance.R;
import com.skysoftsolution.basictoadavance.databinding.ActivityOmrsheetBinding;
import com.skysoftsolution.basictoadavance.omrSheet.helper.OMRSheetGenerator;

public class OMRSheetActivity extends AppCompatActivity {
    private ActivityOmrsheetBinding binding;
    private OMRSheetGenerator omrSheetGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOmrsheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        omrSheetGenerator = new OMRSheetGenerator(this);
        binding.btnGenerateOMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                omrSheetGenerator.generateOMRSheet();
            }
        });
    }
}