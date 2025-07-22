package com.skysoftsolution.basictoadavance.omrSheet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.skysoftsolution.basictoadavance.R;
import com.skysoftsolution.basictoadavance.apiClasses.ApiRequest;
import com.skysoftsolution.basictoadavance.apiClasses.RetrofitRequest;
import com.skysoftsolution.basictoadavance.databinding.ActivityOmrsheetBinding;
import com.skysoftsolution.basictoadavance.omrSheet.entity.OrgQualCourse;
import com.skysoftsolution.basictoadavance.omrSheet.helper.OMRSheetGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OMRSheetActivity extends AppCompatActivity {
    private ActivityOmrsheetBinding binding;
    private OMRSheetGenerator omrSheetGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOmrsheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getDateWiseReport();

    }
    private void getDateWiseReport() {
        ProgressDialog progressDialog1 = new ProgressDialog(OMRSheetActivity.this);
        progressDialog1.setMessage("Loading...");
        progressDialog1.setCancelable(false);
        progressDialog1.show();
        ApiRequest apiRequest = RetrofitRequest.getRetrofitInstancenew().create(ApiRequest.class);
        Call<List<OrgQualCourse>> call = apiRequest.GetClassMasterDataThreeToFive();
        call.enqueue(new Callback<List<OrgQualCourse>>() {
            @Override
            public void onResponse(Call<List<OrgQualCourse>> call, Response<List<OrgQualCourse>> response) {
                progressDialog1.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    List<OrgQualCourse> ptcResponse = response.body();
                    if (ptcResponse != null && ptcResponse.size()>0) {

                    } else {

                    }
                } else {
                    Toast.makeText(OMRSheetActivity.this, "Error : " + response.code(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<OrgQualCourse>> call, Throwable t) {
                progressDialog1.dismiss();
                Toast.makeText(OMRSheetActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}