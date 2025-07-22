package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.Entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Profile implements Serializable {

    public String KGBVProfile_ID;
    public String KGBVProfile_PersonID;
    public String KGBVProfile_DessignationID;
    public String KGBVProfile_SchoolCode;
    public String KGBVProfile_name;
    public String KGBVProfile_IsActive;
    public String KGBVProfile_Date;
    public String KGBVProfile_IsLast;
    public String KGBVProfile_AddedBy;


    public String getKGBVProfile_IsSkip() {
        return KGBVProfile_IsSkip;
    }

    public void setKGBVProfile_IsSkip(String KGBVProfile_IsSkip) {
        this.KGBVProfile_IsSkip = KGBVProfile_IsSkip;
    }

    public String KGBVProfile_IsSkip;





    public String getResultString() {
        return ResultString;
    }

    public void setResultString(String resultString) {
        ResultString = resultString;
    }

    public String ResultString;


    public List<ProfilePhoto> lstPhoto;

    public List<ProfilePhoto> getLstPhoto() {
        return lstPhoto;
    }

    public void setLstPhoto(List<ProfilePhoto> lstPhoto) {
        this.lstPhoto = lstPhoto;
    }

    public String getIsSynced() {
        return IsSynced;
    }

    public void setIsSynced(String isSynced) {
        IsSynced = isSynced;
    }

    private String IsSynced;

    List<ProfilePhoto> profilePhotos = new ArrayList<>();

    public List<ProfilePhoto> getProfilePhotos() {
        return profilePhotos;
    }

    public void setProfilePhotos(List<ProfilePhoto> profilePhotos) {
        this.profilePhotos = profilePhotos;
    }

    public String getKGBVProfile_AddedBy() {
        return KGBVProfile_AddedBy;
    }

    public void setKGBVProfile_AddedBy(String KGBVProfile_AddedBy) {
        this.KGBVProfile_AddedBy = KGBVProfile_AddedBy;
    }

    public String getKGBVProfile_ID() {
        return KGBVProfile_ID;
    }

    public void setKGBVProfile_ID(String KGBVProfile_ID) {
        this.KGBVProfile_ID = KGBVProfile_ID;
    }

    public String getKGBVProfile_PersonID() {
        return KGBVProfile_PersonID;
    }

    public void setKGBVProfile_PersonID(String KGBVProfile_PersonID) {
        this.KGBVProfile_PersonID = KGBVProfile_PersonID;
    }

    public String getKGBVProfile_DessignationID() {
        return KGBVProfile_DessignationID;
    }

    public void setKGBVProfile_DessignationID(String KGBVProfile_DessignationID) {
        this.KGBVProfile_DessignationID = KGBVProfile_DessignationID;
    }

    public String getKGBVProfile_SchoolCode() {
        return KGBVProfile_SchoolCode;
    }

    public void setKGBVProfile_SchoolCode(String KGBVProfile_SchoolCode) {
        this.KGBVProfile_SchoolCode = KGBVProfile_SchoolCode;
    }

    public String getKGBVProfile_name() {
        return KGBVProfile_name;
    }

    public void setKGBVProfile_name(String KGBVProfile_name) {
        this.KGBVProfile_name = KGBVProfile_name;
    }

    public String getKGBVProfile_IsActive() {
        return KGBVProfile_IsActive;
    }

    public void setKGBVProfile_IsActive(String KGBVProfile_IsActive) {
        this.KGBVProfile_IsActive = KGBVProfile_IsActive;
    }

    public String getKGBVProfile_Date() {
        return KGBVProfile_Date;
    }

    public void setKGBVProfile_Date(String KGBVProfile_Date) {
        this.KGBVProfile_Date = KGBVProfile_Date;
    }

    public String getKGBVProfile_IsLast() {
        return KGBVProfile_IsLast;
    }

    public void setKGBVProfile_IsLast(String KGBVProfile_IsLast) {
        this.KGBVProfile_IsLast = KGBVProfile_IsLast;
    }


    public static List<Profile> createlstFromJson(String jsonStringFromServer) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Profile>>() {
        }.getType();
        return gson.fromJson(jsonStringFromServer, type);
    }


    public static Profile createobjFromJson(String jsonStringFromServer) {
        Gson gson = new Gson();
        Type type = new TypeToken<Profile>() {
        }.getType();
        return gson.fromJson(jsonStringFromServer, type);
    }


    public static String converttbl_ProfilePhotoDatatoJson(List<Profile> profile) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Profile>>() {
        }.getType();
        return gson.toJson(profile, type);
    }

}
