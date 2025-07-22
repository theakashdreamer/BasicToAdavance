package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.Entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class AcademicYear implements Serializable {
    private String Common_Id;
    private String AY_Id;
    private String AY_TextToDisplay;
    private String AY_StartDate;
    private String AY_EndDate;
    private String AY_IsCurrentAY;

    public String getCommon_Id() {
        return Common_Id;
    }

    public void setCommon_Id(String common_Id) {
        Common_Id = common_Id;
    }

    public String getAY_Id() {
        return AY_Id;
    }

    public void setAY_Id(String AY_Id) {
        this.AY_Id = AY_Id;
    }

    public String getAY_TextToDisplay() {
        return AY_TextToDisplay;
    }

    public void setAY_TextToDisplay(String AY_TextToDisplay) {
        this.AY_TextToDisplay = AY_TextToDisplay;
    }

    public String getAY_StartDate() {
        return AY_StartDate;
    }

    public void setAY_StartDate(String AY_StartDate) {
        this.AY_StartDate = AY_StartDate;
    }

    public String getAY_EndDate() {
        return AY_EndDate;
    }

    public void setAY_EndDate(String AY_EndDate) {
        this.AY_EndDate = AY_EndDate;
    }

    public String getAY_IsCurrentAY() {
        return AY_IsCurrentAY;
    }

    public void setAY_IsCurrentAY(String AY_IsCurrentAY) {
        this.AY_IsCurrentAY = AY_IsCurrentAY;
    }

    public static List<AcademicYear> getListofAcademicYearFromServerJson(String serverJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<AcademicYear>>() {
        }.getType();
        return gson.fromJson(serverJson, type);
    }
}
