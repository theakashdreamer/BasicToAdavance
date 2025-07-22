package com.skysoftsolution.basictoadavance.omrSheet.entity;

import java.io.Serializable;

public class Subject implements Serializable {
    private String Subjects_Id;
    private String Subjects_Name;
    private String Subjects_HName;
    private String ClassId;

    public String getSubjects_Id() {
        return Subjects_Id;
    }

    public void setSubjects_Id(String subjects_Id) {
        Subjects_Id = subjects_Id;
    }

    public String getSubjects_Name() {
        return Subjects_Name;
    }

    public void setSubjects_Name(String subjects_Name) {
        Subjects_Name = subjects_Name;
    }

    public String getSubjects_HName() {
        return Subjects_HName;
    }

    public void setSubjects_HName(String subjects_HName) {
        Subjects_HName = subjects_HName;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }
}
