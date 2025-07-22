package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.Entity;

import java.io.Serializable;
import java.util.List;

public class Teacher implements Serializable {


    public  String IsSynced;

    public String getIsSynced() {
        return IsSynced;
    }

    public void setIsSynced(String isSynced) {
        IsSynced = isSynced;
    }

    public String getKGBVProfile_IsVerfied() {
        return KGBVProfile_IsVerfied;
    }

    public void setKGBVProfile_IsVerfied(String KGBVProfile_IsVerfied) {
        this.KGBVProfile_IsVerfied = KGBVProfile_IsVerfied;
    }

    private String KGBVProfile_IsVerfied;

    public int getCommonIncrementID() {
        return commonIncrementID;
    }

    public void setCommonIncrementID(int commonIncrementID) {
        this.commonIncrementID = commonIncrementID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherName_E() {
        return teacherName_E;
    }

    public void setTeacherName_E(String teacherName_E) {
        this.teacherName_E = teacherName_E;
    }

    public String getTeacherName_H() {
        return teacherName_H;
    }

    public void setTeacherName_H(String teacherName_H) {
        this.teacherName_H = teacherName_H;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    int commonIncrementID;
    String teacherID;
    String teacherName_E;
    String teacherName_H;
    String schoolCode;
    String schoolId;
    String teacherMobile;
    String teacherDesignation;
    int attendancereasonid;

    String KGBVPhoto_FilePath;

    public String getKGBVPhoto_FilePath() {
        return KGBVPhoto_FilePath;
    }

    public void setKGBVPhoto_FilePath(String KGBVPhoto_FilePath) {
        this.KGBVPhoto_FilePath = KGBVPhoto_FilePath;
    }

    public String getIs_Checked() {
        return Is_Checked;
    }

    public void setIs_Checked(String is_Checked) {
        Is_Checked = is_Checked;
    }

    String Is_Checked;
    public int getAttendancereasonid() {
        return attendancereasonid;
    }

    public void setAttendancereasonid(int attendancereasonid) {
        this.attendancereasonid = attendancereasonid;
    }

    public String getTeacherDesignation() {
        return teacherDesignation;
    }

    public void setTeacherDesignation(String teacherDesignation) {
        this.teacherDesignation = teacherDesignation;
    }

    public String getAttendanceReason() {
        return attendanceReason;
    }

    public void setAttendanceReason(String attendanceReason) {
        this.attendanceReason = attendanceReason;
    }

    String attendanceReason;

    public String getTeacherMobile() {
        return teacherMobile;
    }

    public void setTeacherMobile(String teacherMobile) {
        this.teacherMobile = teacherMobile;
    }

    public String getDesignationID() {
        return DesignationID;
    }

    public void setDesignationID(String designationID) {
        DesignationID = designationID;
    }

    String DesignationID;

    public String classID;
    public String subjectID;
    public String IsSankulAdhikari;
    public String SubjectName;
    public String ClassName;

    public float left;
    public float top;
    public float right;
    public float bottom;
    private Integer color;

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

    public String getEmbeedings() {
        return embeedings;
    }

    public void setEmbeedings(String embeedings) {
        this.embeedings = embeedings;
    }

    public String getTeacherPresentOrAbsent() {
        return TeacherPresentOrAbsent;
    }

    public void setTeacherPresentOrAbsent(String teacherPresentOrAbsent) {
        TeacherPresentOrAbsent = teacherPresentOrAbsent;
    }

    public String getCurrentDate() {
        return CurrentDate;
    }

    public void setCurrentDate(String currentDate) {
        CurrentDate = currentDate;
    }

    private Float distance;
    private String title;
    private String id;
    private String embeedings;

    public List<String> getLstEmbeedings() {
        return LstEmbeedings;
    }

    public void setLstEmbeedings(List<String> lstEmbeedings) {
        LstEmbeedings = lstEmbeedings;
    }

    private List<String> LstEmbeedings;
    private String TeacherPresentOrAbsent;
    private String CurrentDate;
    private String Assign;
    private String StartDate;
    private String EndDate;

    public String getAssign() {
        return Assign;
    }

    public void setAssign(String assign) {
        Assign = assign;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getIsSankulAdhikari() {
        return IsSankulAdhikari;
    }

    public void setIsSankulAdhikari(String isSankulAdhikari) {
        IsSankulAdhikari = isSankulAdhikari;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

}