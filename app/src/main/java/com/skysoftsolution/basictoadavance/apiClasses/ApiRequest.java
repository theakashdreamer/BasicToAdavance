package com.skysoftsolution.basictoadavance.apiClasses;
import com.skysoftsolution.basictoadavance.omrSheet.entity.OrgQualCourse;
import com.skysoftsolution.basictoadavance.omrSheet.entity.Subject;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiRequest {
    @GET("SubjectLession?mode=GetClassMasterDataThreeToFive")
    Call<List<OrgQualCourse>> GetClassMasterDataThreeToFive();

    @GET("SubjectLession?mode=GetSubjectsAccToClassId")
    Call<List<Subject>> GetSubjectsAccToClassId(@Query("ClassID") String ClassID);

}