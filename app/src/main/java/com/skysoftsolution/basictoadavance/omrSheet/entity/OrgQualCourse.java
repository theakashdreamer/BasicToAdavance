package com.skysoftsolution.basictoadavance.omrSheet.entity;

import java.io.Serializable;

public class OrgQualCourse implements Serializable {
    private String OrgQualCourse_Id;
    private String OrgQualCourse_Title;
    private String OrgQualCourse_HName;
    private String OrgQualCourse_Description;
    private String Class_Name;
    private String ULP_ClassName;

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

    public String getOrgQualCourse_HName() {
        return OrgQualCourse_HName;
    }

    public void setOrgQualCourse_HName(String orgQualCourse_HName) {
        OrgQualCourse_HName = orgQualCourse_HName;
    }

    public String getOrgQualCourse_Description() {
        return OrgQualCourse_Description;
    }

    public void setOrgQualCourse_Description(String orgQualCourse_Description) {
        OrgQualCourse_Description = orgQualCourse_Description;
    }

    public String getClass_Name() {
        return Class_Name;
    }

    public void setClass_Name(String class_Name) {
        Class_Name = class_Name;
    }

    public String getULP_ClassName() {
        return ULP_ClassName;
    }

    public void setULP_ClassName(String ULP_ClassName) {
        this.ULP_ClassName = ULP_ClassName;
    }
}
