package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.Entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClassWiseAttendence implements Serializable {
    private String Common_Id;
    private String SessionID;
    private String CurSession;
    private String ClassId;
    private String Attendance_ID;
    private String AttendenceDate;
    private String Gender;
    private String PSandUPSId;
    private String CategoryId;
    private String School_Code;
    private String StudentRegistrationNumber;
    private String SRNumber;
    private String Father_Name;
    private String Mother_Name;
    private String Student_Name;
    private String DateOfBirth;
    private String Area;
    private String StatusAction;
    private String PresentOrAbsent;
    private String AbsentReason;
    private String IsSync;
    private String IsSyncDate;
    private String AttendanceMode;
    private String StudentID;
    private String Attendance_P02Person_ID;
    private String filePath;
    private String Student_in_Out;
    private  String InOut_ID;

    public String getInOut_ID() {
        return InOut_ID;
    }

    public void setInOut_ID(String inOut_ID) {
        InOut_ID = inOut_ID;
    }

    public String getStudent_in_Out() {
        return Student_in_Out;
    }

    public void setStudent_in_Out(String student_in_Out) {
        Student_in_Out = student_in_Out;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getLstEmbeedings() {
        return LstEmbeedings;
    }

    public void setLstEmbeedings(List<String> lstEmbeedings) {
        LstEmbeedings = lstEmbeedings;
    }

    private List<String> LstEmbeedings;

    public String getPresentOrAbsentDate() {
        return PresentOrAbsentDate;
    }

    public void setPresentOrAbsentDate(String presentOrAbsentDate) {
        PresentOrAbsentDate = presentOrAbsentDate;
    }

    private String PresentOrAbsentDate;

    public String getAdded_by() {
        return Added_by;
    }

    public void setAdded_by(String added_by) {
        Added_by = added_by;
    }

    private String Lattitude;
    private String Longitude;
    private String Added_On;
    private String Added_by;
    private String AttendanceDateTime;
    private List<ClassWiseAttendence> lstClassWiseAttendance = new ArrayList<>();

    public List<ClassWiseAttendence> getLstStudentPresentOrAbsent() {
        return lstStudentPresentOrAbsent;
    }

    public void setLstStudentPresentOrAbsent(List<ClassWiseAttendence> lstStudentPresentOrAbsent) {
        this.lstStudentPresentOrAbsent = lstStudentPresentOrAbsent;
    }

    private List<ClassWiseAttendence> lstStudentPresentOrAbsent = new ArrayList<>();

    public List<ClassWiseAttendence> getLstClassWiseAttendance() {
        return lstClassWiseAttendance;
    }

    public void setLstClassWiseAttendance(List<ClassWiseAttendence> lstClassWiseAttendance) {
        this.lstClassWiseAttendance = lstClassWiseAttendance;
    }

    /////////////

    public float left;
    public float top;
    public float right;
    public float bottom;
    private Integer color;
    private Float distance;
    private String title;
    private String id;
    private String Embeedings;

    public String getEmbeedings() {
        return Embeedings;
    }

    public void setEmbeedings(String embeedings) {
        Embeedings = embeedings;
    }

    private String StudentPresentOrAbsent;
    private String CurrentDate;

    public String getStudentPresentOrAbsent() {
        return StudentPresentOrAbsent;
    }

    public void setStudentPresentOrAbsent(String studentPresentOrAbsent) {
        StudentPresentOrAbsent = studentPresentOrAbsent;
    }

    public String getCurrentDate() {
        return CurrentDate;
    }

    public void setCurrentDate(String currentDate) {
        CurrentDate = currentDate;
    }


    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAttendanceDateTime() {
        return AttendanceDateTime;
    }

    public void setAttendanceDateTime(String attendanceDateTime) {
        AttendanceDateTime = attendanceDateTime;
    }

    public static String getJsonStringFromClassWiseAttendenceList(List<ClassWiseAttendence> lstClassEnrollment) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ClassWiseAttendence>>() {
        }.getType();
        return gson.toJson(lstClassEnrollment, type);
    }

    public static ClassWiseAttendence getJsonStringFromClassWiseAttendenceList(String lstClassEnrollment) {
        Gson gson = new Gson();
        Type type = new TypeToken<ClassWiseAttendence>() {
        }.getType();
        return gson.fromJson(lstClassEnrollment, type);
    }

    public static ClassWiseAttendence getOBJofStudentFromServerJson(String serverJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<ClassWiseAttendence>() {
        }.getType();
        return gson.fromJson(serverJson, type);
    }

    public static List<ClassWiseAttendence> getListofStudentFromServerJson(String serverJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ClassWiseAttendence>>() {
        }.getType();
        return gson.fromJson(serverJson, type);
    }

    public String getAttendance_ID() {
        return Attendance_ID;
    }

    public void setAttendance_ID(String attendance_ID) {
        Attendance_ID = attendance_ID;
    }


    public String getAdded_On() {
        return Added_On;
    }

    public void setAdded_On(String added_On) {
        Added_On = added_On;
    }

    public String getAttendenceDate() {
        return AttendenceDate;
    }

    public void setAttendenceDate(String attendenceDate) {
        AttendenceDate = attendenceDate;
    }

    public String getCommon_Id() {
        return Common_Id;
    }

    public void setCommon_Id(String common_Id) {
        Common_Id = common_Id;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public String getCurSession() {
        return CurSession;
    }

    public void setCurSession(String curSession) {
        CurSession = curSession;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getPSandUPSId() {
        return PSandUPSId;
    }

    public void setPSandUPSId(String PSandUPSId) {
        this.PSandUPSId = PSandUPSId;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getSchool_Code() {
        return School_Code;
    }

    public void setSchool_Code(String school_Code) {
        School_Code = school_Code;
    }

    public String getStudentRegistrationNumber() {
        return StudentRegistrationNumber;
    }

    public void setStudentRegistrationNumber(String studentRegistrationNumber) {
        StudentRegistrationNumber = studentRegistrationNumber;
    }

    public String getSRNumber() {
        return SRNumber;
    }

    public void setSRNumber(String SRNumber) {
        this.SRNumber = SRNumber;
    }

    public String getFather_Name() {
        return Father_Name;
    }

    public void setFather_Name(String father_Name) {
        Father_Name = father_Name;
    }

    public String getMother_Name() {
        return Mother_Name;
    }

    public void setMother_Name(String mother_Name) {
        Mother_Name = mother_Name;
    }

    public String getStudent_Name() {
        return Student_Name;
    }

    public void setStudent_Name(String student_Name) {
        Student_Name = student_Name;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getStatusAction() {
        return StatusAction;
    }

    public void setStatusAction(String statusAction) {
        StatusAction = statusAction;
    }

    public String getPresentOrAbsent() {
        return PresentOrAbsent;
    }

    public void setPresentOrAbsent(String presentOrAbsent) {
        PresentOrAbsent = presentOrAbsent;
    }

    public String getAbsentReason() {
        return AbsentReason;
    }

    public void setAbsentReason(String absentReason) {
        AbsentReason = absentReason;
    }

    public String getIsSync() {
        return IsSync;
    }

    public void setIsSync(String isSync) {
        IsSync = isSync;
    }

    public String getIsSyncDate() {
        return IsSyncDate;
    }

    public void setIsSyncDate(String isSyncDate) {
        IsSyncDate = isSyncDate;
    }

    public String getAttendanceMode() {
        return AttendanceMode;
    }

    public void setAttendanceMode(String attendanceMode) {
        AttendanceMode = attendanceMode;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getAttendance_P02Person_ID() {
        return Attendance_P02Person_ID;
    }

    public void setAttendance_P02Person_ID(String attendance_P02Person_ID) {
        Attendance_P02Person_ID = attendance_P02Person_ID;
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
}
