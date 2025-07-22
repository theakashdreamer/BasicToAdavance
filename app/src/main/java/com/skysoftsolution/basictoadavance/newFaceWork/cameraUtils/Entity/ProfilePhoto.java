package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.Entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class ProfilePhoto implements Serializable {
    public String KGBVPhoto_ID ;
    public String KGBVPhoto_ProfileID ;
    public String KGBVPhoto_PhotoName ;
    public String KGBVPhoto_FilePath ;
    public String KGBVPhoto_Lattitude ;
    public String KGBVPhoto_Longitude ;
    public String KGBVPhoto_ClickedAT ;
    public String KGBVPhoto_Embeedings ;
    private String IsSynced;

    public String getKGBVRandomId() {
        return KGBVRandomId;
    }

    public void setKGBVRandomId(String KGBVRandomId) {
        this.KGBVRandomId = KGBVRandomId;
    }

    public String KGBVRandomId;

    public String getIsSynced() {
        return IsSynced;
    }

    public void setIsSynced(String isSynced) {
        IsSynced = isSynced;
    }

    public String getKGBVPhoto_ID() {
        return KGBVPhoto_ID;
    }

    public void setKGBVPhoto_ID(String KGBVPhoto_ID) {
        this.KGBVPhoto_ID = KGBVPhoto_ID;
    }

    public String getKGBVPhoto_ProfileID() {
        return KGBVPhoto_ProfileID;
    }

    public void setKGBVPhoto_ProfileID(String KGBVPhoto_ProfileID) {
        this.KGBVPhoto_ProfileID = KGBVPhoto_ProfileID;
    }

    public String getKGBVPhoto_PhotoName() {
        return KGBVPhoto_PhotoName;
    }

    public void setKGBVPhoto_PhotoName(String KGBVPhoto_PhotoName) {
        this.KGBVPhoto_PhotoName = KGBVPhoto_PhotoName;
    }

    public String getKGBVPhoto_FilePath() {
        return KGBVPhoto_FilePath;
    }

    public void setKGBVPhoto_FilePath(String KGBVPhoto_FilePath) {
        this.KGBVPhoto_FilePath = KGBVPhoto_FilePath;
    }

    public String getKGBVPhoto_Lattitude() {
        return KGBVPhoto_Lattitude;
    }

    public void setKGBVPhoto_Lattitude(String KGBVPhoto_Lattitude) {
        this.KGBVPhoto_Lattitude = KGBVPhoto_Lattitude;
    }

    public String getKGBVPhoto_Longitude() {
        return KGBVPhoto_Longitude;
    }

    public void setKGBVPhoto_Longitude(String KGBVPhoto_Longitude) {
        this.KGBVPhoto_Longitude = KGBVPhoto_Longitude;
    }

    public String getKGBVPhoto_ClickedAT() {
        return KGBVPhoto_ClickedAT;
    }

    public void setKGBVPhoto_ClickedAT(String KGBVPhoto_ClickedAT) {
        this.KGBVPhoto_ClickedAT = KGBVPhoto_ClickedAT;
    }

    public String getKGBVPhoto_Embeedings() {
        return KGBVPhoto_Embeedings;
    }

    public void setKGBVPhoto_Embeedings(String KGBVPhoto_Embeedings) {
        this.KGBVPhoto_Embeedings = KGBVPhoto_Embeedings;
    }



    public static List<ProfilePhoto> createlstFromJson(String jsonStringFromServer) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ProfilePhoto>>() {
        }.getType();
        return gson.fromJson(jsonStringFromServer, type);
    }



    public static Profile createobjFromJson(String jsonStringFromServer) {
        Gson gson = new Gson();
        Type type = new TypeToken<ProfilePhoto>() {
        }.getType();
        return gson.fromJson(jsonStringFromServer, type);
    }

    public static String getJsonFromList(List<ProfilePhoto> tbl_answerssheet) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ProfilePhoto>>() {
        }.getType();
        return gson.toJson(tbl_answerssheet, type);
    }



}
