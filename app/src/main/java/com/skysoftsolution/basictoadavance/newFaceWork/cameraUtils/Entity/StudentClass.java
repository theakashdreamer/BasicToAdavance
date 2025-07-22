package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.Entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class StudentClass implements Serializable {
    private String CommonId;
    private String OrgQualCourse_Id;
    private String OrgQualCourse_Title;

    public static List<StudentClass> getListofStudentClassFromServerJson(String serverJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<StudentClass>>() {
        }.getType();
        return gson.fromJson(serverJson, type);
    }

    public String getCommonId() {
        return CommonId;
    }

    public void setCommonId(String commonId) {
        CommonId = commonId;
    }

    public String getOrgQualCourse_Id() {
        return OrgQualCourse_Id;
    }

    public void setOrgQualCourse_Id(String orgQualCourse_Id) {
        OrgQualCourse_Id = orgQualCourse_Id;
    }

    public String getOrgQualCourse_Title() {
        return OrgQualCourse_Title;
    }

    public void setOrgQualCourse_Title(String orgQualCourse_Title) {
        OrgQualCourse_Title = orgQualCourse_Title;
    }

}
