package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.Entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class TblAttendance implements Serializable {
    private String Common_Id;
    private String SessionID;
    private String Attendance_ID;
    private String AttendanceMode;
    private String ClassId;
    private String AttendenceDate;
    private String StudentID;
    private String Lattitude;
    private String Longitude;
    private String Added_On;
    private String Added_By;
    private String School_Code;
    private String IsDataSync;
    private String IsPhotoSync;
    private String DataSyncDate;
    private String PhotoSyncDate;
    private String Attendance_P02Person_ID;
    private List<ClassWiseAttendence> lstClassWiseAttendance;
  //  private List<ClassWiseAttendencePhotos> lstClassWiseAttendancePhoto;

    public static TblAttendance getObjectOfClassWiseAttendanceFromServerJson(String returnData) {
        Gson gson = new Gson();
        Type type = new TypeToken<TblAttendance>() {
        }.getType();
        return gson.fromJson(returnData, type);
    }


    public static String getJsonStringFromClassWiseAttendenceList(List<TblAttendance> lstClassEnrollment) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<TblAttendance>>() {
        }.getType();
        return gson.toJson(lstClassEnrollment, type);
    }

    public static List<TblAttendance> getListofStudentFromServerJson(String serverJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<TblAttendance>>() {
        }.getType();
        return gson.fromJson(serverJson, type);
    }

    public String getCommon_Id() {
        return Common_Id;
    }

    public void setCommon_Id(String common_Id) {
        Common_Id = common_Id;

    }

    public String getAttendanceMode() {
        return AttendanceMode;
    }

    public void setAttendanceMode(String attendanceMode) {
        AttendanceMode = attendanceMode;
    }

    public String getAttendance_ID() {
        return Attendance_ID;
    }

    public void setAttendance_ID(String attendance_ID) {
        Attendance_ID = attendance_ID;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public String getAttendance_P02Person_ID() {
        return Attendance_P02Person_ID;
    }

    public void setAttendance_P02Person_ID(String attendance_P02Person_ID) {
        Attendance_P02Person_ID = attendance_P02Person_ID;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getAttendenceDate() {
        return AttendenceDate;
    }

    public void setAttendenceDate(String attendenceDate) {
        AttendenceDate = attendenceDate;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getLattitude() {
        return Lattitude;
    }

    public void setLattitude(String lattitude) {
        Lattitude = lattitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getAdded_On() {
        return Added_On;
    }

    public void setAdded_On(String added_On) {
        Added_On = added_On;
    }

    public String getAdded_By() {
        return Added_By;
    }

    public void setAdded_By(String added_By) {
        Added_By = added_By;
    }

    public String getSchool_Code() {
        return School_Code;
    }

    public void setSchool_Code(String school_Code) {
        School_Code = school_Code;
    }

    public String getIsDataSync() {
        return IsDataSync;
    }

    public void setIsDataSync(String isDataSync) {
        IsDataSync = isDataSync;
    }

    public String getIsPhotoSync() {
        return IsPhotoSync;
    }

    public void setIsPhotoSync(String isPhotoSync) {
        IsPhotoSync = isPhotoSync;
    }

    public String getDataSyncDate() {
        return DataSyncDate;
    }

    public void setDataSyncDate(String dataSyncDate) {
        DataSyncDate = dataSyncDate;
    }

    public String getPhotoSyncDate() {
        return PhotoSyncDate;
    }

    public void setPhotoSyncDate(String photoSyncDate) {
        PhotoSyncDate = photoSyncDate;
    }

    public List<ClassWiseAttendence> getLstClassWiseAttendance() {
        return lstClassWiseAttendance;
    }

    public void setLstClassWiseAttendance(List<ClassWiseAttendence> lstClassWiseAttendance) {
        this.lstClassWiseAttendance = lstClassWiseAttendance;
    }

   /* public List<ClassWiseAttendencePhotos> getLstClassWiseAttendancePhoto() {
        return lstClassWiseAttendancePhoto;
    }

    public void setLstClassWiseAttendancePhoto(List<ClassWiseAttendencePhotos> lstClassWiseAttendancePhoto) {
        this.lstClassWiseAttendancePhoto = lstClassWiseAttendancePhoto;
    }*/
}